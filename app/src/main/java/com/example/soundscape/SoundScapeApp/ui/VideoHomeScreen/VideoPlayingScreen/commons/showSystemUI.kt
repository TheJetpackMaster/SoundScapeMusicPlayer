package com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons

import android.view.View
import android.view.Window
import androidx.compose.runtime.MutableState
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun showSystemUI(window: Window, view: View, showControls: MutableState<Boolean>) {
    val insetsController = WindowCompat.getInsetsController(window, view)
    if (!view.isInEditMode) {
        if (!showControls.value) {
            insetsController.apply {
                hide(WindowInsetsCompat.Type.statusBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        } else {
            insetsController.apply {
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }
}
