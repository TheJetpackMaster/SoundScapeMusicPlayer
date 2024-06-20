package com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.customBottomNavigation

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.ui.theme.GrayIcons
import com.SoundScapeApp.soundscape.ui.theme.PurpleIcons
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50

@Composable
fun CustomBottomNav(
    navController: NavController,
    context: Context,
    viewModel: AudioViewModel
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                SoundScapeThemes.colorScheme.primary
            )
            .padding(start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

//        SONGS
        Column(
            modifier = Modifier
                .size(78.dp)
                .clickable(
                    onClick = {
                        if (currentDestination != BottomNavScreenRoutes.SongsHome.route) {
                            navController.navigate(BottomNavScreenRoutes.SongsHome.route) {
                                popUpTo(BottomNavScreenRoutes.SongsHome.route)
                            }
                        }
                    }
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = BottomNavScreenRoutes.SongsHome.icon),
                contentDescription = "songs home button",
                modifier = Modifier.size(24.dp),
                tint = if (currentDestination == BottomNavScreenRoutes.SongsHome.route) PurpleIcons else GrayIcons,
            )
        }

//        VIDEOS
        Column(
            modifier = Modifier
                .size(78.dp)
                .clickable(
                    onClick = {
                        if (currentDestination != BottomNavScreenRoutes.VideosHome.route) {
                            navController.navigate(BottomNavScreenRoutes.VideosHome.route) {
                                popUpTo(BottomNavScreenRoutes.SongsHome.route)
                            }
                        }
                    }
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = BottomNavScreenRoutes.VideosHome.icon),
                contentDescription = "videos home button",
                modifier = Modifier.size(28.dp),
                tint = if (currentDestination == BottomNavScreenRoutes.VideosHome.route) PurpleIcons else GrayIcons,
            )
        }

//        SETTINGS
        Column(
            modifier = Modifier
                .size(78.dp)
                .clickable(
                    onClick = {
                        if (currentDestination != BottomNavScreenRoutes.Settings.route) {
                            navController.navigate(BottomNavScreenRoutes.Settings.route) {
                                popUpTo(BottomNavScreenRoutes.SongsHome.route)
                            }
                        }
                    }
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = BottomNavScreenRoutes.Settings.icon),
                contentDescription = "main settings button",
                modifier = Modifier.size(24.dp),
                tint = if (currentDestination == BottomNavScreenRoutes.Settings.route) PurpleIcons else GrayIcons,
            )
        }
    }
}