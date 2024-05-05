package com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.soundscape.R
import com.example.soundscape.ui.theme.White90
import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.TimeBar
import androidx.navigation.NavController
import com.example.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.example.soundscape.SoundScapeApp.MainViewModel.PlayerState
import com.example.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.example.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.formatDuration
import com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.AllVideos.formatVideoDuration
import com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.BottomControls
import com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.PlayerScreen
import com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.ScreenBrightnessController
import com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.mapBrightnessToRange
import com.example.soundscape.ui.theme.Theme2Primary
import com.example.soundscape.ui.theme.White50
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.R)
@kotlin.OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@UnstableApi
@Composable
fun VideoPlayingScreen(
    viewModel: VideoViewModel,
    navController: NavController,
    onPipClick: () -> Unit
//    onRotateScreenClick: () -> Unit,
//    onBackClick: () -> Unit,
) {

    val currentSeekTime = viewModel.videoSeekTime.collectAsState()
    val resumeFromLeftPos by viewModel.resumeFromLeftPositionEnabled.collectAsState()

    val context = LocalContext.current as ComponentActivity
    val view = LocalView.current
    val window = (view.context as Activity).window

    val exoPlayer = viewModel.exoPlayer
    val pipMode by viewModel.isPipModeEnabled.collectAsState()

    val playerState by viewModel.playerState.collectAsState()
    val currentPlaybackSpeed by viewModel.currentPlaybackSpeed.collectAsState()
    val tracksOriginalLanguage by viewModel.originalLanguageTracks.collectAsState()
    val subtitlesOriginal by viewModel.originalSubtitles.collectAsState()

    val showControls = remember {
        mutableStateOf(false)
    }

    val tracks by viewModel.availableTracks.collectAsState(initial = emptyList())
    val subtitles by viewModel.availableSubtitles.collectAsState(initial = emptyList())


    val trackSelector = viewModel.trackSelector

    val isLocked = rememberSaveable {
        mutableStateOf(false)
    }

    val currentMediaId = remember { mutableStateOf("0") }
    val currentMediaPosition = remember { mutableStateOf(0L) }

    var duration by remember { mutableStateOf(0L) }

    var progress by remember { mutableStateOf(0f) }

    val isPlaying = remember { mutableStateOf(false) }

    LaunchedEffect(
        showControls.value,
        exoPlayer.currentPosition
    ) {
        if (showControls.value) {
            delay(1500)
            showControls.value = false
        }
        currentMediaId.value = exoPlayer.currentMediaItem!!.mediaId
        currentMediaPosition.value = exoPlayer.currentPosition

        val videoDuration = exoPlayer.duration
        val percentagePlayed =
            (currentMediaPosition.value.toFloat() / videoDuration.toFloat()) * 100

        // Check if more than 95% of the video is played
        if (percentagePlayed >= 95) {
            viewModel.removeSavedPlayback(currentMediaId.value)
        }

        isPlaying.value = exoPlayer.isPlaying
        duration = exoPlayer.duration
        progress = (currentMediaPosition.value.toFloat() / duration.toFloat()) * 100f
    }


    LaunchedEffect(exoPlayer.currentMediaItem) {
        duration = exoPlayer.duration
    }

    LaunchedEffect(Unit) {
        if (resumeFromLeftPos) {
            val position = viewModel.getPlaybackPosition(currentMediaId.value)
            viewModel.seekToSavedPosition(position)
        }
    }

    // Define a mutableStateOf to track temporary UI change after forward seeking
    val temporaryForward = remember { mutableStateOf(false) }
    val temporaryBackward = remember { mutableStateOf(false) }

    val temporaryForwardRotation = remember {
        mutableStateOf(false)
    }
    val temporaryBackwardRotation = remember {
        mutableStateOf(false)
    }
    val backwardRotation by animateFloatAsState(
        targetValue = if (temporaryBackwardRotation.value) -60f else 0f,
        label = ""
    )
    val forwardRotation by animateFloatAsState(
        targetValue = if (temporaryForwardRotation.value) 60f else 0f,
        label = ""
    )
    val scope = rememberCoroutineScope()

    var brightness by remember { mutableStateOf(0f) }
    var volumeLevel by remember { mutableStateOf(0f) }

    val skipText = remember { mutableStateOf("") }


    var isBrightnessChanging by remember { mutableStateOf(false) }
    var isVolumeChanging by remember { mutableStateOf(false) }
    var isSkipTextChanging by remember { mutableStateOf(false) }

    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }

    val currentSelectedSubtitle = remember { mutableStateOf("disable") }



    LaunchedEffect(brightness) {
        isBrightnessChanging = true
        delay(200)
        isBrightnessChanging = false
    }
    LaunchedEffect(skipText.value) {
        isSkipTextChanging = true
        delay(600)
        isSkipTextChanging = false
    }

    LaunchedEffect(volumeLevel) {
        val invertedVolume = 1f - volumeLevel
        viewModel.videoVolume(invertedVolume)

        isVolumeChanging = true
        delay(200)
        isVolumeChanging = false
    }

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val lifeCycleOwner = LocalLifecycleOwner.current


    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }

        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
            if (!pipMode) {
                exoPlayer.pause()
            }
            viewModel.savePlaybackPosition(
                context,
                currentMediaId.value,
                currentMediaPosition.value
            )

        }
    }

