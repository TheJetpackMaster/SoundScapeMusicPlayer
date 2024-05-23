package com.SoundScapeApp.soundscape

import android.app.NotificationManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.service.MusicService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.VideoPlayingScreen
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeColorScheme
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {

    private val videoViewModel: VideoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        handleWindowInsetsAndDecors(window = window)
        super.onCreate(savedInstanceState)


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )


        val videoUri = intent.data

        if (videoUri != null) {
            videoViewModel.onIntent(videoUri)
        }

        setContent {
            SoundScapeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                            .background(Color.Red)
                    ) {
                        VideoPlayingScreen(viewModel = videoViewModel,
                            navController = rememberNavController(),
                            onPipClick = {
                                enterPiPMode()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val videoUri = intent.data

        if (videoUri != null) {
            videoViewModel.onNewIntent(videoUri)
        }
    }


    companion object {

        const val TAG = "PlayerActivity"

        fun handleWindowInsetsAndDecors(window: Window) {

            WindowCompat.setDecorFitsSystemWindows(window, false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun enterPiPMode() {
        val aspectRatio = Rational(16, 9)
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .setSeamlessResizeEnabled(true)
            .build()
        this.enterPictureInPictureMode(params)
        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)
//        videoViewModel.createVideoMediaSession(this)

        if (isInPictureInPictureMode) {
            stopService(Intent(this, MusicService::class.java))

            // Cancel the notification
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(101)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        videoViewModel.exoPlayer.pause()
    }
}
