package cc.ddsakura.modernapp001

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebViewClientCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class WebviewActivity : AppCompatActivity() {

    /**
     * 處理權限請求結果的啟動器
     */
    private var pendingSaveAction: (() -> Unit)? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 使用者授予權限後，執行被暫緩的儲存操作
            pendingSaveAction?.invoke()
            pendingSaveAction = null // 完成後清除
        } else {
            Toast.makeText(this, "Permission denied. Cannot save image.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        // 設置窗口插入監聽器以調整系統欄位的填充
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.my_app_bar_layout)
        ) { view, windowInsets ->
            // 獲取系統欄位（狀態欄、導航欄等）的插入
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 根據插入設置視圖的填充
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            // 表示插入已被消耗
            WindowInsetsCompat.CONSUMED
        }

        val myWebView = findViewById<WebView>(R.id.myWebView)

        // ref: https://codelabs.developers.google.com/handling-gesture-back-navigation
        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (myWebView.canGoBack()) {
                        myWebView.goBack()
                    }
                }
            }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        // 註冊 WebView 以便長按時可以顯示 ContextMenu (內容選單)
        registerForContextMenu(myWebView)
        myWebView.settings.apply {
            javaScriptEnabled = true
            userAgentString = "$userAgentString HelloUserAgent"
        }
        WebView.setWebContentsDebuggingEnabled(true)
        myWebView.webViewClient = MyWebViewClient(onBackPressedCallback)
        myWebView.webChromeClient = MyWebChromeClient()
        myWebView.loadUrl("https://www.google.com")
    }

    /**
     * 當 WebView 被長按並註冊過 ContextMenu 時，此方法會被呼叫
     * 我們在這裡建立圖片專屬的內容選單 (儲存/檢視)
     */
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val webView = v as WebView
        val result = webView.hitTestResult
        // 檢查長按的目標是否為圖片類型
        if (result.type == WebView.HitTestResult.IMAGE_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            result.extra?.let { imageUrl ->
                menu.setHeaderTitle("Image Options")
                // 判斷圖片來源是 data: URI 還是遠端 URL
                val isDataUri = imageUrl.startsWith("data:")

                // 選單項目：儲存圖片
                menu.add(0, 1, 0, "Save Image").setOnMenuItemClickListener {
                    if (isDataUri) {
                        // 若為 data: URI，走專門的解析和儲存流程
                        handleDataUriSave(imageUrl)
                    } else {
                        // 若為遠端 URL，使用 DownloadManager 下載
                        handleNetworkUrlSave(imageUrl)
                    }
                    true
                }

                // 選單項目：檢視圖片
                menu.add(0, 2, 0, "View Image").apply {
                    // data: URI 無法直接被外部應用程式開啟，故禁用此按鈕
                    isEnabled = !isDataUri
                    setOnMenuItemClickListener {
                        if (!isDataUri) {
                            val intent = Intent(Intent.ACTION_VIEW, imageUrl.toUri())
                            startActivity(intent)
                        }
                        true
                    }
                }
            }
        }
    }

    /**
     * 處理 data: URI 格式的圖片儲存
     */
    private fun handleDataUriSave(imageUrl: String) {
        val parsedData = parseDataUri(imageUrl)
        if (parsedData != null) {
            val (mimeType, base64Data) = parsedData
            // 將儲存邏輯定義為一個動作，並明確指定其類型為 () -> Unit
            val saveAction: () -> Unit = {
                // 使用 lifecycleScope.launch 啟動協程，能感知生命週期，避免記憶體洩漏
                lifecycleScope.launch {
                    val imageBytes = withContext(Dispatchers.Default) {
                        Base64.decode(base64Data, Base64.DEFAULT)
                    }
                    saveImageBytesToGallery(this@WebviewActivity, imageBytes, mimeType)
                }
            }
            // 執行儲存前，先檢查權限
            checkPermissionAndSave(saveAction)
        } else {
            Toast.makeText(applicationContext, "Unsupported data URI", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 處理遠端 URL 格式的圖片儲存，使用系統的 DownloadManager
     */
    private fun handleNetworkUrlSave(imageUrl: String) {
        val saveAction = { // 將儲存邏輯定義為一個動作
            val request = DownloadManager.Request(imageUrl.toUri())
            val originalFileName = URLUtil.guessFileName(imageUrl, null, MimeTypeMap.getFileExtensionFromUrl(imageUrl))
            val timestamp = System.currentTimeMillis()
            val fileName = originalFileName.substringBeforeLast('.')
            val fileExtension = originalFileName.substringAfterLast('.', "")
            // 加上時間戳確保每次儲存的檔名都獨一無二
            val uniqueFileName = if (fileExtension.isNotEmpty()) {
                "${fileName}_${timestamp}.$fileExtension"
            } else {
                "${fileName}_${timestamp}"
            }

            request.setTitle(uniqueFileName)
            request.setDescription("Downloading...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uniqueFileName)
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(applicationContext, "Downloading Image...", Toast.LENGTH_SHORT).show()
        }
        // 執行儲存前，先檢查權限
        checkPermissionAndSave(saveAction)
    }

    /**
     * 檢查儲存權限，如果具備權限則執行儲存動作，否則請求權限
     */
    private fun checkPermissionAndSave(action: () -> Unit) {
        // Android 10 (API 29) 以上，儲存到公有目錄不需申請 WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            action()
            return
        }

        // 對於 Android 9 (API 28) 及以下版本，檢查權限
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已有權限，直接執行
                action()
            }
            else -> {
                // 尚未授予權限，發起請求
                pendingSaveAction = action
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    /**
     * 解析 data: URI，回傳 MIME 類型和 Base64 編碼的資料
     * @return Pair(MIME類型, Base64資料) 或 null (如果格式不符)
     */
    private fun parseDataUri(uri: String): Pair<String, String>? {
        val commaIndex = uri.indexOf(',')
        if (commaIndex == -1) return null

        val metadata = uri.substring(0, commaIndex)
        val data = uri.substring(commaIndex + 1)

        if (!metadata.contains(";base64", ignoreCase = true)) return null

        val mimeType = metadata.substringAfter("data:").substringBefore(";")
        if (mimeType.isBlank()) return null

        return mimeType to data
    }

    /**
     * 將解碼後的圖片資料 (ByteArray) 儲存到系統相簿
     * 使用 suspend 關鍵字標記為協程的掛起函式
     */
    private suspend fun saveImageBytesToGallery(context: Context, imageBytes: ByteArray, mimeType: String) {
        // withContext(Dispatchers.IO) 將檔案讀寫操作切換到 IO 執行緒
        val success = withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "png"
            val displayName = "Image_${System.currentTimeMillis()}.$extension"

            // 使用 ContentResolver 和 ContentValues，這是 Android 10 (Q) 以上推薦的作法
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
            }

            val resolver = context.contentResolver
            var uri: Uri? = null
            try {
                val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                uri = resolver.insert(collection, contentValues)
                if (uri == null) throw IOException("Failed to create new MediaStore record.")

                resolver.openOutputStream(uri)?.use { stream ->
                    // 我們將解碼後的 bitmap 再壓縮。PNG 是個好的無損格式選擇。
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                        throw IOException("Failed to save bitmap.")
                    }
                }
                true // Success
            } catch (e: IOException) {
                uri?.let { resolver.delete(it, null, null) } // 如果發生錯誤，清除不完整的項目
                Log.e("WebviewActivity", "Failed to save image", e)
                false // Failure
            }
        }

        if (success) {
            Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * 自訂的 WebViewClient
 * @param onBackPressedCallback 用於處理返回手勢的回調，更新其啟用狀態
 */
private class MyWebViewClient(private val onBackPressedCallback: OnBackPressedCallback) : WebViewClientCompat() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        Log.d(
            "WebviewActivity",
            "shouldOverrideUrlLoading WebResourceRequest: ${request.url} /  ${request.method}"
        )
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        view?.post {
            if (view.settings.userAgentString?.contains("HelloUserAgent") != true) {
                view.settings.userAgentString = "HelloUserAgent"
            }
        }

        Log.d(
            "WebviewActivity",
            "shouldInterceptRequest WebResourceRequest: ${request?.url} /  ${request?.method}"
        )
        return super.shouldInterceptRequest(view, request)
    }

    /**
     * 當 WebView 的歷史紀錄更新時被呼叫
     * 我們在此根據 WebView 是否可以返回，來決定返回手勢的回調是否啟用
     */
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        onBackPressedCallback.isEnabled = view?.canGoBack() ?: false
    }
}

private class MyWebChromeClient : WebChromeClient() {
    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        Log.d("WebviewActivity", message.message())
        return true
    }
}
