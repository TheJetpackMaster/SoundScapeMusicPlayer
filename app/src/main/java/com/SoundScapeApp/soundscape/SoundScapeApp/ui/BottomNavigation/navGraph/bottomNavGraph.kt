package com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.navGraph

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
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
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.AboutUs.AboutUs
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.AudioSettings.AudioSettings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.Settings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.ThemeSettings.ThemeSettings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.VideoSettings.VideoSettings
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Folders.FolderVideosList
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Playlists.VideoPlaylistDetailScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.VideosHome
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.VideoPlayingScreen

@RequiresApi(Build.VERSION_CODES.R)
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
    onVideoItemClick:(Int,Long)->Unit,
    mediaSession: MediaSession,
    onDeleteSong:(List<Uri>)->Unit,
    onVideoDelete:(List<Uri>)->Unit
) {
    NavHost(navController = navController,
        startDestination = BottomNavScreenRoutes.SongsHome.route,
        enterTransition = {
            fadeIn(animationSpec = tween(200))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
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
            ScreenRoute.NowPlayingScreen.route,
//            enterTransition = {
//                slideInVertically(
//                    initialOffsetY = { fullHeight ->
//                        fullHeight },
//                    animationSpec = tween(durationMillis = 200, easing = LinearEasing)
//                ) + fadeIn()
//            },
//            exitTransition = {
//                slideOutVertically(
//                    targetOffsetY = { fullHeight ->
//                        fullHeight },
//                    animationSpec = tween(durationMillis = 200, easing = LinearEasing)
//                ) + fadeOut()
//            }
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
        composable(BottomNavScreenRoutes.VideosHome.route) {
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
                onPipClick = onPipClick
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


//        SETTINGS
        composable(BottomNavScreenRoutes.Settings.route) {
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

        //        Theme Settings main screen
        composable(ScreenRoute.ThemeSettings.route) {
            ThemeSettings(
                navController = navController,
                audioViewModel = audioViewModel,
                videoViewModel = videoViewModel
            )
        }

        composable(ScreenRoute.AboutUs.route){
            AboutUs(navController = navController)
        }
    }
}
