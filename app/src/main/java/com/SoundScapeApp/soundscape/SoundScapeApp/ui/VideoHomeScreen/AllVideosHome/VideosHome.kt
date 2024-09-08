package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.CustomTabIndicator
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.AllVideos.AllVideos
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Folders.AllFolders
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Movies.AllMovies
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Playlists.VideoPlayListsScreen
import com.SoundScapeApp.soundscape.ui.theme.White90
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideosHome(
    navController: NavController,
    viewModel: VideoViewModel,
    onVideoItemClick: (Int, Long) -> Unit,
    onVideoDelete:(List<Uri>)->Unit
) {

    val videoList by viewModel.videoList.collectAsState()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Videos", "Folders", "Playlists", "Movies")

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })


    val state by remember {
        derivedStateOf { pagerState.currentPage }
    }


    val current = remember {
        mutableLongStateOf(0L)
    }


    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var search by remember {
        mutableStateOf("")
    }


    val placeholderText = when (pagerState.currentPage) {
        0 -> {
            "Search Video"
        }

        1 -> {
            "Search Folder"
        }

        2 -> {
            "Search Playlist"
        }

        3 -> {
            "Search Movie"
        }

        else -> {
            ""
        }
    }

    //SELECTION SECTION
    //1-PLAYLIST
    val selectedPlaylists = remember { mutableStateMapOf<Long, Boolean>() }
    val isPlaylistSelected = viewModel.isPlaylistSelected.collectAsState()
    val confirmPlaylistDeletion = remember {
        mutableStateOf(false)
    }

