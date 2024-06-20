package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen

import Video
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.Build
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.media3.common.util.UnstableApi
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.ui.theme.White90
import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.AllVideos.formatVideoDuration
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.BottomControls
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.CenterControls
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.PlayerScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.ScreenBrightnessController
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.getCurrentBrightness
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.mapBrightnessToRange
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons.setScreenBrightness
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme2Primary
import com.SoundScapeApp.soundscape.ui.theme.White50
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.math.abs


@SuppressLint("SourceLockedOrientationActivity")
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
    onPipClick: () -> Unit,
    isMainActivity: Boolean,
    onVideoBack: () -> Unit = {}
) {

    ScreenRotationHandler()

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

    var isMuted = remember { mutableStateOf(false) }


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

    val currentBrightness by viewModel.videoScreenBrightness.collectAsState()
    val deviceVolume = getDeviceVolume(context)

    var brightness by remember { mutableStateOf(currentBrightness) }
    var volumeLevel by remember { mutableStateOf(deviceVolume) }
    Log.d("volume", getDeviceVolume(context).toString())

    val skipText = remember { mutableStateOf("") }


    var isBrightnessChanging by remember { mutableStateOf(false) }
    var isVolumeChanging by remember { mutableStateOf(false) }
    var isSkipTextChanging by remember { mutableStateOf(false) }

    val isDoubleTapToSeekEnabled = viewModel.doubleTapSeekEnabled.collectAsState()

    var showSpeedDailog by remember { mutableStateOf(false) }

    val settingsSheetState = rememberModalBottomSheetState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }
    var showMoreVertDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var isVideoLooping by remember { mutableStateOf(false) }

    var showVideoInfoDialog by remember { mutableStateOf(false) }

    val currentSelectedSubtitle = remember { mutableStateOf("disable") }

    var selectedTime by remember { mutableStateOf(0L) } // Default time selection is 0 minutes

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val lifeCycleOwner = LocalLifecycleOwner.current

    val activity = context as ComponentActivity

    val requestedOrientation = activity.requestedOrientation

    var delayJob: Job? = null
    var skipDelayJob: Job? = null
    val showControlDelayJob = remember { mutableStateOf<Job?>(null) }


    val videoList by viewModel.videoList.collectAsState()

    LaunchedEffect(
        exoPlayer.currentPosition
    ) {
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

//    LaunchedEffect(showControls.value,exoPlayer.isPlaying) {
//        showControlDelayJob?.cancel()
//        if (showControls.value && exoPlayer.isPlaying) {
//            showControlDelayJob = launch {
//                delay(2500)
//                showControls.value = false
//            }
//        }
//    }


    LaunchedEffect(Unit) {
        if (resumeFromLeftPos && isMainActivity) {
            val position = viewModel.getPlaybackPosition(currentMediaId.value)
            viewModel.seekToSavedPosition(position)
        }

        val defaultBrightness = getCurrentBrightness(activity) / 100
        viewModel.setBrightness(defaultBrightness)
        setScreenBrightness(activity, defaultBrightness)

    }

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

            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            viewModel.playBackSpeed(1f)
            viewModel.startTimer(duration = 0, onFinish = { exoPlayer.pause() }, exoPlayer)
            val defaultBrightness = getCurrentBrightness(activity) / 100

            setScreenBrightness(activity, defaultBrightness)
            viewModel.videoVolume(getDeviceVolume(context))
            viewModel.setBrightness(defaultBrightness)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->

                    if (!isLocked.value) {
                        val maxSwipeDistance = 200
                        val maxSeekIncrementSeconds = 20
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

                        skipDelayJob?.cancel()
                        isSkipTextChanging = true


                        skipDelayJob = scope.launch {
                            delay(600)
                            isSkipTextChanging = false
                        }
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
            window = window,
            onShowControlsTap = {
                if (!showControls.value) {
                    triggerShowControls(
                        showControlDelayJob,
                        scope,
                        showControls
                    )
                } else {
                    showControls.value = false
                }
            }
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

                                temporaryForward.value = false
                                temporaryForwardRotation.value = false
                                temporaryBackward.value = false
                                temporaryBackwardRotation.value = false
                                isVolumeChanging = false

                                val brightnessChange = dragAmount / 1000

                                // Update brightness within the range 0 to 1
                                brightness += brightnessChange
                                brightness = brightness.coerceIn(0f, 1f)


                                viewModel.setBrightness(brightness)

                                delayJob?.cancel()

                                // Start a new delay coroutine
                                isBrightnessChanging = true
                                delayJob = scope.launch {
                                    delay(400)
                                    isBrightnessChanging = false
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (!showControls.value) {
                                        showControlDelayJob.value?.cancel()
                                        showControls.value = true
                                        showControlDelayJob.value = scope.launch {
                                            delay(2500)
                                            showControls.value = false
                                        }
                                    } else {
                                        showControls.value = false
                                    }
                                },
                                onDoubleTap = {
                                    if (isDoubleTapToSeekEnabled.value) {
                                        viewModel.skipRewind()

                                        temporaryForward.value = false
                                        temporaryForwardRotation.value = false
                                        isVolumeChanging = false
                                        isBrightnessChanging = false


                                        delayJob?.cancel()

                                        temporaryBackward.value = true
                                        temporaryBackwardRotation.value = true

                                        // Reset temporary UI change after a delay
                                        delayJob = scope.launch {
                                            delay(1200) // Adjust delay time as needed
                                            temporaryForward.value = false
                                            temporaryBackward.value = false
                                        }
                                        scope.launch {
                                            delay(300) // Adjust delay time as needed
                                            temporaryForwardRotation.value = false
                                            temporaryBackwardRotation.value = false
                                        }
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
                                    temporaryForward.value = false
                                    temporaryForwardRotation.value = false
                                    temporaryBackward.value = false
                                    temporaryBackwardRotation.value = false
                                    isBrightnessChanging = false

                                    val volumeChange = dragAmount / 1000

                                    // Update volume within the range 0 to 1
                                    volumeLevel += volumeChange
                                    volumeLevel = volumeLevel.coerceIn(0f, 1f)

                                    val invertedVolume = 1f - volumeLevel
                                    viewModel.videoVolume(invertedVolume)

                                    delayJob?.cancel()

                                    // Start a new delay coroutine
                                    isVolumeChanging = true
                                    delayJob = scope.launch {
                                        delay(400)
                                        isVolumeChanging = false
                                    }
                                }
                            }

                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (!showControls.value) {
                                            triggerShowControls(
                                                showControlDelayJob,
                                                scope,
                                                showControls
                                            )
                                        } else {
                                            showControls.value = false
                                        }
                                    },
                                    onDoubleTap = {
                                        if (isDoubleTapToSeekEnabled.value) {
                                            viewModel.skipForward()

                                            temporaryBackward.value = false
                                            temporaryBackwardRotation.value = false
                                            isVolumeChanging = false
                                            isBrightnessChanging = false



                                            delayJob?.cancel()

                                            temporaryForward.value = true
                                            temporaryForwardRotation.value = true
                                            // Reset temporary UI change after a delay

                                            delayJob = scope.launch {
                                                delay(1200) // Adjust delay time as needed
                                                temporaryForward.value = false
                                            }
                                            scope.launch {
                                                delay(300) // Adjust delay time as needed
                                                temporaryForwardRotation.value = false
                                            }
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
                                if (isMainActivity) {
                                    if (navController.currentBackStackEntry?.lifecycle?.currentState
                                        == Lifecycle.State.RESUMED
                                    ) {
                                        navController.popBackStack()
                                    }
                                } else {
                                    onVideoBack()
                                }
                            },
                            /*onLanguageClick = {
                                showLanguageDialog = !showLanguageDialog
                                viewModel.updateAvailableTracks()
                            },*/
                            onSubtitleClick = {
                                showSubtitleDialog = !showSubtitleDialog
                                viewModel.updateAvailableSubtitles()
                            },
                            onMoreClick = {
                                showMoreVertDialog = !showMoreVertDialog
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (!isLocked.value) {
                        CenterControls(
                            isMuted = isMuted,
                            onLockClick = {
                                isLocked.value = !isLocked.value
                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            },
                            onMuteClick = {
                                isMuted.value = !isMuted.value
                                viewModel.toggleVideoVolume(isMuted)

                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            },
                            onScreenRotationClick = {
                                if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE ||
                                    requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE ||
                                    requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                ) {
                                    activity.requestedOrientation =
                                        ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                                } else {
                                    activity.requestedOrientation =
                                        ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                                }

                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            },
                            onVideoLoopClick = {
                                isVideoLooping = !isVideoLooping
                                viewModel.CurrentVideoLooping(isVideoLooping, exoPlayer)
                                //exoPlayer.repeatMode =
                                //  if (isVideoLooping) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            },
                            isVideoLooping = isVideoLooping
                        )
                    }

                    if (!isLocked.value) {
                        BottomControls(
                            player = exoPlayer,
                            resizeMode = playerState.resizeMode,
                            onResizeModeChange = {
                                viewModel.onResizeClick()
//                                showControlDelayJob.value?.cancel()
//                                showControls.value = true
//                                showControlDelayJob.value = scope.launch {
//                                    delay(2000)
//                                    showControls.value = false
//                                }
                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            },
                            showControls = {
                                if (!showControls.value) {
                                    triggerShowControls(
                                        showControlDelayJob,
                                        scope,
                                        showControls
                                    )
                                } else {
                                    showControls.value = false
                                }
                            },
                            viewModel = viewModel,
                            onLockClock = {
                                isLocked.value = !isLocked.value
                            },
                            onPIPClick = onPipClick,
                            onSpeedClick = {
                                showSpeedDailog = !showSpeedDailog
                            },
                            onNext = {
//                                showControlDelayJob.value?.cancel()
//                                showControls.value = true
//                                showControlDelayJob.value = scope.launch {
//                                    delay(2000)
//                                    showControls.value = false
//                                }
                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            },
                            onPrevious = {
//                                showControlDelayJob.value?.cancel()
//                                showControls.value = true
//                                showControlDelayJob.value = scope.launch {
//                                    delay(2000)
//                                    showControls.value = false
//                                }
                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            },
                            onPlayPause = {
                                if (exoPlayer.isPlaying) {
                                    showControlDelayJob.value?.cancel()
                                    showControls.value = true
                                } else {
                                    triggerShowControls(
                                        showControlDelayJob,
                                        scope,
                                        showControls,
                                        delayTime = 2000L
                                    )
                                }
                            },
                            onProgress = {
                                handleButtonClick(
                                    showControlDelayJob,
                                    scope,
                                    showControls
                                )
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    isLocked.value = false
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.Black.copy(0.4f)
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (isLocked.value) Color.Green.copy(0.9f) else Color.White,
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
            (temporaryBackward.value && !temporaryForward.value),
            enter = fadeIn(),
            exit = fadeOut()
        )
        {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SoundScapeThemes.colorScheme.secondary.copy(.3f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    painter = painterResource(
                        id =
                        R.drawable.backwardseek
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(backwardRotation),
                    tint = White90
                )
                Text(
                    text = "-${currentSeekTime.value / 1000}",
                    color = White90,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AnimatedVisibility(
            visible =
            (temporaryForward.value && !temporaryBackward.value),
            enter = fadeIn(),
            exit = fadeOut()
        )
        {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SoundScapeThemes.colorScheme.secondary.copy(.3f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    painter = painterResource(
                        id =
                        R.drawable.forwardbackward
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(forwardRotation),
                    tint = White90
                )
                Text(
                    text = "${currentSeekTime.value / 1000}",
                    color = White90,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isSkipTextChanging && !isBrightnessChanging && !isVolumeChanging) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SoundScapeThemes.colorScheme.secondary)
                    .padding(top = 4.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
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
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SoundScapeThemes.colorScheme.secondary)
                    .padding(top = 4.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                val formattedBrightness =
                    mapBrightnessToRange(brightness).toString()

                Icon(
                    painter = painterResource(id = R.drawable.brightness),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = formattedBrightness,
                    color = White90,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            ScreenBrightnessController(activity = context, brightness = brightness)

        }
        if (isVolumeChanging && !isBrightnessChanging) {
            if (deviceVolume <= 0f) {
                Toast.makeText(context, "Phone volume is muted", Toast.LENGTH_SHORT).show()
            } else {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SoundScapeThemes.colorScheme.secondary)
                        .padding(top = 4.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val formattedVolume =
                        mapBrightnessToRange(volumeLevel).toString()

                    Icon(
                        painter = painterResource(id = R.drawable.volume),
                        contentDescription = null,
                        tint = White90,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = formattedVolume,
                        color = White90,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
        if (showSpeedDailog) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    showSpeedDailog = false
                },
                title = { Text(text = "Speed", color = Color.White) },
                confirmButton = {},
                text = {
                    Column {
                        // First row of IconButtons with text
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically, // Align items vertically
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = {
                                        val newSpeed =
                                            (exoPlayer.playbackParameters.speed - 0.25f).coerceIn(
                                                0.25f,
                                                3.0f
                                            )
                                        viewModel.playBackSpeed(newSpeed)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp) // Size of the icon itself
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = "${currentPlaybackSpeed}x",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = {
                                        val newSpeed =
                                            (exoPlayer.playbackParameters.speed + 0.25f).coerceIn(
                                                0.25f,
                                                3.0f
                                            )
                                        viewModel.playBackSpeed(newSpeed)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp) // Size of the icon itself
                                    )
                                }
                            }
                        }
                    }
                }
            )

            /*ModalBottomSheet(
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
            }*/
        }
        if (showLanguageDialog) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    showLanguageDialog = false
                    exoPlayer.play()
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
                                                    exoPlayer.play()
                                                }
                                            ),
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
                                        val text =
                                            if (tracks[0].language == "und" || tracks[0].language == "```") "Default Track" else "$index.${track.language}"
                                        Text(text = text, color = Color.White)
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
                containerColor = SoundScapeThemes.colorScheme.secondary,
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
        if (showMoreVertDialog) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    showMoreVertDialog = false
                    exoPlayer.play()
                },

                confirmButton = {
                },
                text = {
                    Column {
                        // First row of IconButtons with text
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        showMoreVertDialog = false
                                        exoPlayer.pause()
                                        showLanguageDialog = !showLanguageDialog
                                        viewModel.updateAvailableTracks()
                                    }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.language),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Language",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        showMoreVertDialog = false
                                        exoPlayer.pause()
                                        showTimerDialog = !showTimerDialog
                                    }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_timer_24),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )

                                Text(
                                    text = "Timer",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        showMoreVertDialog = false
                                        // showVideoInfoDialog = true
                                        showVideoInfoDialog = !showVideoInfoDialog
                                        exoPlayer.pause()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Info",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            )
        }
        if (showTimerDialog) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    showTimerDialog = false
                    exoPlayer.play()
                },
                title = { Text(text = "Set Timer", color = Color.White) },
                confirmButton = {
                    Button(
                        onClick = {
                            exoPlayer.play()
                            viewModel.startTimer(
                                duration = selectedTime,
                                onFinish = { exoPlayer.pause() },
                                exoPlayer
                            )
                            showTimerDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = White90.copy(.8f)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(text = "Set Timer", color = SoundScapeThemes.colorScheme.secondary)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showTimerDialog = false
                            exoPlayer.play()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(text = "Cancel")
                    }
                },
                text = {
                    Column {
                        // Row for increment and decrement buttons
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = {
                                        selectedTime =
                                            (selectedTime - 5 * 60 * 1000).coerceAtLeast(0)
                                    },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${selectedTime / (60 * 1000)} min",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = {
                                        selectedTime =
                                            (selectedTime + 5 * 60 * 1000).coerceAtMost(60 * 60 * 1000)
                                    },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                },
            )
        }
        if (showVideoInfoDialog) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    showVideoInfoDialog = false
                    exoPlayer.play()
                },
                title = {

                },
                text = {
                    val currentMediaId = exoPlayer.currentMediaItem?.mediaId?.toLongOrNull()
                    val currentVideo = videoList.find { it.id == currentMediaId }
                    if (currentVideo != null) {
                        DisplayVideoInfo(video = currentVideo)
                    } else {
                        Text(text = "No media item is currently playing")
                    }
                    /*Text(
                        text = "${exoPlayer.currentMediaItem?.mediaId?}",
                        color = White50
                    )*/
                },
                confirmButton = {
                },
                dismissButton = {

                }
            )
        }
    }
}

