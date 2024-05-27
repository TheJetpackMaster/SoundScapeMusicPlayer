package com.SoundScapeApp.soundscape

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.compose.rememberNavController
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.UIEvents
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.NowPlayingScreen
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeTheme
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerActivity : ComponentActivity() {

    private val audioViewModel: AudioViewModel by viewModels()

    @Inject
    lateinit var player: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioViewModel.setIsMainActivity(false)
        audioViewModel.setCurrentPlayingSection(1)
        audioViewModel.setMediaItemFlag(false)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        val dataUri = intent.data

        if (dataUri != null) {
            audioViewModel.onIntent(dataUri)
        }

        setContent {
            val currentTheme = audioViewModel.currentTheme.collectAsState()
            val audioList = audioViewModel.scannedAudioList.collectAsState()

            SoundScapeThemes(
                themeChoice = currentTheme.value,
                activity = this
            )
            {
                NowPlayingScreen(
                    navController = rememberNavController(),
                    context = this@AudioPlayerActivity,
                    onProgress = { audioViewModel.onProgressSeek(it) },
                    audioList = audioList.value,
                    onStart = {
                        audioViewModel.onUiEvents(UIEvents.PlayPause)
                    },
                    onNext = { /*TODO*/ },
                    onPrevious = { /*TODO*/ },
                    player = player,
                    viewModel = audioViewModel,
                    isMainActivity = false,
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)


        val dataUri = intent.data

        if (dataUri != null) {
            audioViewModel.onNewIntent(uri = dataUri)
        }
    }
}