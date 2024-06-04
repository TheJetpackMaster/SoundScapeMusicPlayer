package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults.LowVelocityAnimationSpec
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.PlaybackState
import com.SoundScapeApp.soundscape.SoundScapeApp.service.MusicService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.Albums.AlbumsScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.AllSongs.AllSongs
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.Artists.ArtistsScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.PlayListsScreen
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.startService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.PurpleGrey80
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.color1
import com.SoundScapeApp.soundscape.ui.theme.color2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.Permission


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongsHome(
    navController: NavController,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int, Long) -> Unit,
    player: ExoPlayer,
    viewModel: AudioViewModel,
    context: Context,
    mediaSession: MediaSession,
    onSongDelete: (List<Uri>) -> Unit
) {

    val isPermissionGranted = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                isPermissionGranted(context, Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        )
    }


    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Playlists", "Albums", "Artists")

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })

    val state by remember {
        derivedStateOf { pagerState.currentPage }
    }


    var playbackState = viewModel.retrievePlaybackState()

    val shouldReloadPlay = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        playbackState = viewModel.retrievePlaybackState()

    }

    val current = remember {
        mutableLongStateOf(0L)
    }

    Log.d("lastplayed", current.longValue.toString())

    val currentPlayingSong: Audio? by remember(audioList, playbackState.lastPlayedSong) {
        derivedStateOf {
            audioList.find { it.id == (playbackState.lastPlayedSong.toLongOrNull() ?: 0L) }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()


    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = currentPlayingSong?.artwork)
            .apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                error(R.drawable.sample)
            }
            ).build()
    )

    var search by remember {
        mutableStateOf("")
    }

    val isFirstVisibleIndexGreaterThan15 = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex in 16..audioList.size / 2
        }
    }
    val isFirstVisibleIndexLessThan15 = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex in 16..audioList.size - 20
        }
    }

    val showInScreens = listOf(
        BottomNavScreenRoutes.SongsHome,
        BottomNavScreenRoutes.VideosHome,
        BottomNavScreenRoutes.Settings
    )

    val placeholderText = when (pagerState.currentPage) {
        0 -> {
            "Search Song"
        }

        1 -> {
            "Search Playlist"
        }

        2 -> {
            "Search Album"
        }

        3 -> {
            "Search Artist"
        }

        else -> {
            ""
        }
    }

    //SELECTION SECTION
    //1-PLAYLIST
    val isPlaylistSelected by viewModel.isPlaylistSelected.collectAsState()
    val selectedPlaylists = remember { mutableStateMapOf<Long, Boolean>() }
    val confirmPlaylistDeletion = remember {
        mutableStateOf(false)
    }


    //2-ALL SONGLIST
    val isSongSelected by viewModel.isSongSelected.collectAsState()
    val selectedSongs = remember { mutableStateMapOf<Long, Boolean>() }
    val confirmAddSong = remember {
        mutableStateOf(false)
    }

    val selectAllSongsClicked = remember { mutableStateOf(false) }
    val selectAllPlaylistsClicked = remember { mutableStateOf(false) }

    val deleteSongsClicked = remember { mutableStateOf(false) }
    val selectedSongsCount = remember { mutableStateOf(0) }
    val selectedSongIds = remember { mutableStateListOf<Long>() }


    // Showing Playing bar
    val songs = viewModel.scannedAudioList.collectAsState()
    val currentSong = playbackState.lastPlayedSong.toLongOrNull() ?: 0L
    val nowPlaying = songs.value.firstOrNull { it.id == currentSong }
    val lastPlayedSongId = playbackState.lastPlayedSong.toLongOrNull() ?: 0L
    val isAvailable = audioList.any { it.id == lastPlayedSongId }

    val showPlayingBar =
        viewModel.retrievePlaybackState().lastPlayedSong != "0" && isPermissionGranted.value && nowPlaying != null && isAvailable


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            SongsHomeTopAppBar(
                searchValue = search,
                onValueChange = {
                    search = it
                },
                placeholderText = placeholderText,
                isPlaylistSelected = isPlaylistSelected,
                isSongSelected = isSongSelected,
                onPlaylistClear = {
                    selectedPlaylists.clear()
                    selectAllPlaylistsClicked.value = false
                },
                onPlaylistDelete = {
                    confirmPlaylistDeletion.value = true
                },
                onSongClear = {
                    selectedSongs.clear()
                    selectedSongsCount.value = 0
                    selectedSongIds.clear()
                    viewModel.setIsSongSelected(false)
                    selectAllSongsClicked.value = false
                },
                onAddSong = {
                    confirmAddSong.value = true
                },
                onSelectAllSongs = {
                    selectAllSongsClicked.value = true
                },
                onSelectAllPlaylist = { selectAllPlaylistsClicked.value = true },
                onSongDelete = {
                    deleteSongsClicked.value = true
                    viewModel.setSelectedSongs(selectedSongIds.toMutableList())
                },
                navController = navController,
                selectedSongsCount = selectedSongsCount
            )
        })
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(bottom = 0.dp)
        ) {
            // TabRow with tabs

            val indicator = @Composable { tabPosition: List<TabPosition> ->
                CustomTabIndicator(tabsPos = tabPosition, pagerState = pagerState)
            }

            TabRow(
                containerColor = Color.Transparent,
                selectedTabIndex = state,
                indicator = indicator,
                divider = {
                    HorizontalDivider(
                        thickness = .7.dp,
                        color = PurpleGrey80
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->

                    val textColor by animateColorAsState(
                        if (state == index) Color.White else White90.copy(.6f), label = ""
                    )

                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    index,
                                    animationSpec = spring(dampingRatio = 1f, stiffness = 500f)
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                            .zIndex(2f)
                    ) {
                        Text(
                            style = SoundScapeThemes.typography.bodyLarge,
                            fontWeight = if (state == index) FontWeight.Medium else FontWeight.Normal,
                            text = title,
                            color = textColor
                        )
                    }
                }
            }
            // Content for each tab using Pager
            Box {
                HorizontalPager(
                    flingBehavior = flingBehavior(
                        snapPositionalThreshold = .1f,
                        state = pagerState,
                        lowVelocityAnimationSpec = LowVelocityAnimationSpec,
                        highVelocityAnimationSpec = rememberSplineBasedDecay(),
                        snapAnimationSpec = spring(stiffness = Spring.StiffnessLow)
                    ),
                    beyondBoundsPageCount = 4,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            AllSongs(
                                listState = listState,
                                onItemClick = onItemClick,
                                navController = navController,
                                context = context,
                                viewModel = viewModel,
                                selectedSongs = selectedSongs,
                                confirmAddSong = confirmAddSong,
                                search = search,
                                onSelectAllSongsClicked = selectAllSongsClicked,
                                deleteSelectedSong = deleteSongsClicked,
                                onSongDelete = onSongDelete,
                                selectedSongsCount = selectedSongsCount,
                                selectedSongsIds = selectedSongIds,
                                isPlayingBarShown = showPlayingBar

                            )
                            clearLists(
                                selectedPlaylists, selectedSongs, pagerState,
                                onSelectAllSongs = selectAllSongsClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsPlaylistSelected(false)

                        }

                        1 -> {
                            PlayListsScreen(
                                navController = navController,
                                viewModel = viewModel,
                                search = search,
                                selectedPlaylists = selectedPlaylists,
                                confirmPlaylistDeletion = confirmPlaylistDeletion,
                                onSelectAllPlaylistsClicked = selectAllPlaylistsClicked,
                                isPlayingBarShown = showPlayingBar

                            )
                            clearLists(
                                selectedPlaylists, selectedSongs, pagerState,
                                onSelectAllSongs = selectAllSongsClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsSongSelected(false)
                        }

                        2 -> {
                            AlbumsScreen(
                                viewModel = viewModel,
                                audioList = audioList,
                                navController = navController,
                                search = search

                            )
                            clearLists(
                                selectedPlaylists, selectedSongs, pagerState,
                                onSelectAllSongs = selectAllSongsClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsPlaylistSelected(false)
                            viewModel.setIsSongSelected(false)
                        }

                        3 -> {
                            ArtistsScreen(
                                audioList = audioList,
                                viewModel = viewModel,
                                navController = navController,
                                context = context,
                                search = search
                            )
                            clearLists(
                                selectedPlaylists, selectedSongs, pagerState,
                                onSelectAllSongs = selectAllSongsClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsPlaylistSelected(false)
                            viewModel.setIsSongSelected(false)
                        }
                    }
                }

                val screens = listOf(
                    BottomNavScreenRoutes.SongsHome,
                    BottomNavScreenRoutes.VideosHome,
                    BottomNavScreenRoutes.Settings
                )
                val showBottomBar = navController
                    .currentBackStackEntryAsState().value?.destination?.route in screens.map { it.route }


                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .padding(
                            bottom = if (showBottomBar) 8.dp else 72.dp,
                            start = 18.dp,
                            end = 18.dp
                        )
                ) {

                    FastScrollButton(
                        isFirstVisibleIndexGreaterThan15 = isFirstVisibleIndexGreaterThan15,
                        isFirstVisibleIndexLessThan15 = isFirstVisibleIndexLessThan15,
                        pagerState = pagerState,
                        listState = listState,
                        coroutineScope = coroutineScope,
                        audioList = audioList,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    if (showPlayingBar) {
                        MainPlayingBar(
                            navController = navController,
                            painter = painter,
                            onStart = onStart,
                            player = player,
                            current = current,
                            currentPlayingSong = currentPlayingSong,
                            viewModel = viewModel,
                            context = context,
                            shouldReloadPlay = shouldReloadPlay,
                            isPermissionGranted = isPermissionGranted
                        )
                    }
//                    }
                }
            }
        }
    }
}

