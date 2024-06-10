package com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes

import com.SoundScapeApp.soundscape.R

sealed class BottomNavScreenRoutes(val route: String, val icon: Int) {
    data object SongsHome : BottomNavScreenRoutes("songsHome", R.drawable.audiobottom)
    data object VideosHome : BottomNavScreenRoutes("videosHome", R.drawable.videobottom)
    data object Settings:BottomNavScreenRoutes("settings",R.drawable.settingsbottom)
}

sealed class ScreenRoute(val route:String){
    data object SplashScreen:ScreenRoute(route = "splashScreen")

    data object MainScreen:ScreenRoute(route = "mainScreen")
    data object NowPlayingScreen:ScreenRoute(route = "nowPlayingScreen")
    data object PlaylistDetailScreen : ScreenRoute(route = "playlist_detail_screen")
    data object AddSongsToPlayList:ScreenRoute(route = "addtoplaylist")
    data object AlbumDetailScreen:ScreenRoute(route = "albumdetails")

    data object ArtistDetailScreen:ScreenRoute(route = "artistdetails")


    data object VideoPlayingScreen:ScreenRoute(route = "videoplayingscreen")
    data object VidePlaylistDetailScreen:ScreenRoute(route = "videoplaylistdetailscreen")

    data object AddVideosToPlaylist:ScreenRoute(route = "addvideostoplaylist")


    data object VideoSettings:ScreenRoute(route = "videomainsettings")
    data object AudioSettings:ScreenRoute(route = "audiomainsettings")
    data object EqualizerSettings:ScreenRoute(route = "equalizersettings")
    data object ThemeSettings:ScreenRoute(route = "thememainsettings")
    data object AboutUs:ScreenRoute(route = "aboutusscreen")
}