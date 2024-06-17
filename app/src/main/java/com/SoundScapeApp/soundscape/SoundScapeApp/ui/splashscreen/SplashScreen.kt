package com.SoundScapeApp.soundscape.SoundScapeApp.ui.splashscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.splashScreenColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: AudioViewModel
) {

    val logoAnimate = remember { mutableStateOf(false) }
    val progress = remember{ mutableStateOf(0f) }
    val animationCompleted = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        logoAnimate.value = true
        delay(500)
        navController.popBackStack()
        navController.navigate(ScreenRoute.MainScreen.route)
    }

    LaunchedEffect(logoAnimate.value) {
        if (logoAnimate.value && !animationCompleted.value) {
            for (i in 0..100) {
                progress.value = i / 100f
                delay(2) // Adjust delay to change animation speed
            }
            animationCompleted.value = true
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(splashScreenColor)
            .padding(top = 40.dp,bottom = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = logoAnimate.value,
            enter = fadeIn(animationSpec = tween(250)) + scaleIn(
                animationSpec = tween(250),
                initialScale = .9f
            ),
            exit = fadeOut(animationSpec = tween(250))
        ) {
            Image(
                painter = painterResource(id = R.drawable.splashscreenlogo),
                contentDescription = "app logo"
            )
        }
        AnimatedVisibility(visible = logoAnimate.value,
            enter = fadeIn(animationSpec = tween(250))
        ) {
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier.scale(scaleX = 1f, scaleY = 1.2f ),
                color = SoundScapeThemes.colorScheme.secondary,
                progress = { progress.value })
        }

        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = "By KP Creative Labs",
            color = White90)
    }
}