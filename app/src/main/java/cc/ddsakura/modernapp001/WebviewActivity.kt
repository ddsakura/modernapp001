package cc.ddsakura.modernapp001

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewClientCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// NOTE: Add coroutine dependency to app/build.gradle.kts:
// implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Use the latest version

class WebviewActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

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

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val webView = v as WebView
        val result = webView.hitTestResult
        if (result.type == WebView.HitTestResult.IMAGE_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            result.extra?.let { imageUrl ->
                menu.setHeaderTitle("Image Options")
                val isDataUri = imageUrl.startsWith("data:")

                menu.add(0, 1, 0, "Save Image").setOnMenuItemClickListener {
                    if (isDataUri) {
                        handleDataUriSave(imageUrl)
                    } else {
                        handleNetworkUrlSave(imageUrl)
                    }
                    true
                }

                menu.add(0, 2, 0, "View Image").apply {
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

    private fun handleDataUriSave(imageUrl: String) {
        val base64Data = parseBase64FromDataUri(imageUrl)
        if (base64Data != null) {
            launch {
                val imageBytes = withContext(Dispatchers.Default) {
                    Base64.decode(base64Data, Base64.DEFAULT)
                }
                saveImageBytesToGallery(this@WebviewActivity, imageBytes)
            }
        } else {
            Toast.makeText(applicationContext, "Unsupported data URI", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNetworkUrlSave(imageUrl: String) {
        val request = DownloadManager.Request(imageUrl.toUri())
        val fileName = URLUtil.guessFileName(imageUrl, null, MimeTypeMap.getFileExtensionFromUrl(imageUrl))
        request.setTitle(fileName)
        request.setDescription("Downloading...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(applicationContext, "Downloading Image...", Toast.LENGTH_SHORT).show()
    }

    private fun parseBase64FromDataUri(uri: String): String? {
        val commaIndex = uri.indexOf(',')
        if (commaIndex != -1) {
            if (uri.substring(0, commaIndex).contains("base64", ignoreCase = true)) {
                return uri.substring(commaIndex + 1)
            }
        }
        return null
    }

    // NOTE: This requires WRITE_EXTERNAL_STORAGE permission on Android 9 (API 28) and below.
    private suspend fun saveImageBytesToGallery(context: Context, imageBytes: ByteArray) {
        val success = withContext(Dispatchers.IO) { // Switch to IO thread for file operations
            try {
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val displayName = "Image_${System.currentTimeMillis()}"
                val savedUri = MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap,
                    displayName,
                    "Image from WebView"
                )
                savedUri != null
            } catch (e: Exception) {
                Log.e("WebviewActivity", "Failed to save image", e)
                false
            }
        }
        // Switch back to Main thread to show Toast
        if (success) {
            Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
        }
    }
}

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
