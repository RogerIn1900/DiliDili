package com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo

import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebView(url:String  ){

    // Declare a string that contains a url
    val mUrl = "https://www.google.com"
    val biliUrl = "https://www.bilibili.com"
    val TAG = "mWebView"
    Log.d(TAG,"url\n"+url)
    Log.d(TAG,"mUrl\n"+mUrl)
    Log.d(TAG,"biliUrl\n"+biliUrl)


    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // 启用浏览器必要的设置
                settings.javaScriptEnabled = true              // 允许 JS
                settings.domStorageEnabled = true              // 允许本地存储（很多网站需要）
                settings.mediaPlaybackRequiresUserGesture = false // 允许自动播放视频
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                // 🔑 页面缩放 & 自适应
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.setSupportZoom(true)

                // WebViewClient 保证在本 WebView 内加载
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return false // 所有跳转都在 WebView 内部完成
                    }
                }

                // WebChromeClient 处理网页里的 JS 对话框 / 视频全屏
                webChromeClient = WebChromeClient()

                var testUrl = "https://player.bilibili.com/player.html?aid=114360349362493&cid=29489891654&page=1"
                Log.d(TAG,"url"+url)
                loadUrl(url)
            }
        },
        //update保证url更新之后会重新加载
        update = { webView ->
            Log.d(TAG,"Loading URL: $url")
            webView.loadUrl(url)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .aspectRatio(16f/9f)
    )

}

class CustomWebViewClient: WebViewClient(){
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if(url != null && url.startsWith("https://google.com")){
            return true
        }
        return false
    }
}
