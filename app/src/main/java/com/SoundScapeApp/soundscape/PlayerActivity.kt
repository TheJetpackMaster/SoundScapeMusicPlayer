package com.SoundScapeApp.soundscape

import android.app.Activity
import android.app.AppOpsManager
import android.app.NotificationManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.service.MusicService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.VideoPlayingScreen
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeColorScheme
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeTheme
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {

    private val videoViewModel: VideoViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )

        val dataUri = intent.data

        if (dataUri != null) {
            videoViewModel.onIntent(dataUri)
        }


        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = false
            )

            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = false
            )

            val currentTheme by videoViewModel.currentTheme.collectAsState()
            SoundScapeThemes(
                currentTheme,
                this
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                ) {
                    if (dataUri != null) {
                        VideoPlayingScreen(viewModel = videoViewModel,
                            navController = rememberNavController(),
                            onPipClick = {
                                val packageManager = this@PlayerActivity.packageManager
                                val pipModeSupported = packageManager.hasSystemFeature(
                                    PackageManager.FEATURE_PICTURE_IN_PICTURE)

                                if (pipModeSupported) {
                                    if (isPipModeEnabled(this@PlayerActivity)) {
                                       enterPiPMode()
                                    }else{
                                        this@PlayerActivity.openPipSettings() }
                                } else {
                                    Toast.makeText(this@PlayerActivity, "Picture-in-Picture mode is not supported on this device.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            isMainActivity = false,
                            onVideoBack = {
                                finish()
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

    private fun enterPiPMode() {
        enterPictureInPictureMode()

        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)

        if(videoViewModel.videoMediaSession == null) {
            videoViewModel.createVideoMediaSession(this)
        }

        if (isInPictureInPictureMode) {
            stopService(Intent(this, MusicService::class.java))
            // Cancel the notification
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(101)
        }
    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)


        if (!isInPictureInPictureMode) {
            videoViewModel.exoPlayer.pause()
            videoViewModel.destroyVideoMediaSession()
        }

        if(isInPictureInPictureMode){
            if(videoViewModel.videoMediaSession == null){
                videoViewModel.createVideoMediaSession(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoViewModel.exoPlayer.pause()
    }
}