@Composable
fun MainPlayingBar(
    navController: NavController,
    painter: Painter,
    currentPlayingSong: Audio? = null,
    onStart: () -> Unit,
    player: ExoPlayer,
    current: MutableState<Long>,
    viewModel: AudioViewModel,
    context: Context,
    shouldReloadPlay: MutableState<Boolean>,
    isPermissionGranted: MutableState<Boolean>
) {
    val isPlaying = remember {
        mutableStateOf(false)
    }
    val songDuration = remember { mutableStateOf(0L) }

    LaunchedEffect(player.currentMediaItem, player.isPlaying) {
        current.value = player.currentMediaItem?.mediaId?.toLongOrNull() ?: -1
        isPlaying.value = player.isPlaying
        songDuration.value = player.duration
    }

    val songProgressValue = remember {
        mutableStateOf(0f)
    }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    onClick = {
                        val playbackState = viewModel.retrievePlaybackState()
                        if (playbackState.lastPlayedSong != "0" && !shouldReloadPlay.value && !isMediaSessionServiceRunning(
                                context
                            )
                        ) {
                            // Resume playback from the last saved position
                            viewModel.restorePlaybackState(playbackState, context = context)
                            player.pause()
                            isPlaying.value = !isPlaying.value
                            shouldReloadPlay.value = true
                        }
                        if (navController.currentBackStackEntry?.lifecycle?.currentState
                            == Lifecycle.State.RESUMED
                        ) {
                            navController.navigate(ScreenRoute.NowPlayingScreen.route)
                        }
                    })
                .background(
//                    SoundScapeThemes.colorScheme.primary
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SoundScapeThemes.colorScheme.primary,
                            SoundScapeThemes.colorScheme.primary,
                        )
                    )
                )
                .border(.1.dp, White50.copy(.2f), RoundedCornerShape(10.dp)),

            verticalAlignment = Alignment.CenterVertically
        ) {


        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    onClick = {
                        val playbackState = viewModel.retrievePlaybackState()
                        if (playbackState.lastPlayedSong != "0" && !shouldReloadPlay.value && !isMediaSessionServiceRunning(
                                context
                            )
                        ) {
                            // Resume playback from the last saved position
                            viewModel.restorePlaybackState(playbackState, context = context)
                            player.pause()
                            isPlaying.value = !isPlaying.value
                            shouldReloadPlay.value = true
                        }
                        if (navController.currentBackStackEntry?.lifecycle?.currentState
                            == Lifecycle.State.RESUMED
                        ) {
                            navController.navigate(ScreenRoute.NowPlayingScreen.route)
                        }
                    })
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                RotatingImage(
                    painter = painter,
                    player = player,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = currentPlayingSong?.displayName ?: "No song playing",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = White90,
                    modifier = Modifier.width(160.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = currentPlayingSong?.artist ?: "Unknown",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = White50,
                    modifier = Modifier.width(160.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable(onClick = {
                        val playbackState = viewModel.retrievePlaybackState()
                        if (playbackState.lastPlayedSong != "0" && !shouldReloadPlay.value && !isMediaSessionServiceRunning(
                                context
                            )
                        ) {
                            // Resume playback from the last saved position
                            viewModel.restorePlaybackState(playbackState, context = context)
                            player.play()
                            isPlaying.value = !isPlaying.value
                            startService(context)
                            shouldReloadPlay.value = true
                        } else {
                            onStart()
                            isPlaying.value = player.isPlaying
                        }
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(id = if (isPlaying.value) R.drawable.pauseicon else R.drawable.playicon),
                    contentDescription = "play pause button",
                    modifier = Modifier.size(18.dp),
                    tint = White90
                )

                CustomCircularProgressIndicator(
                    player = player,
                    viewModel = viewModel,
                    currentPlayingSong,
                    isPermissionGranted = isPermissionGranted,
                    songProgressValue = songProgressValue
                )
            }
        }
    }
}


