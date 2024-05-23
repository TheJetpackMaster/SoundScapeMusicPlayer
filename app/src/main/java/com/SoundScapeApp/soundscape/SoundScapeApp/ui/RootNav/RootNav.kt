package com.SoundScapeApp.soundscape.SoundScapeApp.ui.RootNav

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.MainScreen.MainScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.splashscreen.SplashScreen

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun RootNav(
    navController: NavHostController,
    context: Context,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Long,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int, Long) -> Unit,
    onClick: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    player: ExoPlayer,
    audioViewModel: AudioViewModel,
    videoViewModel: VideoViewModel,
    mediaSession: MediaSession,
    onPipClick: () -> Unit,
    onVideoItemClick: (Int, Long) -> Unit,
    onDeleteSong:(List<Uri>)->Unit,
    onVideoDelete:(List<Uri>)->Unit,
    onScreenRotationClick: () -> Unit
) {
    NavHost(navController = navController, startDestination = ScreenRoute.SplashScreen.route,
        popEnterTransition = {
            fadeIn(animationSpec = tween(250))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(250))
        },
        enterTransition = {
            fadeIn(animationSpec = tween(250))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(250))
        }
    ) {

        composable(ScreenRoute.SplashScreen.route,
            exitTransition = {
                fadeOut(tween(250, easing = LinearEasing))
            }) {
            SplashScreen(navController = navController)
        }

        composable(ScreenRoute.MainScreen.route,
            enterTransition = {
                fadeIn(tween(350, easing = LinearEasing))
            }
            ) {
            MainScreen(
                context = context,
                onProgress = onProgress,
                isAudioPlaying = isAudioPlaying,
                currentPlayingAudio = currentPlayingAudio,
                audioList = audioList,
                onStart = onStart,
                onItemClick = onItemClick,
                onClick = onClick,
                onNext = onNext,
                onPrevious = onPrevious,
                player = player,
                audioViewModel = audioViewModel,
                videoViewModel = videoViewModel,
                mediaSession = mediaSession,
                onPipClick = onPipClick,
                onVideoItemClick = onVideoItemClick,
                onDeleteSong = onDeleteSong,
                onVideoDelete = onVideoDelete,
                onScreenRotationClick= onScreenRotationClick
            )
        }
    }
}