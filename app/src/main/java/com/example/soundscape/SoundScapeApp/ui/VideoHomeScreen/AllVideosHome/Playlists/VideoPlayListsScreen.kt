package com.example.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Playlists


import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.example.soundscape.SoundScapeApp.data.videoPlaylist
import com.example.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.example.soundscape.ui.theme.SoundScapeThemes
import com.example.soundscape.ui.theme.Theme2Primary
import com.example.soundscape.ui.theme.Theme2Secondary
import com.example.soundscape.ui.theme.White50
import com.example.soundscape.ui.theme.White90

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun VideoPlayListsScreen(
    navController: NavController,
    viewModel: VideoViewModel,
    search: String,
    selectedPlaylists: MutableMap<Long, Boolean>,
    confirmPlaylistDeletion: MutableState<Boolean>,
    onSelectAllPlaylistClicked: MutableState<Boolean>
) {

    val myPlaylists by viewModel.videoPlaylists.collectAsState()
    val videoList by viewModel.scannedVideoList.collectAsState()

    var isAddingPlayList by remember {
        mutableStateOf(false)
    }

    val filteredPlaylists = if (search.isNotBlank()) {
        myPlaylists.filter { playlist ->
            playlist.name.contains(search, ignoreCase = true)
        }
    } else {
        myPlaylists
    }


    var playListName by remember {
        mutableStateOf("")
    }

    LaunchedEffect(onSelectAllPlaylistClicked.value) {
        if (onSelectAllPlaylistClicked.value) {
            filteredPlaylists.forEach { playlist ->
                selectedPlaylists[playlist.id] = true
            }
        }
    }


//    SHEET SECTION
    var showSheet by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()

    var selectedPlaylist by remember {
        mutableStateOf<videoPlaylist?>(null)
    }

    var showEditPlaylist by remember {
        mutableStateOf(false)
    }

    val newPlaylistName = remember {
        mutableStateOf("")
    }

    var showDeletePlaylistDialog by remember {
        mutableStateOf(false)
    }
    var showClearPlaylistDialog by remember {
        mutableStateOf(false)
    }

    // Playlist selection
//    viewModel.setIsPlaylistSelected(selectedPlaylists.any{it.value})

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 8.dp,
                bottom = 0.dp,
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .clickable {
                    isAddingPlayList = true
                    selectedPlaylists.clear()
                    viewModel.setIsPlaylistSelected(false)
                    onSelectAllPlaylistClicked.value = false
                }
                .padding(
                    top = 2.dp,
                    bottom = 2.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoundScapeThemes.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Thin,
                    color = White90
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Create new", fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(start = 95.dp),
            color = White50,
            thickness = .5.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = filteredPlaylists, key = { it.id }) { playlist ->
                val isSelected = selectedPlaylists[playlist.id] ?: false
                val firstVideoId = playlist.videoIds.firstOrNull()
                val videoIdsInList = playlist.videoIds.filter { videoId ->
                    videoList.any { it.id == videoId }
                }
                val count = if (videoIdsInList.isNotEmpty()) {
                    videoIdsInList.size
                } else {
                    0
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(if (isSelected) SoundScapeThemes.colorScheme.primary.copy(.9f) else Color.Transparent)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    if (!selectedPlaylists.any {
                                            it.value
                                        }) {
                                        selectedPlaylists[playlist.id] =
                                            !(selectedPlaylists[playlist.id] ?: false)
                                    }
                                    viewModel.setIsPlaylistSelected(selectedPlaylists.any { it.value })

                                },
                                onTap = {
                                    if (selectedPlaylists.any {
                                            it.value
                                        }) {
                                        selectedPlaylists[playlist.id] =
                                            !(selectedPlaylists[playlist.id] ?: false)

                                        viewModel.setIsPlaylistSelected(selectedPlaylists.any { it.value })

                                    } else {
                                        viewModel.onVideoPlaylistClicked(playlist.id, playlist.name)
                                        viewModel.loadVideosForCurrentPlaylist(playlistId = playlist.id)
                                        navController.navigate(ScreenRoute.VidePlaylistDetailScreen.route)
                                    }
                                }
                            )
                        }
                        .padding(
                            top = 2.dp,
                            bottom = 2.dp,
                            start = 16.dp,
                            end = 12.dp
                        )
                        .animateItemPlacement(
                            animationSpec = tween(350)
                        ),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SoundScapeThemes.colorScheme.secondary)
                            .border(.1.dp, White50, RoundedCornerShape(4.dp)),
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
                            text = "$count videos",
                            fontSize = 14.sp,
                            color = White50,
                        )

                    }
                    Spacer(modifier = Modifier.weight(1f))


                    if (selectedPlaylists.any { it.value }) {
                        OutlinedIconButton(
                            modifier = Modifier.size(20.dp),
                            border = BorderStroke(1.dp, White90),
                            onClick = {
                                selectedPlaylists[playlist.id] =
                                    !(selectedPlaylists[playlist.id] ?: false)
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
                            modifier = Modifier.size(20.dp),
                            onClick = {
                                showSheet = true
                                selectedPlaylist = playlist
                                newPlaylistName.value = playlist.name
                            })
                        {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = White90,
                            )
                        }
                    }
                }
            }
        }
        if (isAddingPlayList) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = { isAddingPlayList = false },
                title = {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = playListName,
                            onValueChange = {
                                playListName = it
                            },
                            placeholder = {
                                Text(text = "video Playlist", color = Color.White)
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (playListName != "") {
                            viewModel.saveNewVideoPlaylist(
                                newPlaylistName = playListName,
                            )
                            isAddingPlayList = false
                            playListName = ""
                        }

                    })
                    {
                        Text(text = "Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { isAddingPlayList = false })
                    {
                        Text(text = "Cancel")
                    }
                }
            )
        }
        if (confirmPlaylistDeletion.value) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    confirmPlaylistDeletion.value = false
                },
                title = {
                    Text(
                        text = "Delete Playlist",
                        color = White90
                    )
                },
                text = {
                    Text(
                        text = "Delete ${
                            selectedPlaylists.filter { it.value }.map { it.key }.size
                        } playlists'?",
                        color = White50
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            val selectedPlaylistList = selectedPlaylists
                                .filter { it.value }
                                .map { it.key }

                            viewModel.deleteVideoPlaylists(selectedPlaylistList)
                            selectedPlaylists.clear()
                            confirmPlaylistDeletion.value = false
                            viewModel.setIsPlaylistSelected(false)
                            onSelectAllPlaylistClicked.value = false
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
                            confirmPlaylistDeletion.value = false
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

        if (showSheet) {
//            val firstSongId = selectedPlaylist!!.songIds.lastOrNull()

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
                        .padding(top = 12.dp, bottom = 42.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .padding(start = 12.dp, end = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(White50.copy(.2f))
                                .border(1.dp, White50, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
//                            firstSongId?.let {
//                                val imageUrl = viewModel.getSongImageUrl(it)
//                                val painter = rememberAsyncImagePainter(
//                                    imageUrl
//                                )
//                                Image(
//                                    painter = painter,
//                                    contentDescription = null,
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = selectedPlaylist!!.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = White90,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(225.dp)
                            )
                            Text(
                                text = "${selectedPlaylist!!.videoIds.size} videos",
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

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable {
                                showSheet = false
                                showDeletePlaylistDialog = true
                            }
                            .padding(start = 24.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            border = BorderStroke(1.dp, White90),
                            modifier = Modifier.size(22.dp),
                            onClick = {
                                showSheet = false
                                showDeletePlaylistDialog = true
                            })
                        {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = White90,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Delete Playlist",
                            color = White90,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable {
                                showClearPlaylistDialog = true
                                showSheet = false
                            }
                            .padding(start = 24.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            border = BorderStroke(1.dp, White90),
                            modifier = Modifier.size(22.dp),
                            onClick = {
                                showClearPlaylistDialog = true
                                showSheet = false
                            })
                        {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = White90,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Clear Playlist",
                            color = White90,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable {
                                showEditPlaylist = true
                                showSheet = false
                            }
                            .padding(start = 24.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            border = BorderStroke(1.dp, White90),
                            modifier = Modifier.size(22.dp),
                            onClick = {
//                                showEditPlaylist = true
                                showSheet = false
                            })
                        {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = White90,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Edit Playlist",
                            color = White90,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        if (showEditPlaylist) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = { showEditPlaylist = false },
                title = {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        TextField(
                            textStyle = TextStyle(
                                color = White90,
                                fontSize = 14.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            value = newPlaylistName.value,
                            onValueChange = {
                                newPlaylistName.value = it
                            },
                            placeholder = {
                                Text(text = "Playlist", color = Color.White)
                            }
                        )

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
                                    showEditPlaylist = false
                                })
                            {
                                Text(
                                    text = "Cancel",
                                    color = White90
                                )
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = White50
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp),
                                onClick = {
                                    if (newPlaylistName.value != "") {
                                        viewModel.editVideoPlaylistName(
                                            selectedPlaylist!!.id,
                                            newName = newPlaylistName.value,
                                        )
                                        newPlaylistName.value = ""
                                        showEditPlaylist = false
                                    }
                                })
                            {
                                Text(
                                    text = "Done",
                                    color = SoundScapeThemes.colorScheme.secondary
                                )
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
        if (showDeletePlaylistDialog) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    showDeletePlaylistDialog = false
                },
                title = {
                    Text(
                        text = "Delete Playlist",
                        color = White90
                    )
                },
                text = {
                    Text(
                        text = "Delete playlist '${selectedPlaylist!!.name}'?",
                        color = White50
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            viewModel.deleteVideoPlaylist(selectedPlaylist!!.id)
                            showDeletePlaylistDialog = false
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
                            showDeletePlaylistDialog = false
                        })
                    {
                        Text(
                            text = "Cancle",
                            color = White90
                        )
                    }
                }
            )
        }
        if (showClearPlaylistDialog) {
            AlertDialog(
                containerColor = SoundScapeThemes.colorScheme.secondary,
                onDismissRequest = {
                    showClearPlaylistDialog = false
                },
                title = {
                    Text(
                        text = "Clear Playlist",
                        color = White90
                    )
                },
                text = {
                    Text(
                        text = "Clear playlist '${selectedPlaylist!!.name}'?",
                        color = White50
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            viewModel.clearVideoPlaylist(selectedPlaylist!!.id)
                            showClearPlaylistDialog = false
                        })
                    {
                        Text(
                            text = "Clear",
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
                            showClearPlaylistDialog = false
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
    }
}

