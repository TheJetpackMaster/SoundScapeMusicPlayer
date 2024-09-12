package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons


import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import java.util.concurrent.TimeUnit

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSongSlider(
    onProgress: (Float) -> Unit,
    player: ExoPlayer,
    viewModel: AudioViewModel,
    isMainActivity:Boolean

) {

    val songProgressString = remember {
        mutableLongStateOf(0L)
    }

    val songProgress = remember {
        mutableFloatStateOf(0f)
    }

    val duration = remember { mutableLongStateOf(player.duration) }


    LaunchedEffect(viewModel.progress,player.duration) {
        songProgress.floatValue =
            ((player.currentPosition.toFloat() / player.duration.toFloat()) * 100f)
        songProgressString.longValue = player.currentPosition
//        repeatMode.intValue = player.repeatMode
//        shuffleMode.value = player.shuffleModeEnabled
        duration.longValue = player.duration
    }




    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatDuration(songProgressString.longValue),
            color = White90,
            fontSize = 12.sp
        )
//
//        Slider(
//            modifier = Modifier.weight(1f),
//            value = songProgress.floatValue,
//            onValueChange = {
//                onProgress(it)
//            },
//            valueRange = 0f..100f,
//            colors = SliderDefaults.colors(
//                inactiveTrackColor = White50,
//                activeTrackColor = Color.White,
//            )
//        )

        Spacer(Modifier.width(10.dp))

        CustomSimpleSlider(
            modifier = Modifier.fillMaxWidth(.85f),
            value = songProgress.floatValue,
            onValueChange = {
                onProgress(it)
            }
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = formatDuration(duration.longValue),
            color = White90,
            fontSize = 12.sp,
        )
    }
}

//@SuppressLint("DefaultLocale")
//fun formatDuration(duration: Long): String {
//    val minute = TimeUnit.MILLISECONDS.toMinutes(duration)
//    val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minute)
//    return String.format("%02d:%02d", minute, seconds)
//}

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Long): String {
    if (duration <= 0) {
        return "00:00" // Return a default value if duration is not yet available or invalid
    }

    val minute = TimeUnit.MILLISECONDS.toMinutes(duration)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minute)
    return String.format("%02d:%02d", minute, seconds)
}

@Composable
fun CustomSimpleSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    trackHeight: Dp = 4.dp,
    thumbRadius: Dp = 5.dp,
    activeTrackColor: Color = Color.White,
    inactiveTrackColor: Color = White50.copy(.5f),
) {
    var sliderWidth by remember { mutableStateOf(0f) }
    val thumbPosition = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start) * sliderWidth


    Canvas(
        modifier = modifier
            .height(thumbRadius * 8)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newValue = ((offset.x / sliderWidth) * (valueRange.endInclusive - valueRange.start) + valueRange.start)
                        .coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChange(newValue)
                    onValueChangeFinished()
                }
            }
            .onSizeChanged {
                sliderWidth = it.width.toFloat()

            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onValueChangeFinished()
                    },
                    onDrag = { change, _ ->
                        val newValue = ((change.position.x / sliderWidth) * (valueRange.endInclusive - valueRange.start) + valueRange.start)
                            .coerceIn(valueRange.start, valueRange.endInclusive)
                        onValueChange(newValue)
                    }
                )
            }
    ) {
        val trackHeightPx = trackHeight.toPx()
        val thumbRadiusPx = thumbRadius.toPx()

        // Draw inactive track
        drawLine(
            color = inactiveTrackColor,
            start = Offset(0f, center.y),
            end = Offset(sliderWidth, center.y),
            strokeWidth = trackHeightPx,
            cap = StrokeCap.Round
        )

        // Draw active track
        drawLine(
            color = activeTrackColor,
            start = Offset(0f, center.y),
            end = Offset(thumbPosition, center.y),
            strokeWidth = trackHeightPx,
            cap = StrokeCap.Round
        )

        // Draw thumb
        drawCircle(
            color = activeTrackColor,
            radius = thumbRadiusPx,
            center = Offset(thumbPosition, center.y),

        )
    }
}


