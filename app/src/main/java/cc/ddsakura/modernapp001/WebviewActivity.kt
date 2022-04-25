package cc.ddsakura.modernapp001

import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewClientCompat

class WebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_webview)
        val myWebView = WebView(applicationContext)
        setContentView(myWebView)


        myWebView.settings.apply {
            javaScriptEnabled = true
            userAgentString = "${userAgentString} HelloUserAgent 0"
        }
        WebView.setWebContentsDebuggingEnabled(true)
        myWebView.webViewClient = MyWebViewClient()
        myWebView.webChromeClient = MyWebChromeClient()
        myWebView.loadUrl("https://www.google.com")
    }
}

private class MyWebViewClient : WebViewClientCompat() {

    var useragentIdx = 0

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        Log.d("WebviewActivity", "shouldOverrideUrlLoading url")
        return false
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        Log.d("WebviewActivity", "shouldOverrideUrlLoading WebResourceRequest: ${request.url} /  ${request.method}")
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {

        view?.post(Runnable {
            if ((view?.settings?.userAgentString?.contains("HelloUserAgent") ?: false) == false) {
                view?.settings?.userAgentString = "HelloUserAgent ${useragentIdx++}"
            }
        })

        Log.d("WebviewActivity", "shouldInterceptRequest WebResourceRequest: ${request?.url} /  ${request?.method}")
        return super.shouldInterceptRequest(view, request)
    }
}

private class MyWebChromeClient : WebChromeClient() {
    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        Log.d("WebviewActivity", message.message())
        return true
    }
}