package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatDuration(songProgressString.longValue),
            color = White90,
            fontSize = 12.sp
        )
        Slider(
            modifier = Modifier.weight(1f),
            value = songProgress.floatValue,
            onValueChange = {
                onProgress(it)
            },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                inactiveTrackColor = White50,
                activeTrackColor = Color.White,
            ),
            thumb = {
                Box(
                    Modifier
                        .size(24.dp),
                    contentAlignment = Alignment.Center
                ){
                    Spacer(modifier = Modifier.size(10.dp)
                        .clip(CircleShape)
                        .background(White90))
                }
            }
        )
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