//    val pos by viewModel.playbackPosition.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->

                    if (!isLocked.value) {
                        val maxSwipeDistance = 100
                        val maxSeekIncrementSeconds = 10
                        val swipeRatio = abs(dragAmount) / maxSwipeDistance
                        val seekIncrementSeconds = (swipeRatio * maxSeekIncrementSeconds).toInt()
                        val seekIncrementMillis = seekIncrementSeconds * 1000

                        val currentPos = exoPlayer.currentPosition
                        val totalMillis = currentPos + seekIncrementMillis

                        skipText.value = formatVideoDuration(totalMillis)

                        if (dragAmount > 0) {
                            exoPlayer.seekTo(exoPlayer.currentPosition + seekIncrementMillis)
                        } else {
                            exoPlayer.seekTo(exoPlayer.currentPosition - seekIncrementMillis)
                        }
                        change.consume()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
//      PlayerViewScreen
        PlayerScreen(
            exoPlayer = exoPlayer,
            playerState = playerState,
            showControls = showControls,
            pipMode = pipMode,
            skipText = skipText,
            event = lifecycle,
            view = view,
            window = window
        )

        if (!pipMode) {
            if (!isLocked.value) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
//                        .transformable(
//                            state = transformState
//                        ),
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)

                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                val brightnessChange = dragAmount / 1000

                                // Update brightness within the range 0 to 1
                                brightness += brightnessChange
                                brightness = brightness.coerceIn(0f, 1f)
                                Log.d("bright", brightness.toString())
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    showControls.value = !showControls.value
                                },
                                onDoubleTap = {
                                    viewModel.skipRewind()

                                    temporaryForward.value = false
                                    temporaryForwardRotation.value = false
                                    temporaryBackward.value = true
                                    temporaryBackwardRotation.value = true

                                    // Reset temporary UI change after a delay
                                    scope.launch {
                                        delay(1200) // Adjust delay time as needed
                                        temporaryBackward.value = false
                                    }
                                    scope.launch {
                                        delay(300) // Adjust delay time as needed
                                        temporaryBackwardRotation.value = false
                                    }

                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { _, dragAmount ->

                                    val volumeChange = dragAmount / 1000

                                    // Update brightness within the range 0 to 1
                                    volumeLevel += volumeChange
                                    volumeLevel = volumeLevel.coerceIn(0f, 1f)

                                    Log.d("bright", brightness.toString())
                                }
                            }

                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        showControls.value = !showControls.value
                                    },
                                    onDoubleTap = {
                                        viewModel.skipForward()

                                        temporaryBackward.value = false
                                        temporaryBackwardRotation.value = false

                                        temporaryForward.value = true
                                        temporaryForwardRotation.value = true
                                        // Reset temporary UI change after a delay
                                        scope.launch {
                                            delay(1200) // Adjust delay time as needed
                                            temporaryForward.value = false
                                        }
                                        scope.launch {
                                            delay(300) // Adjust delay time as needed
                                            temporaryForwardRotation.value = false
                                        }
                                    }
                                )
                            }
                    )
                }
            }
            if (showControls.value) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(.2f))
                )
            }

            AnimatedVisibility(
                visible = showControls.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (!isLocked.value) {
                        UpperControls(
                            videoTitle = exoPlayer.currentMediaItem?.mediaMetadata?.displayTitle.toString(),
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onLanguageClick = {
                                showLanguageDialog = !showLanguageDialog
                                viewModel.updateAvailableTracks()
                            },
                            onSubtitleClick = {
                                showSubtitleDialog = !showSubtitleDialog
                                viewModel.updateAvailableSubtitles()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (!isLocked.value) {
                        BottomControls(
                            player = exoPlayer,
                            onRotateScreenClick = {},
                            resizeMode = playerState.resizeMode,
                            onResizeModeChange = {
                                viewModel.onResizeClick()
                            },
                            showControls = {
                                showControls.value = !showControls.value
                            },
                            viewModel = viewModel,
                            onLockClock = {
                                isLocked.value = !isLocked.value
                            },
                            onPIPClick = onPipClick,
                            onSettingsClick = {
                                showSettingsBottomSheet = !showSettingsBottomSheet
                            },
                            showControl = showControls
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(onClick = {
                                isLocked.value = false
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = White90
                                )
                            }

                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible =
            (temporaryBackward.value || temporaryForward.value),
            enter = fadeIn(),
            exit = fadeOut()
        )
        {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(.2f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    painter = painterResource(
                        id = if (temporaryBackward.value)
                            R.drawable.backwardseek else R.drawable.forwardbackward
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .rotate(if (temporaryBackward.value) backwardRotation else forwardRotation),
                    tint = White90
                )
                Text(
                    text =
                    if (temporaryBackward.value) {
                        "-${currentSeekTime.value / 1000}"
                    } else "${currentSeekTime.value / 1000}",
                    color = White90,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isSkipTextChanging && !isBrightnessChanging && !isVolumeChanging) {
            Box(
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    text = skipText.value,
                    color = White90,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isBrightnessChanging && !isVolumeChanging) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {


                val formattedBrightness =
                    mapBrightnessToRange(brightness).toString()

                Text(
                    text = formattedBrightness,
                    color = White90,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    painter = painterResource(id = R.drawable.brightness),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(24.dp)
                )
            }

            ScreenBrightnessController(activity = context, brightness = brightness)

        }
        if (isVolumeChanging && !isBrightnessChanging) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {


                val formattedVolume =
                    mapBrightnessToRange(volumeLevel).toString()

                Text(
                    text = formattedVolume,
                    color = White90,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    painter = painterResource(id = R.drawable.volume),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(22.dp)
                )
            }

        }
        if (showSettingsBottomSheet) {
            ModalBottomSheet(
                shape = RoundedCornerShape(4.dp),
                containerColor = Theme2Primary,
                dragHandle = {

                },
                onDismissRequest = {
                    showSettingsBottomSheet = false
                },
                sheetState = settingsSheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(0.25f)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "0.25x",
                            color =
                            if (currentPlaybackSpeed == 0.25f) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(0.5f)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "0.5x",
                            color =
                            if (currentPlaybackSpeed == 0.5f) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(46.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(0.75f)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "0.75x",
                            color =
                            if (currentPlaybackSpeed == 0.75f) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(46.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(1.0f)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "Normal",
                            color =
                            if (currentPlaybackSpeed == 1.0f) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(1.25f)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1.25x",
                            color =
                            if (currentPlaybackSpeed == 1.25f) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(1.5f)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1.5x",
                            color =
                            if (currentPlaybackSpeed == 1.5f) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(1.75f)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1.75x",
                            color =
                            if (currentPlaybackSpeed == 1.75f) Color.White else White50
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.playBackSpeed(2f)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "2x",
                            color =
                            if (currentPlaybackSpeed == 2f) Color.White else White50
                        )

                    }
                }
            }
        }
        if (showLanguageDialog) {
            AlertDialog(
                containerColor = Theme2Primary,
                onDismissRequest = {
                    showLanguageDialog = false
                },
                confirmButton = { /*TODO*/ },
                title = {
                    Text(
                        text = "Select language",
                        color = White90
                    )
                },
                text = {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                        val boxHeight = if (tracks.size <= 3) {
                            (60.dp * tracks.size).coerceAtMost(250.dp)
                        } else {
                            250.dp
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(boxHeight)
                        )
                        {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(items = tracks) { index, track ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp)
                                            .padding(
                                                top = 2.dp,
                                                bottom = 2.dp,
                                                start = 4.dp,
                                                end = 4.dp
                                            )
                                            .clickable(
                                                onClick = {
                                                    trackSelector.setParameters(
                                                        trackSelector
                                                            .buildUponParameters()
                                                            .setRendererDisabled(
                                                                C.TRACK_TYPE_AUDIO, false
                                                            )
                                                            .setPreferredAudioLanguages(
                                                                tracksOriginalLanguage[index]
                                                            )
                                                    )
                                                    showLanguageDialog = false
                                                }
                                            ),
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
                                        val text =
                                            if (tracks[0].language == "und" || tracks[0].language == "```") "Default Track" else "$index.${track.language}"
                                        Text(text = text)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }
            )
        }
        if (showSubtitleDialog) {
            AlertDialog(
                containerColor = Theme2Primary,
                onDismissRequest = {
                    showSubtitleDialog = false
                },
                confirmButton = { /*TODO*/ },
                title = {
                    Text(
                        text = "Select Subtitle",
                        color = White90
                    )
                },
                text = {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                        val boxHeight = if (subtitles.size <= 3) {
                            (60.dp * subtitles.size).coerceAtMost(250.dp)
                        } else {
                            250.dp
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(
                                    top = 2.dp,
                                    bottom = 2.dp,
                                    start = 4.dp,
                                    end = 4.dp
                                )
                                .clickable(
                                    onClick = {
                                        trackSelector.setParameters(
                                            trackSelector
                                                .buildUponParameters()
                                                .setRendererDisabled(
                                                    C.TRACK_TYPE_VIDEO,
                                                    true
                                                )
                                        )
                                        showSubtitleDialog = false
                                        currentSelectedSubtitle.value = "disable"

                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(
                                text = "Disable Subtitles",
                                color = if (currentSelectedSubtitle.value == "disable") Color.White else White50,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(boxHeight)
                        )
                        {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            ) {
                                itemsIndexed(items = subtitles) { index, subtitle ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .padding(
                                                top = 2.dp,
                                                bottom = 2.dp,
                                                start = 4.dp,
                                                end = 4.dp
                                            )
                                            .clickable(
                                                onClick = {
                                                    trackSelector.setParameters(
                                                        trackSelector
                                                            .buildUponParameters()
                                                            .setRendererDisabled(
                                                                C.TRACK_TYPE_TEXT, false
                                                            )
                                                            .setPreferredTextLanguages(
                                                                subtitlesOriginal[index]
                                                            )
                                                    )
                                                    showSubtitleDialog = false
                                                    currentSelectedSubtitle.value =
                                                        subtitles[index].language
                                                }
                                            ),
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
                                        Text(
                                            text = subtitle.language,
                                            color = if (currentSelectedSubtitle.value == subtitles[index].language) Color.White else White50,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}


@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpperControls(
    videoTitle: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLanguageClick: () -> Unit,
    onSubtitleClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(0.4f))
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = videoTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = White90,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .weight(.5f)
            )

            IconButton(onClick = {
                onSubtitleClick()
            })
            {
                Icon(
                    painter = painterResource(id = R.drawable.subtitles),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = {
                onLanguageClick()
            })
            {
                Icon(
                    painter = painterResource(id = R.drawable.language),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

//
@Composable
fun CustomSeekBar(
    modifier: Modifier = Modifier,
    onProgress: (Float) -> Unit,
    videoProgress: Float,
    isSeekFinished: MutableState<Boolean>
) {

    val primaryColor = White90

    Slider(
        value = videoProgress,
        onValueChange = { newPosition ->
            onProgress(newPosition)
        },
        onValueChangeFinished = {
            isSeekFinished.value = true
        },
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(
            thumbColor = primaryColor,
            activeTrackColor = primaryColor,
            inactiveTrackColor = primaryColor.copy(alpha = 0.3f)
        ),
        modifier = modifier
    )
}

//
//@kotlin.OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CustomVideoSlider(
//    videoProgressString: MutableLongState,
//    videoProgress: MutableFloatState,
//    onProgress: (Float) -> Unit,
//    duration: MutableLongState,
//    player: ExoPlayer,
//    viewModel: VideoViewModel
//
//) {
//    LaunchedEffect(viewModel.progress) {
//        duration.longValue = viewModel.duration
//        videoProgress.floatValue =
//            (player.currentPosition.toFloat() / player.duration.toFloat()) * 100f
//        videoProgressString.longValue = player.currentPosition
////        repeatMode.intValue = player.repeatMode
////        shuffleMode.value = player.shuffleModeEnabled
//    }
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = formatDuration(videoProgressString.longValue),
//            color = White90,
//            fontSize = 12.sp
//        )
//        Slider(
//            modifier = Modifier.weight(1f),
//            value = videoProgress.floatValue,
//            onValueChange = {
//                onProgress(it)
//            },
//            valueRange = 0f..100f,
//            colors = SliderDefaults.colors(
//                inactiveTrackColor = White50,
//                activeTrackColor = Color.White,
//            ),
//            thumb = {
//
//            }
//        )
//        Text(
//            text = formatDuration(duration.longValue),
//            color = White90,
//            fontSize = 12.sp,
//        )
//    }
//}

//@UnstableApi
//@Composable
//fun CustomSeekBar(
//    player: Player,
//    isSeekInProgress: (Boolean) -> Unit,
//    onSeekBarMove: (Long) -> Unit,
//    currentTime: Long,
//    totalDuration: Long,
//    modifier: Modifier = Modifier
//) {
//    val primaryColor = MaterialTheme.colorScheme.primary
//
//    AndroidView(
//        factory = { context ->
//
//            val listener = object : TimeBar.OnScrubListener {
//
//                var previousScrubPosition = 0L
//
//                override fun onScrubStart(timeBar: TimeBar, position: Long) {
//                    isSeekInProgress(true)
//                    previousScrubPosition = position
//                }
//
//                override fun onScrubMove(timeBar: TimeBar, position: Long) {
//                    onSeekBarMove(position)
//                }
//
//                override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
//                    if (canceled) {
//                        player.seekTo(previousScrubPosition)
//                    } else {
//                        player.seekTo(position)
//                    }
//                    isSeekInProgress(false)
//                }
//
//            }
//
//            DefaultTimeBar(context).apply {
//                setScrubberColor(primaryColor.toArgb())
//                setPlayedColor(primaryColor.toArgb())
//                setUnplayedColor(primaryColor.copy(0.3f).toArgb())
//                addListener(listener)
//                setDuration(totalDuration)
//                setPosition(player.currentPosition)
//            }
//        },
//        update = {
//            it.apply {
//                setPosition(currentTime)
//            }
//        },
//
//        modifier = modifier
//    )
//}