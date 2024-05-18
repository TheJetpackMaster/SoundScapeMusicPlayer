package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Folders

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.ui.theme.Theme2Primary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllFolders(
    navController: NavController,
    viewModel: VideoViewModel,
    search:String
) {

    val videosList by viewModel.scannedVideoList.collectAsState()

    val groupedFolders = videosList.groupBy { it.bucketName }

    val filteredFolders  = if (search.isNotBlank()) {
//        viewModel.isAppSearch(true)
        groupedFolders.filter { folder ->
            folder.key.contains(search, ignoreCase = true)
        }
    } else {
//        viewModel.isAppSearch(false)
        groupedFolders
    }

    val folders = filteredFolders.keys.toList()

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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(items = folders, key = { it }) { folderId ->
//                    val isSelected = selectedSongs[song.id] ?: false
                    val isSelected = false
                    val folder = groupedFolders[folderId]?.firstOrNull()
                    val videosInFolder = groupedFolders[folderId]?.size ?: 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(82.dp)
                            .clickable(
                                onClick = {
                                    viewModel.loadVideosForFolder(folder!!.bucketName)
                                    navController.navigate("details")
                                }
                            )
                            .background(if (isSelected) Theme2Primary.copy(.9f) else Color.Transparent)
//                            .pointerInput(Unit) {
//                                detectTapGestures(
//                                    onLongPress = {
//
//                                    },
//                                    onTap = {
//
//
//                                    }
//                                )
//                            }
                            .padding(
                                top = 8.dp,
                                bottom = 8.dp,
                                start = 12.dp, end = 2.dp
                            )
                            .animateItemPlacement(
                                animationSpec = tween(durationMillis = 400)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = (painterResource(id = R.drawable.baseline_folder_24)),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                colorFilter = ColorFilter.tint(White90)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = folder!!.bucketName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = White90,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "$videosInFolder videos",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = White50,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White50,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                        }

//                        if (selectedSongs.any { it.value }) {
//                            OutlinedIconButton(
//                                modifier = Modifier.size(20.dp),
//                                border = BorderStroke(1.dp, White90),
//                                onClick = {
//                                    selectedSongs[song.id] =
//                                        !(selectedSongs[song.id] ?: false)
//                                })
//                            {
//                                if (isSelected
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Check,
//                                        contentDescription = null,
//                                        tint = White90,
//                                        modifier = Modifier.size(14.dp)
//                                    )
//                                }
//                            }
//                        } else {
//                            IconButton(
//                                modifier = Modifier.size(20.dp),
//                                onClick = {
//                                    showSheet = true
//                                    selectedSong = song
//                                    current.longValue = song.id
//                                }) {
//                                Icon(
//                                    imageVector = Icons.Default.MoreVert,
//                                    contentDescription = null,
//                                    tint = White90
//                                )
//                            }
//                        }
//                        IconButton(
//                            onClick = {
//
//                            }) {
//                            Icon(
//                                imageVector = Icons.Default.MoreVert,
//                                contentDescription = null,
//                                tint = White90
//                            )
//                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
//            if (showSheet) {
//                ModalBottomSheet(
//                    containerColor = GrayishGreen,
//                    dragHandle = {},
//                    onDismissRequest = {
//                        showSheet = false
//                    },
//                    sheetState = sheetState,
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .wrapContentHeight()
//                            .padding(top = 12.dp, bottom = 42.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(46.dp)
//                                .padding(start = 12.dp, end = 2.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Box(contentAlignment = Alignment.Center) {
//                                Image(
//                                    painter = rememberAsyncImagePainter(
//                                        ImageRequest.Builder(context)
//                                            .data(selectedSong!!.artwork)
//                                            .placeholder(R.drawable.sample)
//                                            .error(R.drawable.sample)
//                                            .memoryCachePolicy(CachePolicy.ENABLED)
//                                            .diskCachePolicy(CachePolicy.ENABLED)
//                                            .build()
//                                    ),
//                                    contentDescription = null,
//                                    modifier = Modifier
//                                        .size(46.dp)
//                                        .clip(CircleShape),
//                                    contentScale = ContentScale.Crop
//                                )
//
//                                Spacer(
//                                    modifier = Modifier
//                                        .size(16.dp)
//                                        .clip(CircleShape)
//                                        .background(GrayishGreenDark)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(12.dp))
//
//                            Column(
//                                modifier = Modifier.weight(1f),
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                Text(
//                                    text = selectedSong!!.displayName,
//                                    fontSize = 13.sp,
//                                    fontWeight = FontWeight.Medium,
//                                    color = White90,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis,
//                                    modifier = Modifier.width(225.dp)
//                                )
//                                Text(
//                                    text = selectedSong!!.artist,
//                                    fontSize = 12.sp,
//                                    fontWeight = FontWeight.Medium,
//                                    color = White50,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis,
//                                    modifier = Modifier.width(200.dp)
//                                )
//                            }
//
//                            IconButton(onClick = {
//                                viewModel.toggleFavorite(current.longValue)
//                                viewModel.getFavoritesSongs()
//                            }) {
//                                Icon(
//                                    imageVector = if (current.longValue in currentPlayListSongs) {
//                                        Icons.Default.Favorite // Red heart icon
//                                    } else {
//                                        Icons.Default.FavoriteBorder // White heart icon
//                                    },
//                                    contentDescription = null,
//                                    tint = if (current.longValue in currentPlayListSongs) Color.Red else White90,
//                                )
//                            }
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        HorizontalDivider(
//                            color = GrayishGreenDark,
//                            thickness = 1.dp
//                        )
//
//                        Spacer(modifier = Modifier.height(12.dp))
//
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(48.dp)
//                                .clickable {
//                                    showSheet = false
//                                    onItemClick(0, selectedSong!!.id)
//                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
//                                }
//                                .padding(start = 12.dp, end = 12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            OutlinedIconButton(
//                                border = BorderStroke(1.dp, White90),
//                                modifier = Modifier.size(22.dp),
//                                onClick = {
//                                    showSheet = false
//                                    onItemClick(0, selectedSong!!.id)
//                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
//                                })
//                            {
//                                Icon(
//                                    imageVector = Icons.Default.PlayArrow,
//                                    contentDescription = null,
//                                    tint = White90,
//                                    modifier = Modifier.size(16.dp)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(10.dp))
//                            Text(
//                                text = "Play",
//                                color = White90,
//                                fontSize = 14.sp
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(48.dp)
//                                .clickable {
//                                    showAddPlaylistDialog = true
//                                    showSheet = false
//                                }
//                                .padding(start = 12.dp, end = 12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            OutlinedIconButton(
//                                border = BorderStroke(1.dp, White90),
//                                modifier = Modifier.size(22.dp),
//                                onClick = {
//
//                                })
//                            {
//                                Icon(
//                                    imageVector = Icons.Default.Add,
//                                    contentDescription = null,
//                                    tint = White90,
//                                    modifier = Modifier.size(16.dp)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(10.dp))
//                            Text(
//                                text = "Add to Playlist",
//                                color = White90,
//                                fontSize = 14.sp
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(48.dp)
//                                .clickable {
//
//                                    showSheet = false
//                                }
//                                .padding(start = 12.dp, end = 12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            OutlinedIconButton(
//                                border = BorderStroke(1.dp, White90),
//                                modifier = Modifier.size(22.dp),
//                                onClick = {
//
//                                })
//                            {
//                                Icon(
//                                    imageVector = Icons.Default.Info,
//                                    contentDescription = null,
//                                    tint = White90,
//                                    modifier = Modifier.size(16.dp)
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(10.dp))
//                            Text(
//                                text = "Details",
//                                color = White90,
//                                fontSize = 14.sp
//                            )
//                        }
//                    }
//                }
//            }

//            if (showAddPlaylistDialog || confirmAddSong.value) {
//                AlertDialog(
//                    containerColor = GrayishGreen,
//                    onDismissRequest = {
//                        showAddPlaylistDialog = false
//                        confirmAddSong.value = false
//                        selectedPlaylists.clear()
//                        enableButton = selectedPlaylists.any { it.value }
//                    },
//                    confirmButton = { /*TODO*/ },
//                    title = {
//                        Text(
//                            text = "Add to",
//                            color = White90
//                        )
//                    },
//                    text = {
//                        Column {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        createPlaylist.value = true
//                                    },
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .clip(RoundedCornerShape(4.dp))
//                                        .background(GrayishGreen)
//                                        .border(
//                                            1.dp,
//                                            White90,
//                                            shape = RoundedCornerShape(4.dp)
//                                        ),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Add,
//                                        contentDescription = null,
//                                        tint = White90
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.width(10.dp))
//
//                                Text(
//                                    text = "New Playlist",
//                                    color = White90,
//                                    fontSize = 14.sp
//                                )
//                            }
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            val boxHeight = if (playLists.size <= maxVisiblePlaylists) {
//                                (60.dp * playLists.size).coerceAtMost(250.dp)
//                            } else {
//                                250.dp
//                            }
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(boxHeight)
//                            )
//                            {
//                                LazyColumn(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .wrapContentHeight()
//                                ) {
//                                    items(items = playLists, key = { it.id }) { playlist ->
//                                        val isSelected = selectedPlaylists[playlist.id] ?: false
//
//                                        val firstSongId = playlist.songIds.lastOrNull()
//                                        Row(
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .height(60.dp)
//                                                .padding(
//                                                    top = 2.dp,
//                                                    bottom = 2.dp,
//                                                    start = 4.dp,
//                                                    end = 4.dp
//                                                )
//                                                .clickable(
//                                                    onClick = {
//                                                        selectedPlaylists[playlist.id] = !isSelected
//                                                        enableButton =
//                                                            selectedPlaylists.any { it.value }
//                                                    }
//                                                ),
//                                            verticalAlignment = Alignment.CenterVertically
//
//                                        ) {
//                                            Box(
//                                                modifier = Modifier
//                                                    .size(40.dp)
//                                                    .clip(RoundedCornerShape(4.dp))
//                                                    .background(GrayishGreen),
//                                                contentAlignment = Alignment.Center
//                                            ) {
//                                                firstSongId?.let {
//                                                    val imageUrl = viewModel.getSongImageUrl(it)
//                                                    val painter = rememberAsyncImagePainter(
//                                                        imageUrl
//                                                    )
//                                                    Image(
//                                                        painter = painter,
//                                                        contentDescription = null,
//                                                        contentScale = ContentScale.Crop
//                                                    )
//                                                }
//                                            }
//                                            Spacer(modifier = Modifier.width(12.dp))
//
//                                            Column(
//                                                modifier = Modifier
//                                                    .fillMaxHeight()
//                                                    .padding(top = 6.dp, bottom = 6.dp),
//                                                verticalArrangement = Arrangement.Center
//                                            ) {
//                                                Text(
//                                                    text = playlist.name,
//                                                    fontSize = 14.sp,
//                                                    color = Color.White
//                                                )
//
//                                                Text(
//                                                    text = "${playlist.songIds.size} songs",
//                                                    fontSize = 14.sp,
//                                                    color = Color.Gray,
//                                                )
//
//                                            }
//
//                                            Spacer(modifier = Modifier.weight(1f))
//
//                                            OutlinedIconButton(
//                                                colors = IconButtonDefaults.iconButtonColors(
//                                                    containerColor = if (isSelected) White90 else Color.Transparent
//                                                ),
//                                                modifier = Modifier.size(22.dp),
//                                                onClick = {
//                                                    selectedPlaylists[playlist.id] = !isSelected
//                                                    enableButton =
//                                                        selectedPlaylists.any { it.value }
//                                                })
//                                            {
//                                                Icon(
//                                                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
//                                                    contentDescription = null,
//                                                    tint = if (isSelected) Color.Black else White90,
//                                                    modifier = Modifier.size(16.dp)
//                                                )
//                                            }
//                                        }
//                                        Spacer(modifier = Modifier.height(2.dp))
//                                    }
//                                }
//                            }
//
//                            Spacer(modifier = Modifier.height(12.dp))
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Button(
//                                    colors = ButtonDefaults.buttonColors(
//                                        containerColor = Color.Transparent
//                                    ),
//                                    modifier = Modifier.weight(1f),
//                                    shape = RoundedCornerShape(4.dp),
//                                    onClick = {
//                                        showAddPlaylistDialog = false
//                                        confirmAddSong.value = false
//                                        selectedPlaylists.clear()
//                                        enableButton = selectedPlaylists.any { it.value }
//
//                                    })
//                                {
//                                    Text(
//                                        text = "Cancel",
//                                        color = White90
//                                    )
//                                }
//
//                                Button(
//                                    colors = ButtonDefaults.buttonColors(
//                                        disabledContainerColor = White50.copy(.3f),
//                                        containerColor = White90.copy(.8f)
//                                    ),
//                                    enabled = enableButton,
//                                    modifier = Modifier.weight(1f),
//                                    shape = RoundedCornerShape(4.dp),
//                                    onClick = {
//                                        val selectedPlayListsList =
//                                            selectedPlaylists.filter { it.value }.map { it.key }
//                                        val selectedSongsList = selectedSongs
//                                            .filter { it.value }
//                                            .map { it.key }
//                                        selectedPlayListsList.forEach { playlistId ->
//                                            if (showAddPlaylistDialog) {
//                                                viewModel.addSongToDifferentPlaylist(
//                                                    playlistId,
//                                                    listOf(selectedSong?.id ?: -1),
//                                                    context
//                                                )
//                                            } else {
//                                                viewModel.addSongToDifferentPlaylist(
//                                                    playlistId,
//                                                    selectedSongsList,
//                                                    context
//                                                )
//                                            }
//                                        }
//                                        selectedPlaylists.clear()
//                                        selectedSongs.clear()
//                                        enableButton = selectedPlaylists.any { it.value }
//                                        showAddPlaylistDialog = false
//                                        confirmAddSong.value = false
//                                    })
//                                {
//                                    Text(
//                                        text = "Done",
//                                        color = GrayishGreenDark
//                                    )
//                                }
//                            }
//                        }
//                    }
//                )
//            }

//            if (createPlaylist.value) {
//                AlertDialog(
//                    containerColor = GrayishGreen,
//                    onDismissRequest = { createPlaylist.value = false },
//                    title = {
//                        Column(
//                            modifier = Modifier.padding(10.dp)
//                        ) {
//                            TextField(
//                                colors = TextFieldDefaults.colors(
//                                    focusedContainerColor = Color.Transparent,
//                                    unfocusedContainerColor = Color.Transparent
//                                ),
//                                value = playlistName,
//                                onValueChange = {
//                                    playlistName = it
//                                },
//                                placeholder = {
//                                    Text(text = "Playlist", color = Color.White)
//                                }
//                            )
//
//                            Spacer(modifier = Modifier.height(12.dp))
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Button(
//                                    modifier = Modifier.width(100.dp),
//                                    shape = RoundedCornerShape(4.dp),
//                                    onClick = {
//                                        createPlaylist.value = false
//                                        confirmAddSong.value = false
//                                    })
//                                {
//                                    Text(text = "Cancel")
//                                }
//
//                                Button(
//                                    modifier = Modifier.width(100.dp),
//                                    shape = RoundedCornerShape(4.dp),
//                                    onClick = {
//                                        val selectedSongsList = selectedSongs
//                                            .filter { it.value }
//                                            .map { it.key }
//
//                                        if (playlistName != "") {
//                                            viewModel.saveNewPlaylist(
//                                                newPlaylistName = playlistName,
//                                            )
//
//                                            if (showAddPlaylistDialog) {
//                                                viewModel.createAndAddSongToPlaylist(
//                                                    songIds = listOf(selectedSong?.id ?: -1),
//                                                    context = context
//                                                )
//                                            } else {
//                                                viewModel.createAndAddSongToPlaylist(
//                                                    songIds = selectedSongsList,
//                                                    context = context
//                                                )
//                                            }
//                                        }
//                                        createPlaylist.value = false
//                                        showAddPlaylistDialog = false
//                                        confirmAddSong.value = false
//                                        selectedSongs.clear()
//                                    })
//                                {
//                                    Text(text = "Done")
//                                }
//                            }
//                        }
//                    },
//                    confirmButton = {
//
//                    },
//                    dismissButton = {
//                    }
//                )
//            }
//
//            if (showSort.value) {
//                ModalBottomSheet(
//                    containerColor = GrayishGreen,
//                    dragHandle = {},
//                    onDismissRequest = {
//                        showSort.value = false
//                    },
//                    sheetState = sheetState,
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .wrapContentHeight()
//                            .padding(top = 12.dp, bottom = 42.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(46.dp)
//                                .padding(start = 16.dp, end = 12.dp)
//                                .clickable(
//                                    onClick = {
//                                        showSort.value = false
//                                        viewModel.sortAudioList(SortType.DATE_ADDED_DESC)
//                                        viewModel.setSortType(SortType.DATE_ADDED_DESC)
//                                    }
//                                ),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "Sort by date added (Descending)",
//                                color =
//                                if (currentSortType == SortType.DATE_ADDED_DESC) White50 else White90
//                            )
//
//                            Spacer(modifier = Modifier.width(8.dp))
//                            if (currentSortType == SortType.DATE_ADDED_DESC) {
//                                Icon(
//                                    imageVector = Icons.Default.Done,
//                                    contentDescription = null,
//                                    tint = White50
//                                )
//                            }
//
//                        }
//                        Row(
//                            modifier = Modifier
//                                .height(46.dp)
//                                .padding(start = 16.dp, end = 12.dp)
//                                .clickable(
//                                    onClick = {
//                                        showSort.value = false
//                                        viewModel.sortAudioList(SortType.DATE_ADDED_ASC)
//                                        viewModel.setSortType(SortType.DATE_ADDED_ASC)
//                                    }
//                                ),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "Sort by date added (Ascending)",
//                                color =
//                                if (currentSortType == SortType.DATE_ADDED_ASC) White50 else White90
//                            )
//
//                            Spacer(modifier = Modifier.width(8.dp))
//                            if (currentSortType == SortType.DATE_ADDED_ASC) {
//                                Icon(
//                                    imageVector = Icons.Default.Done,
//                                    contentDescription = null,
//                                    tint = White50
//                                )
//                            }
//
//                        }
//                        Row(
//                            modifier = Modifier
//                                .height(46.dp)
//                                .padding(start = 16.dp, end = 12.dp)
//                                .clickable(
//                                    onClick = {
//                                        showSort.value = false
//                                        viewModel.sortAudioList(SortType.TITLE_DESC)
//                                        viewModel.setSortType(SortType.TITLE_DESC)
//                                    }
//                                ),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "Sort by title (Descending)",
//                                color =
//                                if (currentSortType == SortType.TITLE_DESC) White50 else White90
//                            )
//
//                            Spacer(modifier = Modifier.width(8.dp))
//                            if (currentSortType == SortType.TITLE_DESC) {
//                                Icon(
//                                    imageVector = Icons.Default.Done,
//                                    contentDescription = null,
//                                    tint = White50
//                                )
//                            }
//
//                        }
//                        Row(
//                            modifier = Modifier
//                                .height(46.dp)
//                                .padding(start = 16.dp, end = 12.dp)
//                                .clickable(
//                                    onClick = {
//                                        showSort.value = false
//                                        viewModel.sortAudioList(SortType.TITLE_ASC)
//                                        viewModel.setSortType(SortType.TITLE_ASC)
//                                    }
//                                ),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "Sort by title (Ascending)",
//                                color =
//                                if (currentSortType == SortType.TITLE_ASC) White50 else White90
//                            )
//
//                            Spacer(modifier = Modifier.width(8.dp))
//                            if (currentSortType == SortType.TITLE_ASC) {
//                                Icon(
//                                    imageVector = Icons.Default.Done,
//                                    contentDescription = null,
//                                    tint = White50
//                                )
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}