//    //2-ALL Videos
    val selectedVideos = remember { mutableStateMapOf<Long, Boolean>() }
    val isVideoSelected = viewModel.isVideoSelected.collectAsState()
    val confirmAddVideo = remember {
        mutableStateOf(false)
    }

    val isMovieSelected = viewModel.isMovieSelected.collectAsState()
    val selectedMovies = remember { mutableStateMapOf<Long, Boolean>() }
    val confirmAddMovie = remember {
        mutableStateOf(false)
    }

    val selectAllVideosClicked = remember { mutableStateOf(false) }
    val selectAllPlaylistsClicked = remember { mutableStateOf(false) }
    val selectAllMoviesClicked = remember { mutableStateOf(false) }

    val deleteVideosClicked = remember { mutableStateOf(false) }
    val selectedVideoIds = remember { mutableStateListOf<Long>() }
    val selectedVideosCount = remember { mutableStateOf(0) }



    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            VideosHomeTopBar(
                navController = navController,
                searchValue = search,
                onValueChange = {
                    search = it
                },
                placeholderText = placeholderText,
                isPlaylistSelected = isPlaylistSelected.value,
                isVideoSelected = isVideoSelected.value,
                isMovieSelected = isMovieSelected.value,
                onPlaylistClear = {
                    selectedPlaylists.clear()
                    viewModel.setIsPlaylistSelected(false)
                    selectAllPlaylistsClicked.value = false
                },
                onPlaylistDelete = {
                    confirmPlaylistDeletion.value = true
                },
                onVideoClear = {
                    selectedVideos.clear()
                    viewModel.setIsVideoSelected(false)
                    selectAllVideosClicked.value = false
                },
                onAddVideo = {
                    confirmAddVideo.value = true
                },
                onMovieClear = {
                    selectedMovies.clear()
                    viewModel.isMovieSelected(false)
                    selectAllMoviesClicked.value = false

                },
                onMovieAdd = {
                    confirmAddMovie.value = true
                },
                onVideoDelete = {
                    deleteVideosClicked.value = true
                    viewModel.setSelectedVideos(selectedVideoIds.toMutableList())
                },
                onSelectAllVideos = { selectAllVideosClicked.value = true },
                onSelectAllMovies = { selectAllMoviesClicked.value = true },
                onSelectAllPlaylist = { selectAllPlaylistsClicked.value = true },
                selectedVideosCount = selectedVideosCount,
                onVideoShare = {
                    val selectedVideosList = selectedVideos
                        .filter { it.value } // Filter out only the selected songs
                        .map { it.key } // Extract the IDs of the selected songs

                    val selectedVideoURIs = videoList
                        .filter { selectedVideosList.contains(it.id) } // Filter selected songs
                        .map { video -> video.uri.toUri() } // Map each song to its URI

                    val selectedTitle = videoList
                        .filter { selectedVideosList.contains(it.id) } // Filter selected songs
                        .map { video -> video.displayName } // Map each song to its URI

                    viewModel.shareVideos(context, selectedVideoURIs, selectedTitle)
                    viewModel.setIsVideoSelected(false)
                    selectedVideos.clear()
                }
                )
        })
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(bottom = 0.dp)
        ) {
            val indicator = @Composable { tabPosition: List<TabPosition> ->
                CustomTabIndicator(tabsPos = tabPosition, pagerState = pagerState)
            }

            // TabRow with tabs
            TabRow(
                containerColor = Color.Transparent,
                selectedTabIndex = (state),
                indicator = indicator,
                divider = {
                    HorizontalDivider(
                        thickness = .7.dp,
                        color = White90
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
                                    page = index,
                                    animationSpec = spring(dampingRatio = 1f, stiffness = 500f)
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                    ) {
                        Text(
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
                        //lowVelocityAnimationSpec = LowVelocityAnimationSpec,
                        //highVelocityAnimationSpec = rememberSplineBasedDecay(),
                        snapAnimationSpec = spring(stiffness = Spring.StiffnessLow)
                    ),
                    //beyondBoundsPageCount = 4,
                    state = pagerState,
                    pageSize = PageSize.Fill,
                    modifier = Modifier
                        .fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            AllVideos(
                                viewModel = viewModel,
                                navController = navController,
                                selectedVideos = selectedVideos,
                                confirmAddVideo = confirmAddVideo,
                                search = search,
                                onVideoItemClick = onVideoItemClick,
                                onSelectAllVideosClicked = selectAllVideosClicked,
                                onVideoDelete = onVideoDelete,
                                selectedVideosCount = selectedVideosCount,
                                selectedVideosIds = selectedVideoIds,
                                deleteSelectedVideo = deleteVideosClicked

                            )
                            clearVideoLists(
                                selectedPlaylists = selectedPlaylists,
                                selectedVideos = selectedVideos,
                                selectedMovies = selectedMovies,
                                pagerState = pagerState,
                                onSelectAllVideos = selectAllVideosClicked,
                                onSelectAllMovies = selectAllMoviesClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsPlaylistSelected(false)
                            viewModel.isMovieSelected(false)
                        }

                        1 -> {
                            AllFolders(
                                viewModel = viewModel,
                                navController = navController,
                                search = search
                            )
                            clearVideoLists(
                                selectedPlaylists = selectedPlaylists,
                                selectedVideos = selectedVideos,
                                selectedMovies = selectedMovies,
                                pagerState = pagerState,
                                onSelectAllVideos = selectAllVideosClicked,
                                onSelectAllMovies = selectAllMoviesClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsPlaylistSelected(false)
                            viewModel.setIsVideoSelected(false)
                            viewModel.isMovieSelected(false)
                        }

                        2 -> {
                            VideoPlayListsScreen(
                                navController = navController,
                                viewModel = viewModel,
                                search = search,
                                selectedPlaylists = selectedPlaylists,
                                confirmPlaylistDeletion = confirmPlaylistDeletion,
                                onSelectAllPlaylistClicked = selectAllPlaylistsClicked
                            )
                            clearVideoLists(
                                selectedPlaylists = selectedPlaylists,
                                selectedVideos = selectedVideos,
                                selectedMovies = selectedMovies,
                                pagerState = pagerState,
                                onSelectAllVideos = selectAllVideosClicked,
                                onSelectAllMovies = selectAllMoviesClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsVideoSelected(false)
                            viewModel.isMovieSelected(false)
                        }

                        3 -> {
                            AllMovies(
                                navController = navController,
                                viewModel = viewModel,
                                selectedMovies = selectedMovies,
                                confirmAddMovie = confirmAddMovie,
                                search = search,
                                onSelectAllMoviesClicked = selectAllMoviesClicked
                            )
                            clearVideoLists(
                                selectedPlaylists = selectedPlaylists,
                                selectedVideos = selectedVideos,
                                selectedMovies = selectedMovies,
                                pagerState = pagerState,
                                onSelectAllVideos = selectAllVideosClicked,
                                onSelectAllMovies = selectAllMoviesClicked,
                                onSelectAllPlaylists = selectAllPlaylistsClicked
                            )
                            viewModel.setIsPlaylistSelected(false)
                            viewModel.setIsVideoSelected(false)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
private fun clearVideoLists(
    selectedPlaylists: MutableMap<Long, Boolean>,
    selectedVideos: MutableMap<Long, Boolean>,
    selectedMovies: MutableMap<Long, Boolean>,
    pagerState: PagerState,
    onSelectAllVideos: MutableState<Boolean>,
    onSelectAllPlaylists: MutableState<Boolean>,
    onSelectAllMovies: MutableState<Boolean>
) {
    when (pagerState.currentPage) {
        0 -> {
            selectedPlaylists.clear()
            selectedMovies.clear()
            onSelectAllPlaylists.value = false
            onSelectAllMovies.value = false
        }

        1 -> {
            selectedVideos.clear()
            selectedPlaylists.clear()
            selectedMovies.clear()
            onSelectAllPlaylists.value = false
            onSelectAllMovies.value = false
            onSelectAllVideos.value = false
        }

        2 -> {
            selectedVideos.clear()
            selectedMovies.clear()
            onSelectAllMovies.value = false
            onSelectAllVideos.value = false
        }

        3 -> {
            selectedPlaylists.clear()
            selectedVideos.clear()
            onSelectAllPlaylists.value = false
            onSelectAllVideos.value = false
        }
    }
}
