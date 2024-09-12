package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.BlurHelpers
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.startService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.CustomSongSlider
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.SongControlButtons
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.MainScreen.BlurHelper
import com.SoundScapeApp.soundscape.ui.theme.BrightGray
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme13Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme3Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme7Secondary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.test1
import com.SoundScapeApp.soundscape.ui.theme.test2
import com.SoundScapeApp.soundscape.ui.theme.test3
import java.io.FileNotFoundException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import android.view.MotionEvent
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInteropFilter
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.DoubleRowSongControlButtons
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.formatDuration
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.color1
import com.SoundScapeApp.soundscape.ui.theme.secondTest1
import com.SoundScapeApp.soundscape.ui.theme.secondTest2
import com.SoundScapeApp.soundscape.ui.theme.thirdTest1
import com.SoundScapeApp.soundscape.ui.theme.thirdTest2
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AudioPlayingScreen2(
    navController: NavController,
    context: Context,
    onProgress: (Float) -> Unit,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    player: ExoPlayer,
    viewModel: AudioViewModel,
    isMainActivity: Boolean = true,
    onBackClick: () -> Unit = {},

    ) {

    val isPlaying = remember {
        mutableStateOf(player.isPlaying)
    }

    val currentPlayListSongs by viewModel.favoritesSongs.collectAsState()

    val current = remember {
        mutableLongStateOf(0L)
    }

    val repeatMode = remember {
        mutableIntStateOf(0)
    }
    val shuffleMode = remember {
        mutableStateOf(false)
    }

    val currentPlayingSong: Audio? by remember(audioList, current.longValue) {
        derivedStateOf {
            audioList.find { it.id == current.longValue }
        }
    }

    val currentIntentMediaItem = viewModel.currentMediaItemAudio.collectAsState()


    val dominantColor = remember {
        mutableStateOf(BrightGray)
    }

    LaunchedEffect(player.currentMediaItem) {
        current.longValue = player.currentMediaItem?.mediaId?.toLongOrNull() ?: -1
        shuffleMode.value = player.shuffleModeEnabled
        repeatMode.intValue = player.repeatMode
    }

    LaunchedEffect(!isMainActivity) {
        startService(context)
        isPlaying.value = true
    }

    val songProgressString = remember {
        mutableLongStateOf(0L)
    }

    val songProgress = remember {
        mutableFloatStateOf(0f)
    }

    val duration = remember { mutableLongStateOf(player.duration) }


    LaunchedEffect(viewModel.progress, player.duration) {
        songProgress.floatValue =
            ((player.currentPosition.toFloat() / player.duration.toFloat()) * 100f)
        songProgressString.longValue = player.currentPosition
//        repeatMode.intValue = player.repeatMode
//        shuffleMode.value = player.shuffleModeEnabled
        duration.longValue = player.duration
    }

    DisposableEffect(currentPlayingSong?.artwork?.toUri()) {
        if (currentPlayingSong?.artwork?.isNotEmpty() == true) {
            try {
                val bitmap = BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(currentPlayingSong?.artwork?.toUri() as Uri)
                )
                Palette.from(bitmap).generate { palette ->
                    val swatches = palette?.swatches.orEmpty()

                    if (swatches.isNotEmpty()) {
                        val mostUsedSwatch = swatches.maxByOrNull { it.population }

                        if (mostUsedSwatch != null) {
                            dominantColor.value = Color(mostUsedSwatch.rgb)
                        }
                    }
                }

                onDispose {
                    bitmap.recycle()
                }
            } catch (e: FileNotFoundException) {
                Log.e("PlayScreen", "Album art not found. Using fallback color.")
                dominantColor.value = BrightGray
            }
        } else {
            Log.e("PlayScreen", "Artwork URI is empty or null.")
            dominantColor.value = BrightGray
        }
        onDispose {

        }

    }

    var showMoreVertDropDown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Now Playing",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = White90
                        )
                    }

                },
                actions = {
                    IconButton(onClick = {
                        showMoreVertDropDown = !showMoreVertDropDown
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90
                        )
                        DropdownMenu(
                            modifier = Modifier.background(SoundScapeThemes.colorScheme.primary),
                            expanded = showMoreVertDropDown,
                            onDismissRequest = {
                                showMoreVertDropDown = false
                            })
                        {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Choose Design",
                                        color = White90
                                    )
                                },
                                onClick = {
                                    showMoreVertDropDown = false
                                    navController.navigate(ScreenRoute.ChooseAudioPlayingScreen.route)
                                })
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isMainActivity) {
                            if (navController.currentBackStackEntry?.lifecycle?.currentState
                                == Lifecycle.State.RESUMED
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = White90,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            val currentSetTheme = remember { mutableStateOf(1) }

//            val uri = currentPlayingSong?.artwork?.toUri()
//            val blurredBitmap = uri?.let {
//                val contextNonNull = context ?: return@let null // Ensure context is not null
//                BlurHelpers.blur(contextNonNull, it, radius = 25f)
//            }
//
//            if (blurredBitmap != null) {
//                Image(
//                    bitmap = blurredBitmap.asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.FillBounds,
//                )
//            } else {
//               Image(painter = painterResource(id = R.drawable.sample), contentDescription = null)
//            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                val uri =
                    if (isMainActivity) currentPlayingSong?.artwork?.toUri() else currentIntentMediaItem.value?.artwork?.toUri()
                val blurredBitmap = uri?.let {
                    try {
                        val contextNonNull =
                            context ?: return@let null // Ensure context is not null
                        BlurHelpers.blur(contextNonNull, it, radius = 25f)
                    } catch (e: FileNotFoundException) {
                        null // Return null if image loading fails
                    }
                }

                if (blurredBitmap != null) {
                    Image(
                        bitmap = blurredBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                    )
                } else {
                    // Use default image when album art is not found or loading fails
                    val blurredBitmaps =
                        BlurHelper.blur(context, drawableResId = R.drawable.roundsampleimage, 25f)
                    Image(
                        bitmap = blurredBitmaps.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                    )
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(data = if (isMainActivity) currentPlayingSong?.artwork else currentIntentMediaItem.value?.artwork)
                            .apply(block = fun ImageRequest.Builder.() {
                                error(R.drawable.roundsampleimage)
                            }
                            ).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(80.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                dominantColor.value.copy(.8f),
                                secondTest2.copy(.8f),
//                                dominantColor.value.copy(.8f)
                            )
                        )
                    )
                    .padding(top = it.calculateTopPadding())
                    .padding(top = 8.dp, start = 2.dp, end = 2.dp, bottom = 28.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isMainActivity) currentPlayingSong?.title
                        ?: "Unknown Title" else currentIntentMediaItem.value?.title
                        ?: "Unknown Title",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = White90,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(.7f)
                        .basicMarquee(
                            animationMode = MarqueeAnimationMode.Immediately,
                            iterations = 1000,
                            initialDelayMillis = 1000,
                            //delayMillis = 2000,
                            velocity = 30.dp
                        )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isMainActivity) {
                        if (currentPlayingSong?.artist == "<unknown>" || currentPlayingSong?.artist == null) "Artist" else currentPlayingSong!!.artist
                    } else currentIntentMediaItem.value?.artist ?: "UArtist",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = White50,
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                )



                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ) {
//
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(data = if (isMainActivity) currentPlayingSong?.artwork else currentIntentMediaItem.value?.artwork)
                                    .apply(block = fun ImageRequest.Builder.() {
                                        error(R.drawable.roundsampleimage)
                                        placeholder(R.drawable.roundsampleimage)
                                    }
                                    ).build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(SoundScapeThemes.sizes.medium)
                                .border(.5.dp, Color.White.copy(.4f), CircleShape)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        CircularSlider(
                            modifier = Modifier
                                .size(SoundScapeThemes.sizes.large),
                            foreGroundStroke = 11f,
                            backgroundStroke = 10f,
                            thumbStroke = 15f,
                            backgroundColor = White90.copy(.2f),
                            progressColor = test3,
                            cap = StrokeCap.Round,
                            thumbColor = test3,
                            padding = 20f,
                            onChange = {

                            },
                            viewModel = viewModel,
                            player = player
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()

                                .align(alignment = Alignment.TopStart)
                                .padding(bottom = 20.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = formatDuration(songProgressString.longValue),
                                modifier = Modifier,
                                color = White90.copy(.7f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            VerticalDivider(
                                Modifier
                                    .height(15.dp),
                                color = White50
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = formatDuration(duration.longValue),
                                modifier = Modifier,
                                color = White90.copy(.7f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                DoubleRowSongControlButtons(
                    onShuffleClick = {
                        viewModel.toggleShuffle()
                        shuffleMode.value = !shuffleMode.value
                    },
                    shuffleMode = shuffleMode,
                    onPreviousClick = {
                        onPrevious()
                        if (!player.isPlaying) {
                            onStart()
                        }
                    },
                    onNextClick = {
                        onNext()
                        if (!player.isPlaying) {
                            onStart()
                        }
                    },
                    onPlayPauseClick = {
                        onStart()
                        isPlaying.value = !isPlaying.value
                    },
                    isPlaying = isPlaying,
                    onRepeatClick = {
                        viewModel.toggleRepeat()
                        repeatMode.intValue = player.repeatMode
                    },
                    repeatMode = repeatMode,
                    player = player,
                    isMainActivity = isMainActivity,
                    current = current,
                    currentPlayListSongs = currentPlayListSongs,
                    onFavoriteClick = {
                        viewModel.toggleFavorite(current.longValue)
                        viewModel.getFavoritesSongs()
                    },
                    onShareClick = {
                        if(isMainActivity) {
                            viewModel.shareAudio(
                                context,
                                currentPlayingSong!!.uri,
                                currentPlayingSong!!.title
                            )

                        }else{
                            viewModel.shareAudio(
                                context,
                                currentIntentMediaItem.value!!.uri,
                                currentIntentMediaItem.value!!.title
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


//@Composable
//fun CustomCircularProgress(
//    canvasSize: Dp = 300.dp,
//    indicatorValue: Int = 65,
//    maxIndicatorValue: Int = 100,
//    backgroundIndicatorColor: Color = White90.copy(.2f),
//    backgroundIndicatorWidth: Float = 12f,
//    foregroundIndicatorColor: Brush = Brush.sweepGradient(
//        colors = listOf(
//            test3,
//            test3,
//        )
//    ),
//    foregroundIndicatorStrokeWidth: Float = 25f,
//    capColor:Color = test3,
//) {
//
//
//    var allowedIndicatorValue by remember {
//        mutableStateOf(maxIndicatorValue)
//    }
//    allowedIndicatorValue = if (indicatorValue <= maxIndicatorValue) {
//        indicatorValue
//    } else {
//        maxIndicatorValue
//    }
//
//
//    val animatedIndicatorValue = remember {
//        Animatable(initialValue = 0f)
//    }
//
//    LaunchedEffect(key1 = allowedIndicatorValue) {
//        animatedIndicatorValue.animateTo(allowedIndicatorValue.toFloat())
//    }
//    val percentage = (animatedIndicatorValue.value / maxIndicatorValue) * 100
//
//    val sweepAngle by animateFloatAsState(
//        targetValue = (2.85 * percentage).toFloat(),
//        label = "",
//        animationSpec = tween(1000)
//    )
//
//    Column(
//        modifier = Modifier
//            .size(canvasSize)
//            .drawBehind {
//                val componentSize = size / 1.25f
//                backgroundIndicator(
//                    componentSize = componentSize,
//                    indicatorColor = backgroundIndicatorColor,
//                    indicatorStrokeWidth = backgroundIndicatorWidth
//                )
//                foregroundIndicator(
//                    sweepAngle = sweepAngle,
//                    componentSize = componentSize,
//                    indicatorColor = foregroundIndicatorColor,
//                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth,
//                    capColor = capColor
//                )
//            }
//
//    ) {
//
//    }
//}
//
//
//fun DrawScope.backgroundIndicator(
//    componentSize: Size,
//    indicatorColor: Color,
//    indicatorStrokeWidth: Float,
//) {
//    drawArc(
//        size = componentSize,
//        color = indicatorColor,
//        startAngle = -54f,
//        sweepAngle = 285f,
//        useCenter = false,
//        style = Stroke(
//            width = indicatorStrokeWidth,
//            cap = StrokeCap.Round
//        ),
//        topLeft = Offset(
//            x = (size.width - componentSize.width) / 2f,
//            y = (size.height - componentSize.height) / 2f,
//        )
//    )
//}
//
//fun DrawScope.foregroundIndicator(
//    sweepAngle: Float,
//    componentSize: Size,
//    indicatorColor: Brush,
//    indicatorStrokeWidth: Float,
//    capWidth: Float = 16f,
//    capColor: Color = Color.Transparent
//) {
//    val center = Offset(size.width / 2f, size.height / 2f)
//
//    // Calculate the position of the cap based on the current sweep angle
//    val capPosition = Offset(
//        x = center.x + (componentSize.width / 2f) * cos(Math.toRadians(sweepAngle.toDouble() - 54)).toFloat(),
//        y = center.y + (componentSize.height / 2f) * sin(Math.toRadians(sweepAngle.toDouble() - 54)).toFloat()
//    )
//
//    // Draw the cap
//    drawArc(
//        color = capColor,
//        startAngle = 0f,
//        sweepAngle = 300f,
//        useCenter = true,
//        style = Stroke(
//            width = capWidth,
//            cap = StrokeCap.Round
//        ),
//        topLeft = capPosition - Offset(capWidth / 2f, capWidth / 2f),
//        size = Size(capWidth, capWidth)
//    )
//
//    // Draw the progress arc
//    drawArc(
//        size = componentSize,
//        brush = indicatorColor,
//        startAngle = -54f,
//        sweepAngle = sweepAngle,
//        useCenter = false,
//        style = Stroke(
//            width = indicatorStrokeWidth,
//            cap = StrokeCap.Round
//        ),
//        topLeft = center - Offset(componentSize.width / 2f, componentSize.height / 2f)
//    )
//}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CircularSlider(
    modifier: Modifier = Modifier,
    padding: Float = 10f,
    backgroundStroke: Float = 20f,
    foreGroundStroke: Float = 20f,
    thumbStroke: Float = 22f,
    cap: StrokeCap = StrokeCap.Round,
    touchStroke: Float = 80f,
    thumbColor: Color = Color.Blue,
    progressColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray,
    debug: Boolean = false,
    onChange: ((Float) -> Unit)? = null,
    viewModel: AudioViewModel,
    player: ExoPlayer
) {
    val songProgress = remember {
        mutableFloatStateOf(0f)
    }

    val duration = remember { mutableLongStateOf(player.duration) }


    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var angle by remember { mutableStateOf(-240f) }
    var last by remember { mutableStateOf(0f) }
    var down by remember { mutableStateOf(false) }
    var radius by remember { mutableStateOf(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }
    var appliedAngle by remember { mutableStateOf(0f) }


    LaunchedEffect(viewModel.progress, player.duration) {
        songProgress.floatValue =
            (((player.currentPosition.toFloat() / player.duration.toFloat()) * 300f))
//        repeatMode.intValue = player.repeatMode
//        shuffleMode.value = player.shuffleModeEnabled
        duration.longValue = player.duration
        appliedAngle = songProgress.floatValue
    }

    LaunchedEffect(key1 = angle) {
        var a = angle
        a += -120
        if (a <= 0f) {
            a += 360
        }
        a = a.coerceIn(0f, 300f)
        if (last < 150f && a == 300f) {
            a = 0f
        }
        last = a
        appliedAngle = a

        if (down) {
            val newPosition = (a / 300f * player.duration).toLong()
            player.seekTo(newPosition)
        }
    }
    LaunchedEffect(key1 = appliedAngle) {
        Log.d("applied", appliedAngle.toString())
        onChange?.invoke(appliedAngle / 300f)
    }
    Canvas(
        modifier = modifier
            .onGloballyPositioned {
                width = it.size.width
                height = it.size.height
                center = Offset(width / 2f, height / 2f)
                radius =
                    min(width.toFloat(), height.toFloat()) / 2f - padding - foreGroundStroke / 2f
            }
            .pointerInteropFilter {
                val x = it.x
                val y = it.y
                val offset = Offset(x, y)
                when (it.action) {

                    MotionEvent.ACTION_DOWN -> {
                        val d = distance(offset, center)
                        val a = angle(center, offset)
                        if (d >= radius - touchStroke / 2f && d <= radius + touchStroke / 2f && a !in 60f..120f) {
                            down = true
                            angle = a
                        } else {
                            down = false
                        }

                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (down) {
                            angle = angle(center, offset)
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        down = false

                    }

                    else -> return@pointerInteropFilter false
                }
                return@pointerInteropFilter true
            }
    ) {
        // Draw background arc
        drawArc(
            color = backgroundColor,
            startAngle = -60f, // Same start angle for background and foreground
            sweepAngle = 300f,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            useCenter = false,
            style = Stroke(
                width = backgroundStroke, // Different stroke width for background
                cap = cap
            )
        )

        drawArc(
            color = progressColor,
            startAngle = -60f, // Same start angle for background and foreground
            sweepAngle = appliedAngle,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            useCenter = false,
            style = Stroke(
                width = foreGroundStroke, // Different stroke width for foreground
                cap = cap
            )
        )

        drawCircle(
            color = thumbColor,
            radius = thumbStroke,
            center = center + Offset(
                radius * cos((-60 + appliedAngle) * PI / 180f).toFloat(),
                radius * sin((-60 + appliedAngle) * PI / 180f).toFloat()
            )
        )
        if (debug) {
            drawRect(
                color = Color.Green,
                topLeft = Offset.Zero,
                size = Size(width.toFloat(), height.toFloat()),
                style = Stroke(
                    4f
                )
            )
            drawRect(
                color = Color.Red,
                topLeft = Offset(padding, padding),
                size = Size(width.toFloat() - padding * 2, height.toFloat() - padding * 2),
                style = Stroke(
                    4f
                )
            )
            drawRect(
                color = Color.Blue,
                topLeft = Offset(padding, padding),
                size = Size(width.toFloat() - padding * 2, height.toFloat() - padding * 2),
                style = Stroke(
                    4f
                )
            )
            drawCircle(
                color = Color.Red,
                center = center,
                radius = radius + foreGroundStroke / 2f,
                style = Stroke(2f)
            )
            drawCircle(
                color = Color.Red,
                center = center,
                radius = radius - foreGroundStroke / 2f,
                style = Stroke(2f)
            )
        }
    }
}

fun angle(center: Offset, offset: Offset): Float {
    val rad = atan2(center.y - offset.y, center.x - offset.x)
    val deg = Math.toDegrees(rad.toDouble())
    return deg.toFloat()
}

fun distance(first: Offset, second: Offset): Float {
    return sqrt((first.x - second.x).square() + (first.y - second.y).square())
}

fun normalizeAngle(angle: Float): Float {
    var normalizedAngle = angle % 300
    if (normalizedAngle < 0) {
        normalizedAngle += 300
    }
    return normalizedAngle
}

fun Float.square(): Float {
    return this * this
}