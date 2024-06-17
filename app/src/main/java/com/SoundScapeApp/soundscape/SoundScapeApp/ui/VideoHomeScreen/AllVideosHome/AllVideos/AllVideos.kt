package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.AllVideos

import Video
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.key
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.video.PlaceholderSurface
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.PlayerActivity
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoSortType
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.ShowSongDetailsDialog
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme2Primary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.bumptech.glide.integration.compose.Placeholder
import java.util.concurrent.TimeUnit

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(
    ExperimentalGlideComposeApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AllVideos(
    search: String,
    navController: NavController,
    selectedVideos: MutableMap<Long, Boolean>,
    confirmAddVideo: MutableState<Boolean>,
    viewModel: VideoViewModel,
    onVideoItemClick: (Int, Long) -> Unit,
    onSelectAllVideosClicked:MutableState<Boolean>,
    deleteSelectedVideo: MutableState<Boolean>,
    onVideoDelete: (List<Uri>) -> Unit,
    selectedVideosCount: MutableState<Int>,
    selectedVideosIds:MutableList<Long>
) {
    val context = LocalContext.current

    val videosList by viewModel.scannedVideoList.collectAsState()
    val lazyListState = rememberLazyListState()

    val resumeFromLeftPos by viewModel.resumeFromLeftPositionEnabled.collectAsState()

    val currentSortType = viewModel.currentVideoSortType

    var showSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    val showSort = remember {
        mutableStateOf(false)
    }

    var selectedVideo by remember {
        mutableStateOf<Video?>(null)
    }
//
    val selectedPlaylists = remember { mutableStateMapOf<Long, Boolean>() }
    var enableButton by remember {
        mutableStateOf(false)
    }
//
    var showAddPlaylistDialog by remember {
        mutableStateOf(false)
    }

    val showVideoDetailsDialog = remember{ mutableStateOf(false) }

//
    val playLists by viewModel.videoPlaylists.collectAsState()
//    val currentPlayListSongs by viewModel.favoritesSongs.collectAsState()

//
    val current = remember {
        mutableLongStateOf(0L)
    }
//
    val createPlaylist = remember {
        mutableStateOf(false)
    }
//
    var playlistName by remember {
        mutableStateOf("")
    }
//
    val maxVisiblePlaylists = 4
//

    val isSearch = viewModel.isVideoSearch.collectAsState()

//    val filteredVideoList = remember(search, videosList) {
//        if (search.isNotBlank()) {
//            viewModel.isAppVideoSearch(true)
//            videosList.filter { video ->
//                video.displayName.contains(search, ignoreCase = true)
//            }
//        } else {
//            viewModel.isAppVideoSearch(false)
//            videosList
//        }
//    }


    val filteredVideoList = if (search.isNotBlank()) {
        viewModel.isAppVideoSearch(true)
        videosList.filter { video ->
            video.displayName.contains(search, ignoreCase = true)
        }
    } else {
        viewModel.isAppVideoSearch(false)
        videosList
    }

    LaunchedEffect(onSelectAllVideosClicked.value){
        if(onSelectAllVideosClicked.value){
            filteredVideoList.forEach { video->
                selectedVideos[video.id] = true
            }
        }
    }

    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(deleteSelectedVideo.value) {
        if (deleteSelectedVideo.value) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                showDeleteDialog.value = true
                deleteSelectedVideo.value = false
            } else {
                val selectedVideosList = selectedVideos
                    .filter { it.value } // Filter out only the selected songs
                    .map { it.key } // Extract the IDs of the selected songs

                val selectedVideoURIs = filteredVideoList
                    .filter { selectedVideosList.contains(it.id) } // Filter selected songs
                    .map { video -> video.uri.toUri() } // Map each song to its URI

                onVideoDelete(selectedVideoURIs)
                deleteSelectedVideo.value = false
                selectedVideos.clear()
                selectedVideosIds.clear()
                viewModel.setIsVideoSelected(false)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    bottom = 0.dp,
                )
        ) {
            key(search){
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Text(
                                text = "Total Videos: ${filteredVideoList.size}",
                                color = White90,
                                style = SoundScapeThemes.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = {
                                    showSort.value = true
                                })
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.sorting),
                                    contentDescription = "all videos list sorting button",
                                    modifier = Modifier.size(24.dp),
                                    tint = White90
                                )
                            }
                        }
                    }

                    items(items = filteredVideoList, key = { it.id }) { video ->
                        val isSelected = selectedVideos[video.id] ?: false
                        val id = video.id.toString()
                        val playbackPosition = viewModel.getPlaybackPosition(id)


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp)
                                .background(
                                    if (isSelected) SoundScapeThemes.colorScheme.primary.copy(.9f)
                                    else Color.Transparent
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            if (!selectedVideos.any {
                                                    it.value
                                                }) {
                                                selectedVideos[video.id] =
                                                    !(selectedVideos[video.id] ?: false)

                                                viewModel.setIsVideoSelected(selectedVideos.any { it.value })
                                                selectedVideosCount.value =
                                                    selectedVideos.count { it.value }
                                                toggleVideoSelection(video.id, selectedVideosIds)
                                            }
                                        },
                                        onTap = {
                                            if (selectedVideos.any {
                                                    it.value
                                                }) {
                                                selectedVideos[video.id] =
                                                    !(selectedVideos[video.id] ?: false)

                                                viewModel.setIsVideoSelected(selectedVideos.any { it.value })
                                                selectedVideosCount.value =
                                                    selectedVideos.count { it.value }
                                                toggleVideoSelection(video.id, selectedVideosIds)

                                            } else {
                                                if (!isSearch.value) {
                                                    // Logic for playing video from the original list
                                                    viewModel.setVideoMediaItems(videosList)
                                                    val position =
                                                        viewModel.getPlaybackPosition(video.id.toString())
                                                    val videoDuration = video.duration
                                                    val percentagePlayed =
                                                        (position.toFloat() / videoDuration.toFloat()) * 100

                                                    // Check if more than 95% of the video is played
                                                    if (percentagePlayed > 95) {
                                                        viewModel.removeSavedPlayback(video.id.toString())
                                                    }

                                                    val originalIndex = videosList.indexOf(video)
                                                    viewModel.playVideo(originalIndex)
                                                } else {
                                                    // Logic for playing video from the filtered list
                                                    Log.d("videoid", isSearch.value.toString())
                                                    viewModel.setVideoMediaItemAndPlay(video)

                                                }
                                                viewModel.updateAvailableTracks()
                                                // Pass ViewModel to PlayerActivity
                                                viewModel.resetDefaultResizeMode()
                                                navController.navigate(ScreenRoute.VideoPlayingScreen.route)

                                            }
                                        }
                                    )
                                }
                                .padding(
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    start = 12.dp,
                                    end = 8.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {


                                GlideImage(
                                    model = video.thumbnail,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(125.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .border(.2.dp, White50, RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop,
                                )

                                Text(
                                    text = formatVideoDuration(video.duration.toLong()),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = White90,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(.4f))
                                        .padding(end = 4.dp, start = 4.dp)

                                )

                                if (playbackPosition > 0 && resumeFromLeftPos) {
                                    LinearProgressIndicator(
                                        trackColor = White50,
                                        color = Color.Red.copy(alpha = .8f),
                                        modifier = Modifier
                                            .width(125.dp)
                                            .height(3.dp)
                                            .align(Alignment.BottomStart),
                                        progress = {
                                            val progress = if (playbackPosition > 0) {
                                                // Calculate progress based on playback position and video duration
                                                val duration = video.duration.toFloat()
                                                val position = playbackPosition.toFloat()
                                                position / duration
                                            } else {
                                                0f
                                            }
                                            progress
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = video.displayName,
                                    style = SoundScapeThemes.typography.titleSmall,
                                    color = White90,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(.9f)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${video.sizeMB} MB",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = White50,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    VerticalDivider(
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = video.bucketName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = White50,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }

                            if (selectedVideos.any { it.value }) {
                                OutlinedIconButton(
                                    modifier = Modifier.size(20.dp),
                                    border = BorderStroke(1.dp, White90),
                                    onClick = {
                                        selectedVideos[video.id] =
                                            !(selectedVideos[video.id] ?: false)
                                    })
                                {
                                    if (isSelected
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = White90,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            } else {
                                IconButton(
                                    modifier = Modifier
                                        .size(28.dp),
                                    onClick = {
                                        showSheet = true
                                        selectedVideo = video
                                        current.longValue = video.id
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = null,
                                        tint = White90
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(74.dp))
                    }
                }
            }
            if (showSheet) {
                ModalBottomSheet(
                    containerColor = SoundScapeThemes.colorScheme.secondary,
                    dragHandle = {},
                    onDismissRequest = {
                        showSheet = false
                    },
                    sheetState = sheetState,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 12.dp, bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .padding(start = 12.dp, end = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                GlideImage(
                                    model = selectedVideo?.thumbnail,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(48.dp)
                                        .width(54.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .border(1.dp, White50, RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = selectedVideo!!.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(225.dp)
                                )
                                Text(
                                    text = selectedVideo!!.bucketName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White50,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(200.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalDivider(
                            color = White90,
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable {
                                    selectedVideo.let {
                                        viewModel.setVideoMediaItemAndPlay(selectedVideo!!)
                                    }
                                    navController.navigate(ScreenRoute.VideoPlayingScreen.route)
                                    showSheet = false
//                                    onItemClick(0, selectedSong!!.id)
//                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
                                    showSheet = false
//                                    onItemClick(0, selectedSong!!.id)
//                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
                                }
                                .padding(start = 12.dp, end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedIconButton(
                                border = BorderStroke(1.dp, White90),
                                modifier = Modifier.size(22.dp),
                                onClick = {
                                    selectedVideo.let {
                                        viewModel.setVideoMediaItemAndPlay(selectedVideo!!)
                                    }
                                    navController.navigate(ScreenRoute.VideoPlayingScreen.route)
                                    showSheet = false
//                                    onItemClick(0, selectedSong!!.id)
//                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
                                })
                            {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Play",
                                color = White90,
                                fontSize = 14.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable {
                                    showAddPlaylistDialog = true
                                    showSheet = false
                                }
                                .padding(start = 12.dp, end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedIconButton(
                                border = BorderStroke(1.dp, White90),
                                modifier = Modifier.size(22.dp),
                                onClick = {
                                    showAddPlaylistDialog = true
                                    showSheet = false
                                })
                            {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Add to Playlist",
                                color = White90,
                                fontSize = 14.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable {
                                    showSheet = false
                                    showVideoDetailsDialog.value = true
                                }
                                .padding(start = 12.dp, end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedIconButton(
                                border = BorderStroke(1.dp, White90),
                                modifier = Modifier.size(22.dp),
                                onClick = {
                                    showSheet = false
                                    showVideoDetailsDialog.value = true
                                })
                            {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Details",
                                color = White90,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            if (confirmAddVideo.value || showAddPlaylistDialog) {
                AlertDialog(
                    containerColor = SoundScapeThemes.colorScheme.secondary,
                    onDismissRequest = {
                        showAddPlaylistDialog = false
                        confirmAddVideo.value = false
                        selectedPlaylists.clear()
                        enableButton = selectedPlaylists.any { it.value }
                        onSelectAllVideosClicked.value = false
                    },
                    confirmButton = { /*TODO*/ },
                    title = {
                        Text(
                            text = "Add to",
                            color = White90
                        )
                    },
                    text = {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        createPlaylist.value = true
                                        showSheet = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .border(
                                            1.dp,
                                            White90,
                                            shape = RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = White90
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "New Playlist",
                                    color = White90,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            val boxHeight = if (playLists.size <= maxVisiblePlaylists) {
                                (60.dp * playLists.size).coerceAtMost(250.dp)
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
                                        .wrapContentHeight()
                                ) {
                                    items(items = playLists, key = { it.id }) { playlist ->
                                        val isSelected = selectedPlaylists[playlist.id] ?: false
                                        val firstVideoId = playlist.videoIds.lastOrNull()
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
                                                        selectedPlaylists[playlist.id] = !isSelected
                                                        enableButton =
                                                            selectedPlaylists.any { it.value }
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically

                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(SoundScapeThemes.colorScheme.secondary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                firstVideoId?.let {
                                                    val imageUrl = viewModel.getVideoImageUrl(it)
                                                    GlideImage(
                                                        model = imageUrl,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(top = 6.dp, bottom = 6.dp),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = playlist.name,
                                                    fontSize = 14.sp,
                                                    color = Color.White
                                                )

                                                Text(
                                                    text = "${playlist.videoIds.size} videos",
                                                    fontSize = 14.sp,
                                                    color = White50,
                                                )

                                            }

                                            Spacer(modifier = Modifier.weight(1f))

                                            OutlinedIconButton(
                                                border = BorderStroke(1.dp, White90),
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    containerColor = if (isSelected) White90 else Color.Transparent
                                                ),
                                                modifier = Modifier.size(22.dp),
                                                onClick = {
                                                    selectedPlaylists[playlist.id] = !isSelected
                                                    enableButton =
                                                        selectedPlaylists.any { it.value }
                                                })
                                            {
                                                Icon(
                                                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.Black else White90,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = {
                                        showAddPlaylistDialog = false
                                        confirmAddVideo.value = false
                                        selectedPlaylists.clear()
                                        enableButton = selectedPlaylists.any { it.value }
                                        onSelectAllVideosClicked.value = false

                                    })
                                {
                                    Text(
                                        text = "Cancel",
                                        color = White90
                                    )
                                }

                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        disabledContainerColor = White50.copy(.3f),
                                        containerColor = White90.copy(.8f)
                                    ),
                                    enabled = enableButton,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = {
                                        val selectedPlayListsList =
                                            selectedPlaylists.filter { it.value }.map { it.key }
                                        val selectedVideosList = selectedVideos
                                            .filter { it.value }
                                            .map { it.key }
                                        selectedPlayListsList.forEach { playlistId ->
                                            if (showAddPlaylistDialog) {
                                                viewModel.addVideoToDifferentPlaylist(
                                                    playlistId,
                                                    listOf(selectedVideo?.id ?: -1),
                                                    context
                                                )
                                            } else {
                                                viewModel.addVideoToDifferentPlaylist(
                                                    playlistId,
                                                    selectedVideosList,
                                                    context
                                                )
                                            }
                                        }
                                        selectedPlaylists.clear()
                                        selectedVideos.clear()
                                        enableButton = selectedPlaylists.any { it.value }
                                        showAddPlaylistDialog = false
                                        confirmAddVideo.value = false
                                        onSelectAllVideosClicked.value = false
                                        viewModel.setIsVideoSelected(false)
                                    })
                                {
                                    Text(
                                        text = "Done",
                                        color = SoundScapeThemes.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                )
            }

            if (createPlaylist.value) {
                AlertDialog(
                    containerColor = SoundScapeThemes.colorScheme.secondary,
                    onDismissRequest = { createPlaylist.value = false },
                    title = {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            TextField(
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                value = playlistName,
                                onValueChange = {
                                    playlistName = it
                                },
                                placeholder = {
                                    Text(text = "video Playlist", color = Color.White)
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    modifier = Modifier.width(100.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = {
                                        createPlaylist.value = false
                                        confirmAddVideo.value = false

                                    })
                                {
                                    Text(text = "Cancel")
                                }

                                Button(
                                    modifier = Modifier.width(100.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = {
                                        val selectedVideosList = selectedVideos
                                            .filter { it.value }
                                            .map { it.key }

                                        if (playlistName != "") {
                                            viewModel.saveNewVideoPlaylist(
                                                newPlaylistName = playlistName,
                                            )

                                            if (showAddPlaylistDialog) {
                                                viewModel.createAndAddVideoToPlaylist(
                                                    videoIds = listOf(selectedVideo?.id ?: -1),
                                                    context = context
                                                )
                                            } else {
                                                viewModel.createAndAddVideoToPlaylist(
                                                    videoIds = selectedVideosList,
                                                    context = context
                                                )
                                            }
                                        }
                                        createPlaylist.value = false
                                        showAddPlaylistDialog = false
                                        confirmAddVideo.value = false
                                        selectedVideos.clear()
                                        onSelectAllVideosClicked.value = false
                                        viewModel.setIsVideoSelected(false)
                                    })
                                {
                                    Text(text = "Done")
                                }
                            }
                        }
                    },
                    confirmButton = {

                    },
                    dismissButton = {
                    }
                )
            }

            if (showSort.value) {
                ModalBottomSheet(
                    containerColor = SoundScapeThemes.colorScheme.secondary,
                    dragHandle = {},
                    onDismissRequest = {
                        showSort.value = false
                    },
                    sheetState = sheetState,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 12.dp, bottom = 42.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .padding(start = 16.dp, end = 12.dp)
                                .clickable(
                                    onClick = {
                                        showSort.value = false
                                        viewModel.sortVideoList(VideoSortType.DATE_ADDED_DESC)
                                        viewModel.setVideoSortType(VideoSortType.DATE_ADDED_DESC)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort by date added (Newest)",
                                color =
                                if (currentSortType == VideoSortType.DATE_ADDED_DESC) White50 else White90
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            if (currentSortType == VideoSortType.DATE_ADDED_DESC) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = White50
                                )
                            }

                        }
                        Row(
                            modifier = Modifier
                                .height(46.dp)
                                .padding(start = 16.dp, end = 12.dp)
                                .clickable(
                                    onClick = {
                                        showSort.value = false
                                        viewModel.sortVideoList(VideoSortType.DATE_ADDED_ASC)
                                        viewModel.setVideoSortType(VideoSortType.DATE_ADDED_ASC)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort by date added (Oldest)",
                                color =
                                if (currentSortType == VideoSortType.DATE_ADDED_ASC) White50 else White90
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            if (currentSortType == VideoSortType.DATE_ADDED_ASC) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = White50
                                )
                            }

                        }
                        Row(
                            modifier = Modifier
                                .height(46.dp)
                                .padding(start = 16.dp, end = 12.dp)
                                .clickable(
                                    onClick = {
                                        showSort.value = false
                                        viewModel.sortVideoList(VideoSortType.SIZE_DESC)
                                        viewModel.setVideoSortType(VideoSortType.SIZE_DESC)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort by size (Large)",
                                color =
                                if (currentSortType == VideoSortType.SIZE_DESC) White50 else White90
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            if (currentSortType == VideoSortType.SIZE_DESC) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = White50
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .height(46.dp)
                                .padding(start = 16.dp, end = 12.dp)
                                .clickable(
                                    onClick = {
                                        showSort.value = false
                                        viewModel.sortVideoList(VideoSortType.SIZE_ASC)
                                        viewModel.setVideoSortType(VideoSortType.SIZE_ASC)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort by size (Small)",
                                color =
                                if (currentSortType == VideoSortType.SIZE_ASC) White50 else White90
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            if (currentSortType == VideoSortType.SIZE_ASC) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = White50
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .height(46.dp)
                                .padding(start = 16.dp, end = 12.dp)
                                .clickable(
                                    onClick = {
                                        showSort.value = false
                                        viewModel.sortVideoList(VideoSortType.TITLE_ASC)
                                        viewModel.setVideoSortType(VideoSortType.TITLE_ASC)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort by title (A-Z)",
                                color =
                                if (currentSortType == VideoSortType.TITLE_ASC) White50 else White90
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            if (currentSortType == VideoSortType.TITLE_ASC) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = White50
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .height(46.dp)
                                .padding(start = 16.dp, end = 12.dp)
                                .clickable(
                                    onClick = {
                                        showSort.value = false
                                        viewModel.sortVideoList(VideoSortType.TITLE_DESC)
                                        viewModel.setVideoSortType(VideoSortType.TITLE_DESC)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort by title (Z-A)",
                                color =
                                if (currentSortType == VideoSortType.TITLE_DESC) White50 else White90
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            if (currentSortType == VideoSortType.TITLE_DESC) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = White50
                                )
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog.value) {
                AlertDialog(
                    containerColor = Theme2Primary,
                    onDismissRequest = {
                        showDeleteDialog.value = false
                    },
                    title = {
                        Text(
                            text = "Delete Videos?",
                            color = White90
                        )
                    },
                    text = {
                        Text(
                            text = "${selectedVideosIds.size} videos will be deleted",
                            color = White50
                        )
                    },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            onClick = {
                                val selectedVideosList = selectedVideos
                                    .filter { it.value } // Filter out only the selected songs
                                    .map { it.key } // Extract the IDs of the selected songs

                                val selectedVideoURIs = filteredVideoList
                                    .filter { selectedVideosList.contains(it.id) } // Filter selected songs
                                    .map { video -> video.uri.toUri() } // Map each song to its URI

                                onVideoDelete(selectedVideoURIs)
                                deleteSelectedVideo.value = false
                                selectedVideos.clear()
                                selectedVideosIds.clear()
                                viewModel.setIsVideoSelected(false)
                                viewModel.setIsVideoSelected(false)
                                showDeleteDialog.value = false
                            })
                        {
                            Text(
                                text = "Delete",
                                color = White90
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            onClick = {
                                showDeleteDialog.value = false
                            })
                        {
                            Text(
                                text = "Cancel",
                                color = White90
                            )
                        }
                    }
                )
            }
            if(showVideoDetailsDialog.value){
                ShowSVideoDetailsDialog(selectedVideo = selectedVideo!!,
                    showVideoDetails = showVideoDetailsDialog)
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatVideoDuration(durationInMillis: Long): String {
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60
    val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
    return if (hours > 0) {
        String.format("%2d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%2d:%02d", minutes, seconds)
    }
}


//
//@Composable
//fun GlideImageFromFilePath(
//    filePath: String,
//    modifier: Modifier = Modifier,
//    contentDescription: String? = null,
//    contentScale: ContentScale = ContentScale.Crop,
//) {
//    Box(modifier = modifier) {
//        Glide.with(LocalContext.current)
//            .asBitmap()
//            .load(File(filePath))
//            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
//            .placeholder(android.R.drawable.ic_menu_report_image)
//            .error(android.R.drawable.ic_delete)
//            .into(object : SimpleTarget<Bitmap>() {
//                override fun onResourceReady(
//                    resource: Bitmap,
//                    transition: Transition<in Bitmap>?
//                ) {
//                    Image(
//                        bitmap = resource,
//                        contentDescription = contentDescription,
//                        contentScale = contentScale,
//                    )
//                }
//            })
//    }
//}

private fun toggleVideoSelection(videoId: Long,selectedVideoIds:MutableList<Long>) {
    if (selectedVideoIds.contains(videoId)) {
        selectedVideoIds.remove(videoId)
    } else {
        selectedVideoIds.add(videoId)
    }
}

@Composable
fun ShowSVideoDetailsDialog(
    selectedVideo: Video,
    showVideoDetails:MutableState<Boolean>
) {
    AlertDialog(
        containerColor = SoundScapeThemes.colorScheme.secondary,
        onDismissRequest = {
            showVideoDetails.value = false
        },
        title = {

        },
        text = {
            Text(
                text = selectedVideo.thumbnail,
                color = White50
            )
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                    showVideoDetails.value = false
                })
            {
                Text(
                    text = "OK",
                    color = White90
                )
            }
        },
        dismissButton = {

        }
    )
}





