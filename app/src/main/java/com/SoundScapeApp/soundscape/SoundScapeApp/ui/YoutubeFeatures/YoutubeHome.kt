package com.SoundScapeApp.soundscape.SoundScapeApp.ui.YoutubeFeatures

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@RequiresApi(Build.VERSION_CODES.ECLAIR_MR1)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubeHome() {

    Column(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
    ){
        val youtubeUrl = "https://www.youtube.com"

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.domStorageEnabled = true
                    settings.useWideViewPort = true
                    settings.builtInZoomControls = true

                    loadUrl(youtubeUrl)
                }
            }
        )
    }

}