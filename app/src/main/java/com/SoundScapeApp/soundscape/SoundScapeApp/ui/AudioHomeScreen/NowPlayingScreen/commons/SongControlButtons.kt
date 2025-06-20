package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.ui.theme.BrightGray
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.test1
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    player:ExoPlayer,
    isMainActivity:Boolean,


) {

    LaunchedEffect(player.isPlaying) {
        if(isMainActivity) {
            isPlaying.value = player.isPlaying
        }
        else{
            isPlaying.value = true
        }
    }


    val playPauseButtonSize = remember { mutableStateOf(60.dp) }
    val playPauseIconSize = remember{ mutableStateOf(24.dp) }

    val coroutineScope = rememberCoroutineScope()


    val animatedButtonSize by animateDpAsState(
        targetValue = playPauseButtonSize.value,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val animatedPlayPauseIconSize by animateDpAsState(
        targetValue = playPauseIconSize.value,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
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
                tint = White90.copy(.9f),
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
                tint = White90.copy(.9f),
                modifier = Modifier.size(24.dp)

            )
        }

        Box(
            modifier = Modifier
                .size(animatedButtonSize)
                .clip(CircleShape)
                .background(SoundScapeThemes.colorScheme.secondary.copy(.5f))
                .clickable {
                    coroutineScope.launch {
                        playPauseButtonSize.value = 80.dp
                        playPauseIconSize.value = 34.dp
                        delay(50)
                        playPauseButtonSize.value = 60.dp
                        playPauseIconSize.value = 24.dp
                    }
                    onPlayPauseClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = if (isPlaying.value) R.drawable.pauseicon else R.drawable.playicon),
                contentDescription = null,
                tint = White90.copy(.9f),
                modifier = Modifier.size(animatedPlayPauseIconSize)
            )
        }
        IconButton(onClick = {
            onNextClick() })
        {
            Icon(
                painterResource(id = R.drawable.skipnexticon),
                contentDescription = null,
                tint = White90.copy(.9f),
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
                tint = White90.copy(.9f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
fun DoubleRowSongControlButtons(
    onShuffleClick: () -> Unit,
    shuffleMode: MutableState<Boolean>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    isPlaying: MutableState<Boolean>,
    onRepeatClick: () -> Unit,
    repeatMode: MutableIntState,
    player:ExoPlayer,
    isMainActivity:Boolean,
    current:MutableLongState,
    currentPlayListSongs:List<Long>,
    onFavoriteClick:()->Unit,
    onShareClick:()->Unit

) {

    LaunchedEffect(player.isPlaying) {
        if(isMainActivity) {
            isPlaying.value = player.isPlaying
        }
        else{
            isPlaying.value = true
        }
    }


    val playPauseButtonSize = remember { mutableStateOf(60.dp) }
    val playPauseIconSize = remember{ mutableStateOf(24.dp) }

    val coroutineScope = rememberCoroutineScope()


    val animatedButtonSize by animateDpAsState(
        targetValue = playPauseButtonSize.value,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    val animatedPlayPauseIconSize by animateDpAsState(
        targetValue = playPauseIconSize.value,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    // First Row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 14.dp, end = 14.dp),
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
                tint = White90.copy(.75f),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))

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

        Spacer(modifier = Modifier.weight(.7f))


        Box(
            modifier = Modifier
                .size(animatedButtonSize)
                .clip(CircleShape)
                .background(test1.copy(.8f))
                .clickable {
                    coroutineScope.launch {
                        playPauseButtonSize.value = 80.dp
                        playPauseIconSize.value = 34.dp
                        delay(50)
                        playPauseButtonSize.value = 60.dp
                        playPauseIconSize.value = 24.dp
                    }
                    onPlayPauseClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = if (isPlaying.value) R.drawable.pauseicon else R.drawable.playicon),
                contentDescription = null,
                tint = White90,
                modifier = Modifier.size(animatedPlayPauseIconSize)
            )
        }

        Spacer(modifier = Modifier.weight(.7f))

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
        Spacer(modifier = Modifier.weight(1f))

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
                tint = White90.copy(.75f),
                modifier = Modifier.size(24.dp)
            )
        }


    }

    Spacer(modifier = Modifier.height(16.dp))


// Second Row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            onFavoriteClick()
        })
        {
            Icon(
                imageVector = if (current.longValue in currentPlayListSongs) {
                    Icons.Default.Favorite // Red heart icon
                } else {
                    Icons.Default.FavoriteBorder // White heart icon
                },
                contentDescription = null,
                tint = if (current.longValue in currentPlayListSongs) Color.Red else White90,
            )
        }

        IconButton(onClick = {
            onShareClick()
        })
        {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = White90,
            )
        }
    }
}

