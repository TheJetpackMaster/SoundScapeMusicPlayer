package com.example.soundscape.SoundScapeApp.ui.splashscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.soundscape.R
import com.example.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.example.soundscape.ui.theme.SoundScapeThemes
import kotlinx.coroutines.delay
import kotlin.math.truncate

@Composable
fun SplashScreen(
    navController: NavHostController
) {

    val logoAnimate = remember { mutableStateOf(false) }
    val progress = remember{ mutableStateOf(0f) }
    var animationCompleted = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        logoAnimate.value = true
        delay(1500)
        logoAnimate.value = false
        navController.popBackStack()
        navController.navigate(ScreenRoute.MainScreen.route)
    }

    LaunchedEffect(logoAnimate.value) {
        if (logoAnimate.value && !animationCompleted.value) {
            for (i in 0..100) {
                progress.value = i / 100f
                delay(17) // Adjust delay to change animation speed
            }
            animationCompleted.value = true
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = logoAnimate.value,
            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                animationSpec = tween(1000),
                initialScale = .7f
            ),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Image(
                painter = painterResource(id = R.drawable.sound_wave_music_logo),
                contentDescription = null
            )
        }
        AnimatedVisibility(visible = logoAnimate.value,
            enter = fadeIn(animationSpec = tween(500))
        ) {
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier.scale(scaleX = 1f, scaleY = 1.2f ),
                color = SoundScapeThemes.colorScheme.secondary,
                progress = { progress.value })
        }
    }
}