package com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController,viewModel: VideoViewModel,audioViewModel: AudioViewModel) {

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        text = "Settings",
                        color = White90,
                        modifier = Modifier.padding(start = 36.dp)
                    )
                })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    top = 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /*
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.generalsetting),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(25.dp)
                )


                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "General Settings",
                    color = White90,
                    style = SoundScapeThemes.typography.titleSmall
                )
            }
             */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        navController.navigate(ScreenRoute.AudioSettings.route)
                    }
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.musicnote),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Audio Settings",
                    color = White90,
                    style = SoundScapeThemes.typography.titleSmall
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        navController.navigate(ScreenRoute.VideoSettings.route)
                    }
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.videosettings),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Video Settings",
                    color = White90,
                    style = SoundScapeThemes.typography.titleSmall
                )
            }


            /*
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sound_settings),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Sound Settings",
                    color = White90,
                    style = SoundScapeThemes.typography.titleSmall
                )
            }
             */


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        navController.navigate(ScreenRoute.ThemeSettings.route)
                    }
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.themesetting),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Theme",
                    color = White90,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        navController.navigate(ScreenRoute.AboutUs.route)
                    }
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "About us",
                    color = White90,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}