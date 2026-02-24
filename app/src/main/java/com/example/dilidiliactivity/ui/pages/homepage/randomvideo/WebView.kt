package com.example.dilidiliactivity.ui.pages.homepage.randomvideo

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import timber.log.Timber

@Composable
fun WebView(url: String) {
    Timber.d("WebView url: %s", url)

    AndroidView(
        factory = { context ->
            val webViewStart = System.currentTimeMillis()
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.setSupportZoom(true)

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return false
                    }
                }

                webChromeClient = WebChromeClient()

                val webViewCost = System.currentTimeMillis() - webViewStart
                Timber.d("[Warmup-Baseline] WebView create + configure: %dms", webViewCost)

                loadUrl(url)
            }
        },
        update = { webView ->
            Timber.d("WebView loading URL: %s", url)
            webView.loadUrl(url)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .aspectRatio(16f / 9f)
    )
}

class CustomWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null && url.startsWith("https://google.com")) {
            return true
        }
        return false
    }
}
