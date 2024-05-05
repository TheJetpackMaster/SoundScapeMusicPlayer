package com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons


import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.soundscape.SoundScapeApp.MainViewModel.PlayerState


@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun PlayerScreen(
    exoPlayer: ExoPlayer,
    playerState: PlayerState,
    showControls: MutableState<Boolean>,
    pipMode: Boolean,
    skipText: MutableState<String>,
    event: Lifecycle.Event,
    view: View,
    window: Window


) {

    LaunchedEffect(showControls.value) {
        showSystemUI(window, view, showControls)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()

        ) {
        AndroidView(
            factory = { context ->
                val playerView = PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    fitsSystemWindows = !showControls.value
                }
                playerView
            },
            update = { playerView ->
                playerView.apply {
                    playerView.resizeMode = playerState.resizeMode
                    playerView.keepScreenOn = playerState.isPlaying
//                    playerView.fitsSystemWindows = false

                    when (event) {
                        Lifecycle.Event.ON_PAUSE -> {
                            if (!pipMode) {
                                playerView.onPause()
                                playerView.player?.pause()
                            }
                        }

                        Lifecycle.Event.ON_RESUME -> {
                            playerView.onResume()
                        }

                        Lifecycle.Event.ON_DESTROY -> {

                        }

                        else -> {

                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
//                .transformable(state = transformState)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showControls.value = !showControls.value
                        }
                    )
                },
            onRelease = {
                showSystemUI(window, view, mutableStateOf(true))
                exoPlayer.pause()
            }
        )


    }
}