package com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlin.math.roundToInt

fun setScreenBrightness(activity: ComponentActivity, brightness: Float) {
    val invertedBrightness = 1f - brightness
    val window = activity.window
    val layoutParams = window.attributes
    layoutParams.screenBrightness = invertedBrightness // Set brightness level here (0 - 1)
    window.attributes = layoutParams
}

@Composable
fun ScreenBrightnessController(activity: ComponentActivity, brightness: Float) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    setScreenBrightness(activity, brightness)

    DisposableEffect(key1 = activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun mapBrightnessToRange(brightness: Float): Int {
    val invertedBrightness = 1f - brightness
    val maxValue = 100 // Maximum brightness value
    val rangeStep = 5 // Step size for each brightness level
    val mappedValue =
        (invertedBrightness * maxValue).roundToInt() // Map brightness value to range [0, 100]
    val mappedBrightness =
        (mappedValue / rangeStep) * rangeStep // Round to nearest step in the range
    return mappedBrightness
}