@Composable
fun CustomCircularProgressIndicator(
    player: ExoPlayer,
    viewModel: AudioViewModel,
    currentPlayingSong: Audio?,
    isPermissionGranted: MutableState<Boolean>,
    songProgressValue: MutableState<Float>
) {

    val playbackState = viewModel.retrievePlaybackState()
    val songs = viewModel.scannedAudioList.collectAsState()
    val currentSong = playbackState.lastPlayedSong.toLongOrNull() ?: 0L


    val nowPlaying = songs.value.firstOrNull { it.id == currentSong }

    val songProgress = remember {
        mutableFloatStateOf(
            if (isPermissionGranted.value && nowPlaying != null) {
                playbackState.lastPlaybackPosition.toFloat() / nowPlaying.duration
            } else {
                0f
            }
        )
    }


    if (player.isPlaying) {
        LaunchedEffect(viewModel.progress) {
            songProgress.floatValue =
                (player.currentPosition.toFloat() / player.duration.toFloat())
            songProgressValue.value = (player.currentPosition.toFloat() / player.duration.toFloat())
        }
    }

    CircularProgressIndicator(
        progress = { songProgress.floatValue },
        modifier = Modifier.size(44.dp),
        strokeWidth = 2.5.dp,
        trackColor = White90,
        color = SoundScapeThemes.colorScheme.onSecondary,
        strokeCap = StrokeCap.Round,
    )
}