@Composable
fun DisplayVideoInfo(video: Video) {
    Column {
        Text(text = "Title: ${video.displayName}", color = Color.White)
        Text(text = "Path: ${video.uri}", color = Color.White)
        Text(text = "Date Added: ${video.dateAdded}", color = Color.White)
        Text(
            text = "Duration: ${formatVideoDuration(video.duration.toLong())} seconds",
            color = Color.White
        )
        Text(text = "Size: ${video.sizeMB}", color = Color.White)
        Text(text = "Bucket Name: ${video.bucketName}", color = Color.White)
        // Add more fields as necessary
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpperControls(
    videoTitle: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    //onLanguageClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onMoreClick: () -> Unit,
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

            /*IconButton(onClick = {
                onLanguageClick()
            })
            {
                Icon(
                    painter = painterResource(id = R.drawable.language),
                    contentDescription = null,
                    tint = White90,
                    modifier = Modifier.size(24.dp)
                )
            }*/

            IconButton(onClick = {
                onMoreClick()
            })
            {
                Icon(
                    imageVector = Icons.Default.MoreVert,
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
    isSeekFinished: MutableState<Boolean>,
) {

    val primaryColor = White90
    Log.d("progress", videoProgress.toString())

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

fun getDeviceVolume(context: Context): Float {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    return currentVolume.toFloat() / maxVolume.toFloat()
}


@Composable
fun ScreenRotationHandler() {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val activity = context as? Activity
    val requestedOrientation = activity?.requestedOrientation

    DisposableEffect(key1 = sensorManager, key2 = accelerometer, key3 = requestedOrientation) {
        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]

                    // Log the accelerometer values
                    Log.d("none", "X: $x, Y: $y")

                    // Check if the device is in landscape mode set by the user
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE) {
                        // Log the orientation
                        Log.d("Orientation", "User landscape detected")

                        // Check if the device is tilted horizontally
                        if (abs(x) > abs(y)) {
                            // Log the orientation change
                            Log.d("Orientation", "Tilted horizontally")

                            // Change to sensor-based landscape
                            (context as? Activity)?.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used
            }
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}

fun startControlVisibilityDelay(
    showControlDelayJob: MutableState<Job?>,
    scope: CoroutineScope,
    showControls: MutableState<Boolean>,
    delay: Long = 2500L
) {
    // Cancel the previous delay job if it exists
    showControlDelayJob.value?.cancel()

    // Start a new delay job to hide controls after 2500 ms
    showControlDelayJob.value = scope.launch {
        delay(delay)
        showControls.value = false
    }
}

// Function to trigger showing controls
fun triggerShowControls(
    showControlDelayJob: MutableState<Job?>,
    scope: CoroutineScope,
    showControls: MutableState<Boolean>,
    delayTime: Long = 2500L
) {
    showControls.value = true // Show controls immediately
    startControlVisibilityDelay(
        showControlDelayJob,
        scope, showControls,
        delay = delayTime
    )// Start the control visibility delay
}

// Function to handle clicks on other buttons (cancel the delay and start a new one)
fun handleButtonClick(
    showControlDelayJob: MutableState<Job?>,
    scope: CoroutineScope,
    showControls: MutableState<Boolean>,
) {
    showControlDelayJob.value?.cancel() // Cancel the existing delay job
    startControlVisibilityDelay(
        showControlDelayJob,
        scope,
        showControls,
        delay = 2000
    ) // Start a new delay job for hiding controls
}