package com.SoundScapeApp.soundscape.SoundScapeApp.ui.navGraph

import EqualizerScreen
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.Albums.AlbumsDetailScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.Artists.ArtistsDetailScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.AddSongsToPlaylist
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.PlayListDetailsScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.SongsHome
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.NowPlayingScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.AboutUs.AboutUs
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.AudioSettings.AudioSettings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.Settings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.ThemeSettings.ThemeSettings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.VideoSettings.VideoSettings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Folders.FolderVideosList
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Playlists.AddVideosToPlaylist
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Playlists.VideoPlaylistDetailScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.VideosHome
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.VideoPlayingScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.ChooseAudioPlayingScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.AppUpdate.AppUpdateInfo
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.splashscreen.OnBoardingScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(UnstableApi::class)
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    context: Context,
    onProgress: (Float) -> Unit,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int, Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    player: ExoPlayer,
    audioViewModel: AudioViewModel,
    videoViewModel: VideoViewModel,
    onPipClick: () -> Unit,
    onVideoItemClick: (Int, Long) -> Unit,
    mediaSession: MediaSession,
    onDeleteSong: (List<Uri>) -> Unit,
    onVideoDelete: (List<Uri>) -> Unit
) {
    val isFirstTime by audioViewModel.isFirstTime.collectAsState()
    val startDestination =
        if (isFirstTime) ScreenRoute.OnBoardingScreen.route else BottomNavScreenRoutes.SongsHome.route

    NavHost(navController = navController,
        startDestination = startDestination,
//        enterTransition = {
//            fadeIn(animationSpec = tween(250))
//        },
//        exitTransition = {
//            fadeOut(animationSpec = tween(250))
//        }

        enterTransition = {
            scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(160)
            ) + fadeIn(animationSpec = tween(100))
        },
        exitTransition = {
            scaleOut(
                targetScale = .95f,
                animationSpec = tween(160)
            ) + fadeOut(animationSpec = tween(150))
        },
        popEnterTransition = {
            scaleIn(initialScale = .95f, animationSpec = tween(160)) + fadeIn(
                animationSpec = tween(
                    100
                )
            )
        },
        popExitTransition = {
            scaleOut(
                targetScale = .95f,
                animationSpec = tween(160)
            ) + fadeOut(animationSpec = tween(150))
        }
    ) {

        composable("splash") { }

//        SongsHomeScreen
        composable(
            BottomNavScreenRoutes.SongsHome.route,
            enterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            }
        ) {
            SongsHome(
                navController = navController,
                audioList = audioList,
                onStart = onStart,
                onItemClick = onItemClick,
                player = player,
                viewModel = audioViewModel,
                context = context,
                mediaSession = mediaSession,
                onSongDelete = onDeleteSong
            )
        }
//        NowPlayingScreen
        composable(
            ScreenRoute.NowPlayingScreen.route
        ) {
            NowPlayingScreen(
                navController = navController,
                context = context,
                onProgress = onProgress,
                audioList = audioList,
                onStart = onStart,
                onNext = onNext,
                onPrevious = onPrevious,
                player = player,
                viewModel = audioViewModel
            )
        }


//        PlayListDetailScreen
        composable(
            route = ScreenRoute.PlaylistDetailScreen.route
        ) {
            PlayListDetailsScreen(
                viewModel = audioViewModel,
                navController = navController,
                audioList = audioList,
                player = player
            )
        }

//        AddSongsToPlayList
        composable("add") {
            AddSongsToPlaylist(
                navController = navController,
                viewModel = audioViewModel
            )
        }

//        Album detail screen
        composable(
            route = ScreenRoute.AlbumDetailScreen.route
        ) {
            AlbumsDetailScreen(
                viewModel = audioViewModel,
                navController = navController,
                audioList = audioList,
                player = player
            )
        }

//        Artist detail Screen
        composable(
            route = ScreenRoute.ArtistDetailScreen.route,
        ) {
            ArtistsDetailScreen(
                viewModel = audioViewModel,
                navController = navController,
                audioList = audioList,
                player = player
            )
        }


//        VideosHome
        composable(
            BottomNavScreenRoutes.VideosHome.route,
            enterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            }) {

            VideosHome(
                navController = navController,
                videoViewModel,
                onVideoItemClick = onVideoItemClick,
                onVideoDelete = onVideoDelete
            )
        }

//        video Playing Screen
        composable(ScreenRoute.VideoPlayingScreen.route) {
            VideoPlayingScreen(
                viewModel = videoViewModel,
                navController = navController,
                onPipClick = onPipClick,
                isMainActivity = true
            )
        }
//        FolderVideos
        composable("details") {
            FolderVideosList(
                videoViewModel,
                navController
            )
        }
//
        composable(ScreenRoute.VidePlaylistDetailScreen.route) {
            VideoPlaylistDetailScreen(
                viewModel = videoViewModel,
                navController = navController
            )
        }

        composable(ScreenRoute.AddVideosToPlaylist.route) {
            AddVideosToPlaylist(
                navController = navController,
                viewModel = videoViewModel
            )
        }


//        SETTINGS
        composable(
            BottomNavScreenRoutes.Settings.route,
            enterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            }) {
            Settings(navController = navController, videoViewModel, audioViewModel)
        }

//        Video settings main screen
        composable(ScreenRoute.VideoSettings.route) {
            VideoSettings(navController, videoViewModel)
        }

//        Audio Settings main screen
        composable(ScreenRoute.AudioSettings.route) {
            AudioSettings(navController, audioViewModel)
        }

        //Equalizer settings
        composable(ScreenRoute.EqualizerSettings.route) {
            EqualizerScreen(
                viewModel = audioViewModel,
                navController = navController
            )
        }

        //        Theme Settings main screen
        composable(ScreenRoute.ThemeSettings.route) {
            ThemeSettings(
                navController = navController,
                audioViewModel = audioViewModel,
                videoViewModel = videoViewModel
            )
        }

        //App Update settings
        composable(ScreenRoute.UpdateApp.route){
            AppUpdateInfo()
        }

        //About us
        composable(ScreenRoute.AboutUs.route) {
            AboutUs(navController = navController)
        }



        // Choose AudioPlaying Screen design
        composable(ScreenRoute.ChooseAudioPlayingScreen.route) {
            ChooseAudioPlayingScreen(
                viewModel = audioViewModel,
                navController = navController
            )
        }

        //Onboarding
        composable(ScreenRoute.OnBoardingScreen.route) {
            OnBoardingScreen(navController = navController)
        }
    }
}