@OptIn(ExperimentalFoundationApi::class)
private fun clearLists(
    selectedPlaylists: MutableMap<Long, Boolean>,
    selectedSongs: MutableMap<Long, Boolean>,
    pagerState: PagerState,
    onSelectAllSongs: MutableState<Boolean>,
    onSelectAllPlaylists: MutableState<Boolean>

) {
    when (pagerState.currentPage) {
        0 -> {
            selectedPlaylists.clear()
            onSelectAllPlaylists.value = false
        }

        1 -> {
            selectedSongs.clear()
            onSelectAllSongs.value = false
        }

        2 -> {
            selectedPlaylists.clear()
            selectedSongs.clear()
            onSelectAllSongs.value = false
            onSelectAllPlaylists.value = false
        }

        3 -> {
            selectedPlaylists.clear()
            selectedSongs.clear()
            onSelectAllSongs.value = false
            onSelectAllPlaylists.value = false
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomTabIndicator(
    tabsPos: List<TabPosition>,
    pagerState: PagerState
) {
    val transition =
        updateTransition(targetState = pagerState.currentPageOffsetFraction, label = "")

    val indicatorStart by transition.animateDp(
        transitionSpec = {
            spring(
                dampingRatio = 1f, // Adjust the damping ratio for smoother movement
                stiffness = 400f // Adjust the stiffness for the desired speed of movement
            )
        }, label = ""
    ) {
        // Calculate the left position of the indicator based on the currentPageOffset
        val currentIndex = pagerState.currentPage
        val nextIndex =
            if (pagerState.currentPage < pagerState.pageCount - 1) currentIndex + 1 else currentIndex
        val startX = tabsPos[currentIndex].left
        val endX = tabsPos[nextIndex.coerceAtMost(tabsPos.size - 1)].left
        startX + (endX - startX) * it
    }

    val currentIndex = pagerState.currentPage
    val indicatorWidth = tabsPos[currentIndex].width // Use the width of the current tab

    Box(
        modifier = Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(Alignment.BottomStart)
            .width(indicatorWidth)
            .padding(start = 4.dp, end = 4.dp, top = 2.dp)
            .height(3.dp)
            .background(White90, RoundedCornerShape(50))
    )
}


//@Composable
//fun RotatingImage(
//    painter: Painter,
//    modifier: Modifier = Modifier
//) {
//    Image(
//        painter = painter,
//        contentDescription = null,
//        modifier = modifier
//            .size(44.dp)
//            .clip(CircleShape),
//        contentScale = ContentScale.Crop
//    )
//}


@Composable
fun RotatingImage(
    painter: Painter,
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {

    var rotationAngle by remember { mutableStateOf(0f) }
    val rotationValue = remember { Animatable(rotationAngle) }


    LaunchedEffect(player.isPlaying) {
        if (player.isPlaying) {
            rotationValue.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 6000,
                        easing = LinearEasing,
                        delayMillis = 0
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotationAngle = 0f
            rotationValue.stop()
        }
    }

    Image(
        painter = painter,
        contentDescription = "playing song rotating art",
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .graphicsLayer {
                rotationZ = rotationValue.value
            },
        contentScale = ContentScale.Crop
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FastScrollButton(
    isFirstVisibleIndexGreaterThan15: State<Boolean>,
    isFirstVisibleIndexLessThan15: State<Boolean>,
    pagerState: PagerState,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    audioList: List<Audio>,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(38.dp)
            .padding(bottom = 4.dp)
            .clip(CircleShape)
            .background(
                if ((isFirstVisibleIndexGreaterThan15.value || isFirstVisibleIndexLessThan15.value) && pagerState.currentPage == 0) {
                    Color.Black.copy(.6f)
                } else Color.Transparent
            )
    ) {
        if (isFirstVisibleIndexGreaterThan15.value && pagerState.currentPage == 0) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(28.dp),
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            audioList.lastIndex
                        )
                    }
                })
            {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "scroll all song list down button",
                    modifier = Modifier.size(24.dp),
                    tint = White90
                )
            }
        } else if (isFirstVisibleIndexLessThan15.value && pagerState.currentPage == 0) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(28.dp),
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            0,
                        )
                    }
                })
            {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "scroll all songs list up button",
                    modifier = Modifier.size(24.dp),
                    tint = White90
                )
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun isMediaSessionServiceRunning(context: Context): Boolean {
    val serviceClass = MusicService::class.java
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

//
//Row(
//modifier = Modifier
//.fillMaxWidth()
//.height(62.dp)
//.clip(RoundedCornerShape(10.dp))
//.clickable(
//onClick = {
//    val playbackState = viewModel.retrievePlaybackState()
//    if (playbackState.lastPlayedSong != "0" && !shouldReloadPlay.value && !isMediaSessionServiceRunning(
//            context
//        )
//    ) {
//        // Resume playback from the last saved position
//        viewModel.restorePlaybackState(playbackState, context = context)
//        player.pause()
//        isPlaying.value = !isPlaying.value
//        shouldReloadPlay.value = true
//    }
//    if (navController.currentBackStackEntry?.lifecycle?.currentState
//        == Lifecycle.State.RESUMED
//    ) {
//        navController.navigate(ScreenRoute.NowPlayingScreen.route)
//    }
//})
//.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
//verticalAlignment = Alignment.CenterVertically
//) {
//    Box(
//        contentAlignment = Alignment.Center
//    ) {
//        RotatingImage(
//            painter = painter,
//            player = player,
//        )
//    }
//    Spacer(modifier = Modifier.width(12.dp))
//
//    Column(
//        modifier = Modifier.fillMaxHeight(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.Start
//    ) {
//        Text(
//            text = currentPlayingSong?.displayName ?: "No song playing",
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Medium,
//            color = White90,
//            modifier = Modifier.width(160.dp),
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//        )
//
//        Text(
//            text = currentPlayingSong?.artist ?: "Unknown",
//            fontSize = 13.sp,
//            fontWeight = FontWeight.Medium,
//            color = White50,
//            modifier = Modifier.width(160.dp),
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis
//        )
//    }
//
//    Spacer(modifier = Modifier.weight(1f))
//
//    Box(
//        modifier = Modifier
//            .fillMaxHeight()
//            .size(50.dp)
//            .clip(CircleShape)
//            .clickable(onClick = {
//                val playbackState = viewModel.retrievePlaybackState()
//                if (playbackState.lastPlayedSong != "0" && !shouldReloadPlay.value && !isMediaSessionServiceRunning(
//                        context
//                    )
//                ) {
//                    // Resume playback from the last saved position
//                    viewModel.restorePlaybackState(playbackState, context = context)
//                    player.play()
//                    isPlaying.value = !isPlaying.value
//                    startService(context)
//                    shouldReloadPlay.value = true
//                } else {
//                    onStart()
//                    isPlaying.value = player.isPlaying
//                }
//            }),
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            painterResource(id = if (isPlaying.value) R.drawable.pauseicon else R.drawable.playicon),
//            contentDescription = "play pause button",
//            modifier = Modifier.size(18.dp),
//            tint = White90
//        )
//
//        CustomCircularProgressIndicator(
//            player = player,
//            viewModel = viewModel,
//            currentPlayingSong,
//            isPermissionGranted = isPermissionGranted,
//            songProgressValue = songProgressValue
//        )
//    }
//}