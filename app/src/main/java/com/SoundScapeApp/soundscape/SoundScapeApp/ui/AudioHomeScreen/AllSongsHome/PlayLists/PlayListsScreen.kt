package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Playlist
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayListsScreen(
    navController: NavController,
    viewModel: AudioViewModel,
    search: String,
    selectedPlaylists: MutableMap<Long, Boolean>,
    confirmPlaylistDeletion: MutableState<Boolean>,
    onSelectAllPlaylistsClicked: MutableState<Boolean>
) {

    val myPlaylists by viewModel.playlists.collectAsState()


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

    val favSongs by viewModel.favoritesSongs.collectAsState()


    var playListName by remember {
        mutableStateOf("")
    }

    LaunchedEffect(onSelectAllPlaylistsClicked.value) {
        if (onSelectAllPlaylistsClicked.value) {
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
        mutableStateOf<Playlist?>(null)
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
    viewModel.setIsPlaylistSelected(selectedPlaylists.any { it.value })

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .background(GrayishGreenDark)
            .padding(
                top = 8.dp,
                bottom = 0.dp,
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .clickable {
                    isAddingPlayList = true
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
                    .size(58.dp)
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
                text = "Create new",
                style = SoundScapeThemes.typography.titleSmall,
                color = Color.White
            )
        }



        HorizontalDivider(
            modifier = Modifier.padding(start = 95.dp),
            thickness = .5.dp,
            color = White90
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable(
                            onClick = {
                                viewModel.onFavoritesClicked(123, "Favorites")
                                viewModel.loadSongsForFavorites()
                                if (navController.currentBackStackEntry?.lifecycle?.currentState
                                    == Lifecycle.State.RESUMED
                                ) {
                                    navController.navigate(ScreenRoute.PlaylistDetailScreen.route)
                                }
                            }
                        )
                        .padding(
                            top = 2.dp,
                            bottom = 2.dp,
                            start = 16.dp,
                            end = 12.dp
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

                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White
                        )

                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = 6.dp, bottom = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Favorites",
                            style = SoundScapeThemes.typography.bodyLarge,
                            color = Color.White
                        )

                        Text(
                            text = "${favSongs.size} songs",
                            style = SoundScapeThemes.typography.bodyMedium,
                            color = White50,
                        )
                    }
                }
            }

            items(items = filteredPlaylists, key = { it.id }) { playlist ->
                val firstSongId = playlist.songIds.lastOrNull()
                val isSelected = selectedPlaylists[playlist.id] ?: false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            if (isSelected) SoundScapeThemes.colorScheme.primary.copy(.9f)
                            else Color.Transparent
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    if (!selectedPlaylists.any {
                                            it.value
                                        }) {
                                        selectedPlaylists[playlist.id] =
                                            !(selectedPlaylists[playlist.id] ?: false)
                                    }
                                },
                                onTap = {
                                    if (selectedPlaylists.any {
                                            it.value
                                        }) {
                                        selectedPlaylists[playlist.id] =
                                            !(selectedPlaylists[playlist.id] ?: false)
                                    } else {
                                        viewModel.onPlaylistClicked(playlist.id, playlist.name)
                                        scope.launch {
                                            viewModel.loadSongsForCurrentPlaylist(playlistId = playlist.id)
                                        }
                                        if (navController.currentBackStackEntry?.lifecycle?.currentState
                                            == Lifecycle.State.RESUMED
                                        ) {
                                            navController.navigate(ScreenRoute.PlaylistDetailScreen.route)
                                        }
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
                            animationSpec = spring(
                                Spring.DampingRatioLowBouncy,
                                Spring.StiffnessLow
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SoundScapeThemes.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        firstSongId?.let {
                            val imageUrl = viewModel.getSongImageUrl(it)

                            val painter = rememberAsyncImagePainter(

                                ImageRequest.Builder(context)
                                    .data(
                                        data = imageUrl!!
                                    )
                                    .apply(block = fun ImageRequest.Builder.() {
                                        error(R.drawable.sample)
                                    }
                                    ).build()
                            )
                            Image(
                                painter = painter,
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
                            style = SoundScapeThemes.typography.bodyLarge,
                            color = White90
                        )

                        Text(
                            text = "${playlist.songIds.size} songs",
                            style = SoundScapeThemes.typography.bodyMedium,
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

            item {
                Spacer(modifier = Modifier.height(74.dp))
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
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                focusedTextColor = White90,
                                unfocusedTextColor = White90
                            ),
                            value = playListName,
                            onValueChange = {
                                playListName = it
                            },
                            placeholder = {
                                Text(text = "Playlist name", color = Color.White.copy(.7f))
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        enabled = playListName.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = White50.copy(.3f),
                            containerColor = White90.copy(.8f)
                        ),
                        onClick = {
                        if (playListName != "") {
                            viewModel.saveNewPlaylist(
                                newPlaylistName = playListName,
                            )
                            isAddingPlayList = false
                            playListName = ""
                        }

                    })
                    {
                        Text(
                            color = SoundScapeThemes.colorScheme.secondary,
                            text = "Save")
                    }
                },
                dismissButton = {
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = { isAddingPlayList = false })
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

                            viewModel.deletePlaylists(selectedPlaylistList)
                            selectedPlaylists.clear()
                            confirmPlaylistDeletion.value = false
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
            val firstSongId = selectedPlaylist!!.songIds.lastOrNull()
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
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(White50.copy(.2f))
                                .border(1.dp, White50, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            firstSongId?.let {
                                val imageUrl = viewModel.getSongImageUrl(it)

                                val painter = rememberAsyncImagePainter(

                                    ImageRequest.Builder(context)
                                        .data(
                                            data = imageUrl!!
                                        )
                                        .apply(block = fun ImageRequest.Builder.() {
                                            error(R.drawable.sample)
                                        }
                                        ).build()
                                )
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
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
                                text = "${selectedPlaylist!!.songIds.size} songs",
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
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(46.dp)
//                            .clickable {
////                                showAddPlaylistDialog = true
//                                showSheet = false
//                            }
//                            .padding(start = 24.dp, end = 12.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        OutlinedIconButton(
//                            border = BorderStroke(1.dp, White90),
//                            modifier = Modifier.size(22.dp),
//                            onClick = {
//                                scope.launch {
//                                    viewModel.loadSongsForCurrentPlaylist(selectedPlaylist!!.id)
//                                }
//                                viewModel.setMediaItems(audioList = playlistSongs.value, context =context )
//
//                            })
//                        {
//                            Icon(
//                                imageVector = Icons.Default.PlayArrow,
//                                contentDescription = null,
//                                tint = White90,
//                                modifier = Modifier.size(16.dp)
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.width(10.dp))
//                        Text(
//                            text = "Play",
//                            color = White90,
//                            fontSize = 14.sp
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))

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
                                focusedTextColor = White90,
                                unfocusedTextColor = White90,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            value = newPlaylistName.value,
                            onValueChange = {
                                newPlaylistName.value = it
                            },
                            placeholder = {
                                Text(text = "Playlist name", color = Color.White.copy(.7f))
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
                                    containerColor = White90,
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp),
                                onClick = {
                                    if (newPlaylistName.value != "") {
                                        viewModel.editPlaylistName(
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
                            viewModel.deletePlaylist(selectedPlaylist!!.id)
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
                            viewModel.clearPlaylist(selectedPlaylist!!.id)
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
