package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.AllVideos.formatVideoDuration
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.CustomSeekBar
import com.SoundScapeApp.soundscape.ui.theme.White90


@UnstableApi
@Composable
fun BottomControls(
    player: ExoPlayer,
    onRotateScreenClick: () -> Unit,
    resizeMode: Int,
    onLockClock: () -> Unit,
    onPIPClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onResizeModeChange: () -> Unit,
    modifier: Modifier = Modifier,
    showControls: () -> Unit,
    viewModel: VideoViewModel,
    showControl: MutableState<Boolean>

) {

    val isPlaying = remember { mutableStateOf(player.isPlaying) }
    val currentPosition = remember { mutableStateOf(0L) }
    val duration = remember { mutableStateOf(0L) }
    val progress = remember { mutableStateOf(0f) }

    val isSeekFinished = remember { mutableStateOf(false) }

    LaunchedEffect(player.currentPosition, player.duration,player.isPlaying,player.currentMediaItem) {
        currentPosition.value = player.currentPosition
        duration.value = player.duration

        progress.value = (player.currentPosition.toFloat() / player.duration.toFloat()) * 100f
        isPlaying.value = player.isPlaying
    }
    Column(
        modifier = modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .background(Color.Black.copy(0.4f))
            .pointerInput(Unit) {
                detectDragGestures { _, _ -> }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatVideoDuration(currentPosition.value),
                modifier = Modifier.padding(start = 8.dp),
                color = White90,
                fontWeight = FontWeight.SemiBold
            )


            CustomSeekBar(
                onProgress = { viewModel.onProgressSeek(it) },
                videoProgress = progress.value,
                isSeekFinished = isSeekFinished,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = formatVideoDuration(duration.value),
                modifier = Modifier.padding(end = 8.dp),
                color = White90,
                fontWeight = FontWeight.SemiBold
            )
        }

        playbackControls(
            onClick = {
                showControls()
            },
            onPlayPauseClick = {
                viewModel.onPlayPause()
            },
            onSeekForwardClick = {
                viewModel.playNext()
                if (!player.isPlaying) {
                    viewModel.exoPlayer.play()
                    isPlaying.value = true
                }else{
                    isPlaying.value = true
                }
            },
            onSeekBackwardClick = {
                viewModel.playPrevious()
                if (!player.isPlaying) {
                    viewModel.exoPlayer.play()
                    isPlaying.value = true

                }else{
                    isPlaying.value = true
                }
            },
            player = viewModel.exoPlayer,
            onResizeClick = onResizeModeChange,
            resizeMode = resizeMode,
            onLockClick = onLockClock,
            onPIPClick = onPIPClick,
            onSettingsClick = onSettingsClick,
            isPlaying = isPlaying
        )
        Spacer(modifier = Modifier.size(24.dp))
    }
}


@OptIn(UnstableApi::class)
@Composable
private fun playbackControls(
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    resizeMode: Int,
    onSeekForwardClick: () -> Unit,
    onSeekBackwardClick: () -> Unit,
    onResizeClick: () -> Unit,
    onPIPClick: () -> Unit,
    onLockClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    isPlaying: MutableState<Boolean>
) {

    Row(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        playbackControlsItem(
            icon = R.drawable.unlock,
            contentDescription = "lock",
            onIconClick = {
                onLockClick()
            },
            onSingleClick = { /*TODO*/ },
            onDoubleClick = { /*TODO*/ },
            iconSize = 20.dp
        )


        playbackControlsItem(
            icon = R.drawable.settingsbottom,
            contentDescription = "lock",
            onIconClick = {
                onSettingsClick()
            },
            onSingleClick = { /*TODO*/ },
            onDoubleClick = { /*TODO*/ },
            iconSize = 20.dp
        )

        Spacer(modifier = Modifier.weight(.2f))

        playbackControlsItem(
            icon = R.drawable.skippreviousicon,
            contentDescription = "back",
            onIconClick = onSeekBackwardClick,
            onSingleClick = onClick,
            onDoubleClick = onSeekBackwardClick,
            modifier = modifier
                .weight(1f),
            iconSize = 22.dp
        )

        playbackControlsItem(
            icon = if (isPlaying.value) R.drawable.pauseicon else R.drawable.playicon,
            contentDescription = "playPause",
            onIconClick = {
                isPlaying.value = !isPlaying.value
                onPlayPauseClick()
            },
            onSingleClick = {
                onClick()
            },
            onDoubleClick = onPlayPauseClick,
            modifier = modifier.weight(1f),
            outlineColor = White90,
            outlineStroke = 1.dp,
            iconSize = 22.dp
        )

        playbackControlsItem(
            icon = R.drawable.skipnexticon,
            contentDescription = "next",
            onIconClick = onSeekForwardClick,
            onSingleClick = onClick,
            onDoubleClick = onSeekForwardClick,
            modifier = modifier
                .weight(1f)
        )

        Spacer(modifier = Modifier.weight(.2f))

        playbackControlsItem(
            icon = R.drawable.pipmode,
            contentDescription = "full screen",
            onIconClick = {
                onPIPClick()
            },
            onSingleClick = { /*TODO*/ },
            onDoubleClick = { /*TODO*/ },
            iconSize = 20.dp
        )

        playbackControlsItem(
            icon = if (resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) R.drawable.minimize else R.drawable.maximize,
            contentDescription = "full screen",
            onIconClick = {
                onResizeClick()
            },
            onSingleClick = { /*TODO*/ },
            onDoubleClick = { /*TODO*/ },
            iconSize = 22.dp
        )
    }
}

@kotlin.OptIn(ExperimentalFoundationApi::class)
@Composable
private fun playbackControlsItem(
    @DrawableRes icon: Int,
    contentDescription: String,
    onIconClick: () -> Unit,
    iconSize: Dp = 22.dp,
    onSingleClick: () -> Unit,
    onDoubleClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    outlineStroke: Dp = 0.dp,
    outlineColor: Color = Color.Transparent

) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        OutlinedIconButton(
            modifier = Modifier.size(42.dp),
            onClick = onIconClick,
            border = BorderStroke(width = outlineStroke, color = outlineColor)
        ) {
            Icon(
                painterResource(id = icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = White90
            )
        }

    }
}
