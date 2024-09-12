package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.BlurHelpers
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.startService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.CustomSongSlider
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.SongControlButtons
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.MainScreen.BlurHelper
import com.SoundScapeApp.soundscape.ui.theme.BrightGray
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.secondTest2
import java.io.FileNotFoundException


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AudioPlayingScreen1(
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
    onBackClick: () -> Unit = {}

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
                title = {},
                actions = {
                    IconButton(onClick = {
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90
                        )
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
                                    }
                                )
                            }
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
                        BlurHelper.blur(context, drawableResId = R.drawable.sample, 25f)
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
                                error(R.drawable.sample)
                                placeholder(R.drawable.sample)
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
                    .padding(top = 0.dp, start = 18.dp, end = 18.dp, bottom = 32.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .border(.5.dp, Color.White.copy(.4f), shape = RoundedCornerShape(12.dp))
                        .weight(.7f)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(data = if (isMainActivity) currentPlayingSong?.artwork else currentIntentMediaItem.value?.artwork)
                                .apply(block = fun ImageRequest.Builder.() {
                                    error(R.drawable.sample)
                                }
                                ).build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop)
                }
                Spacer(modifier = Modifier.height(34.dp))

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
                        .width(280.dp)
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
                        if (currentPlayingSong?.artist == "<unknown>" || currentPlayingSong?.artist == null) "Unknown Artist" else currentPlayingSong!!.artist
                    } else currentIntentMediaItem.value?.artist ?: "Unknown Artist",
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = White50,
                    modifier = Modifier
                        .width(280.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        viewModel.toggleFavorite(current.longValue)
                        viewModel.getFavoritesSongs()
                    })
                    {
                        Icon(
                            imageVector = if (current.longValue in currentPlayListSongs) {
                                Icons.Default.Favorite // Red heart icon
                            } else {
                                Icons.Default.FavoriteBorder // White heart icon
                            },
                            contentDescription = null,
                            tint = if (current.longValue in currentPlayListSongs) Color.Red else White90.copy(.8f),
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = {
                        if (isMainActivity) {
                            viewModel.shareAudio(
                                context,
                                currentPlayingSong!!.uri,
                                currentPlayingSong!!.title
                            )

                        } else {
                            viewModel.shareAudio(
                                context,
                                currentIntentMediaItem.value!!.uri,
                                currentIntentMediaItem.value!!.title
                            )
                        }
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = White90.copy(.8f)
                        )
                    }
                }

                CustomSongSlider(
                    onProgress = onProgress,
                    player = player,
                    viewModel = viewModel,
                    isMainActivity = isMainActivity
                )

                Spacer(modifier = Modifier.height(20.dp))

                SongControlButtons(
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
                    isMainActivity = isMainActivity
                )
            }
        }
    }
}