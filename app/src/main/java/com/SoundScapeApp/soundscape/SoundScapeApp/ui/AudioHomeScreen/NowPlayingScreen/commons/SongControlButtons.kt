package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.ui.theme.BrightGray
import com.SoundScapeApp.soundscape.ui.theme.White90


@Composable
fun SongControlButtons(
    onShuffleClick: () -> Unit,
    shuffleMode: MutableState<Boolean>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    isPlaying: MutableState<Boolean>,
    onRepeatClick: () -> Unit,
    repeatMode: MutableIntState,
    player:ExoPlayer

) {
    LaunchedEffect(player.isPlaying) {
        isPlaying.value = player.isPlaying

    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onShuffleClick()
            })
        {
            Icon(
                painterResource(
                    id = if (shuffleMode.value) R.drawable.shuffle1
                    else R.drawable.noshuffle
                ),
                contentDescription = null,
                tint = White90,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(onClick = {
            onPreviousClick()
        })
        {
            Icon(
                painterResource(id = R.drawable.skippreviousicon),
                contentDescription = null,
                tint = White90,
                modifier = Modifier.size(24.dp)

            )
        }

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(BrightGray)
                .clickable {
                    onPlayPauseClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = if (isPlaying.value) R.drawable.pauseicon else R.drawable.playicon),
                contentDescription = null,
                tint = White90,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = {
            onNextClick() })
        {
            Icon(
                painterResource(id = R.drawable.skipnexticon),
                contentDescription = null,
                tint = White90,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = {
            onRepeatClick() })
        {
            Icon(
                painterResource(
                    id =
                    when (repeatMode.intValue) {
                        1 -> R.drawable.repeatone
                        2 -> R.drawable.repeatall
                        else -> {
                            R.drawable.notrepeat
                        }
                    }
                ),
                contentDescription = null,
                tint = White90,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

