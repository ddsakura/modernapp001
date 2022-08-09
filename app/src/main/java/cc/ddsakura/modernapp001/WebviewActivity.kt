package cc.ddsakura.modernapp001

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewClientCompat

class WebviewActivity : AppCompatActivity() {


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(applicationContext)
        setContentView(myWebView)

        myWebView.settings.apply {
            javaScriptEnabled = true
            userAgentString = "$userAgentString HelloUserAgent"
        }
        WebView.setWebContentsDebuggingEnabled(true)
        myWebView.webViewClient = MyWebViewClient()
        myWebView.webChromeClient = MyWebChromeClient()
        myWebView.loadUrl("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_date")

        // ref: https://codelabs.developers.google.com/handling-gesture-back-navigation
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    myWebView.canGoBack() -> myWebView.goBack()
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        disableOnBackPressedCallback(myWebView, onBackPressedCallback)
    }

    private fun disableOnBackPressedCallback(
        webView: WebView,
        onBackPressedCallback: OnBackPressedCallback
    ) {
        webView.webViewClient = object : WebViewClient() {
            // Use webView.canGoBack() to determine whether or not the OnBackPressedCallback is enabled.
            // if the callback is enabled, the app takes control and determines what to do. If the
            // callbacks is disabled; the back nav gesture will go back to the topmost activity/fragment
            // in the back stack.
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                onBackPressedCallback.isEnabled = webView.canGoBack()
            }
        }
    }


// Original function to handle back press
//    private lateinit var myWebView: WebView
//    override fun onBackPressed() {
//        if (myWebView.canGoBack())
//            myWebView.goBack()
//        else
//            super.onBackPressed()
//    }
}

private class MyWebViewClient : WebViewClientCompat() {

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        Log.d(
            "WebviewActivity",
            "shouldOverrideUrlLoading WebResourceRequest: ${request.url} /  ${request.method}"
        )
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {

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
}

private class MyWebChromeClient : WebChromeClient() {
    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        Log.d("WebviewActivity", message.message())
        return true
    }
}