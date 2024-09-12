package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Folders

import Video
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.AllVideos.formatVideoDuration
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Folders.commons.FolderVideoListTopBar
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme2Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme2Secondary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalGlideComposeApi::class
)

@Composable
fun FolderVideosList(
    viewModel: VideoViewModel,
    navController: NavController
) {
    val currentFolderVideos by viewModel.currentFolderVideos.collectAsState()
    val videoList by viewModel.scannedVideoList.collectAsState()

    val context = LocalContext.current

    val folderVideos: List<Video> = videoList.filter { video ->
        video.id in currentFolderVideos
    }

    var showSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    var selectedVideo by remember {
        mutableStateOf<Video?>(null)
    }

    val current = remember {
        mutableLongStateOf(0L)
    }

    val selectedPlaylists = remember { mutableStateMapOf<Long, Boolean>() }

    var enableButton by remember {
        mutableStateOf(false)
    }

    var showAddPlaylistDialog by remember {
        mutableStateOf(false)
    }

    val createPlaylist = remember {
        mutableStateOf(false)
    }

    var playlistName by remember {
        mutableStateOf("")
    }

    val playLists by viewModel.videoPlaylists.collectAsState()

    var showDeleteVideoDialog by remember {
        mutableStateOf(false)
    }

    val maxVisiblePlaylists = 4

    //Song selection Part
    val selectedVideos = remember { mutableStateMapOf<Long, Boolean>() }

    var showDeleteVideosDialog by remember {
        mutableStateOf(false)
    }
    var showAddVideosToPlaylistDialog by remember {
        mutableStateOf(false)
    }

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
    DisposableEffect(backDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                val currentNavDestination = navBackStackEntry?.destination?.route
                if (selectedVideos.isNotEmpty()) {
                    selectedVideos.clear()

                } else {
                    navController.popBackStack()
                }
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }



    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            FolderVideoListTopBar(
                isSelected = selectedVideos.isNotEmpty(),
                title = folderVideos.firstOrNull()!!.bucketName ?: "Folder Name",
                onBack = {
                    navController.navigateUp()
                },
                onClear = {
                    selectedVideos.clear()
                },
                onAdd = {
                    showAddVideosToPlaylistDialog = !showAddVideosToPlaylistDialog
                },
                onMore = {

                },
                onShare = {
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
                    selectedVideos.clear()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(bottom = 16.dp)
        ) {
            if (folderVideos.isEmpty()) {
                // Display a message or handle UI for empty folder
                Text(text = "No videos found in this folder", color = White90)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(items = folderVideos) { index, video ->
                        val isSelected = selectedVideos[video.id] ?: false

                        val id = video.id.toString()
                        val playbackPosition = viewModel.getPlaybackPosition(id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp)
                                .clickable(
                                    onClick = {}
                                )
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
                                            }
                                        },
                                        onTap = {
                                            if (selectedVideos.any {
                                                    it.value
                                                }) {
                                                selectedVideos[video.id] =
                                                    !(selectedVideos[video.id] ?: false)
                                            } else {
                                                viewModel.setVideoMediaItems(folderVideos)
                                                val position =
                                                    viewModel.getPlaybackPosition(video.id.toString())
                                                val videoDuration = video.duration
                                                val percentagePlayed =
                                                    (position.toFloat() / videoDuration.toFloat()) * 100

                                                // Check if more than 95% of the video is played
                                                if (percentagePlayed > 95) {
                                                    viewModel.removeSavedPlayback(video.id.toString())
                                                }
                                                viewModel.playVideo(index)

                                                navController.navigate(ScreenRoute.VideoPlayingScreen.route)
                                            }
                                        }
                                    )
                                }
                                .padding(
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    start = 12.dp, end = 8.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                GlideImage(
                                    model = video.thumbnail, contentDescription = null,
                                    modifier = Modifier
                                        .width(125.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = formatVideoDuration(video.duration.toLong()),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = White90,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Black.copy(.4f))
                                        .padding(end = 4.dp)

                                )
                                if (playbackPosition > 0) {
                                    LinearProgressIndicator(
                                        trackColor = White50,
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
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(225.dp)
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
                                    if (isSelected) {
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
                                                    .size(46.dp)
                                                    .clip(CircleShape),
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

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .clickable {
                                                showSheet = false
                                                showAddPlaylistDialog = true
                                            }
                                            .padding(start = 12.dp, end = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedIconButton(
                                            border = BorderStroke(1.dp, White90),
                                            modifier = Modifier.size(22.dp),
                                            onClick = {
                                                showSheet = false
                                                showAddPlaylistDialog = true
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
                                }
                            }
                        }
                        if (showAddPlaylistDialog || showAddVideosToPlaylistDialog) {
                            AlertDialog(
                                containerColor = SoundScapeThemes.colorScheme.secondary,
                                onDismissRequest = {
                                    showAddPlaylistDialog = false
                                    showAddVideosToPlaylistDialog = false
                                    selectedPlaylists.clear()
                                    enableButton = selectedPlaylists.any { it.value }
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
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Theme2Primary)
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
                                                items(
                                                    items = playLists,
                                                    key = { it.id }) { playlist ->
                                                    val isSelectedPlaylist =
                                                        selectedPlaylists[playlist.id] ?: false
                                                    val firstVideoId =
                                                        playlist.videoIds.lastOrNull()
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
                                                                    selectedPlaylists[playlist.id] =
                                                                        !isSelectedPlaylist
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
                                                                .background(Theme2Primary),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            firstVideoId?.let {
                                                                val imageUrl =
                                                                    viewModel.getVideoImageUrl(it)
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
                                                                color = Color.Gray,
                                                            )

                                                        }

                                                        Spacer(modifier = Modifier.weight(1f))

                                                        OutlinedIconButton(
                                                            colors = IconButtonDefaults.iconButtonColors(
                                                                containerColor = if (isSelected) White90 else Color.Transparent
                                                            ),
                                                            modifier = Modifier.size(22.dp),
                                                            onClick = {
                                                                selectedPlaylists[playlist.id] =
                                                                    !isSelectedPlaylist
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
                                                    showAddVideosToPlaylistDialog = false
                                                    selectedPlaylists.clear()
                                                    enableButton =
                                                        selectedPlaylists.any { it.value }

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
                                                        selectedPlaylists.filter { it.value }
                                                            .map { it.key }
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
                                                    enableButton =
                                                        selectedPlaylists.any { it.value }
                                                    showAddPlaylistDialog = false
                                                    showAddVideosToPlaylistDialog = false

                                                })
                                            {
                                                Text(
                                                    text = "Done",
                                                    color = Theme2Secondary
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
//                                        showAddVideosToPlaylistDialog = false
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
                                                                videoIds = listOf(
                                                                    selectedVideo?.id ?: -1
                                                                ),
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
                                                    showAddVideosToPlaylistDialog = false
                                                    selectedVideos.clear()
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
                    }
                }
            }
        }
    }
}
