package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.Albums

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.Lifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.UIEvents
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.service.MusicService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.startService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.AddSelectedSongsToPlaylist
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.CreatePlaylistAndAddSong
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.MainBottomSheet
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.ShowSongDetailsDialog
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.SongItem
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.BrightGray
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsDetailScreen(
    navController: NavController,
    viewModel: AudioViewModel,
    audioList: List<Audio>,
    player: ExoPlayer

) {

    val currentAlbumSongs by viewModel.currentAlbumSongs.collectAsState()

    val albumSongs: List<Audio> = audioList.filter { audio ->
        audio.id in currentAlbumSongs
    }

    val setMediaItems = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    //    BOTTOM SHEET
    val showSheet = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    var selectedSong by remember {
        mutableStateOf<Audio?>(null)
    }
    val current = remember {
        mutableLongStateOf(0L)
    }
    val currentFavSongs by viewModel.favoritesSongs.collectAsState()

    val selectedPlaylists = remember { mutableStateMapOf<Long, Boolean>() }
    val enableButton = remember {
        mutableStateOf(false)
    }

    val showAddPlaylistDialog = remember {
        mutableStateOf(false)
    }

    val createPlaylist = remember {
        mutableStateOf(false)
    }

    val showSongDetailsDialog = remember { mutableStateOf(false) }


    var playlistName by remember {
        mutableStateOf("")
    }

    val playLists by viewModel.playlists.collectAsState()

    val maxVisiblePlaylists = 4


    //Song selection Part
    val selectedSongs = remember { mutableStateMapOf<Long, Boolean>() }

    val showAddSongsToPlaylistDialog = remember {
        mutableStateOf(false)
    }

    val showSelectAllDropDown = remember { mutableStateOf(false) }

    val playbackState = viewModel.retrievePlaybackState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(80.dp)
                    .padding(end = 0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {},
                navigationIcon = {
                    if (selectedSongs.any { it.value }) {
                        IconButton(onClick = {
                            selectedSongs.clear()
                        })
                        {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = BrightGray
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            if (navController.currentBackStackEntry?.lifecycle?.currentState
                                == Lifecycle.State.RESUMED
                            ) {
                                navController.popBackStack()
                            }
                        })
                        {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = BrightGray
                            )
                        }
                    }
                },
                actions = {
                    if (selectedSongs.any { it.value }) {
                        IconButton(onClick = {
                            showAddSongsToPlaylistDialog.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = White90
                            )
                        }
                    }
                    if (selectedSongs.any { it.value }) {
                        IconButton(onClick = {
                            showSelectAllDropDown.value = !showSelectAllDropDown.value
                        })
                        {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = White90
                            )

                            if (showSelectAllDropDown.value) {
                                DropdownMenu(
                                    modifier = Modifier.background(SoundScapeThemes.colorScheme.primary),
                                    expanded = showSelectAllDropDown.value,
                                    onDismissRequest = {
                                        showSelectAllDropDown.value = false
                                    })
                                {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Select All",
                                                color = White90,
                                                style = SoundScapeThemes.typography.bodyMedium
                                            )
                                        },
                                        onClick = {
                                            showSelectAllDropDown.value = false
                                            albumSongs.forEach { song ->
                                                selectedSongs[song.id] = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        IconButton(onClick = { /*TODO*/ })
                        {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = White90
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(top = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(
                                data = if (currentAlbumSongs.isEmpty()) {
                                    audioList[0].artwork
                                } else {
                                    albumSongs[0].artwork
                                }
                            )
                            .apply(block = fun ImageRequest.Builder.() {
                                error(R.drawable.sample)
                            }
                            ).build()
                    ),
                    contentDescription = "album cover image",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(.5.dp, White90, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(24.dp))

                Column {

                    Text(
                        text = "${albumSongs.size} Songs",
                        color = White90
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White50
                    ),
                    onClick = {
                        if (currentAlbumSongs.isNotEmpty()) {

                            viewModel.setCurrentPlayingSection(3)
                            viewModel.setCurrentPlayingAlbum(albumSongs[0].albumId.toLongOrNull()?:0L)
                            viewModel.setMediaItemFlag(false)
                            viewModel.setMediaItems(albumSongs,context)
                            viewModel.onUiEvents(UIEvents.PlayPause)

                            startService(context)

                            setMediaItems.value = true
                            navController.navigate(ScreenRoute.NowPlayingScreen.route)
                            if (!player.shuffleModeEnabled) {
                                viewModel.toggleShuffle()
                            }
                        } else {
                            Toast.makeText(context, "Playlist is empty!", Toast.LENGTH_SHORT).show()
                        }
                    })
                {
                    Text(
                        text = "Shuffle All",
                        color = White90.copy(.8f),
                        style = SoundScapeThemes.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White50
                    ),
                    onClick = {
                        if (currentAlbumSongs.isNotEmpty()) {
                            viewModel.setCurrentPlayingSection(3)
                            viewModel.setCurrentPlayingAlbum(albumSongs[0].albumId.toLongOrNull()?:0L)

                            viewModel.setMediaItemFlag(false)

                            viewModel.setMediaItems(albumSongs,context)
                            viewModel.onUiEvents(UIEvents.PlayPause)

                            startService(context)

                            setMediaItems.value = true
                            navController.navigate(ScreenRoute.NowPlayingScreen.route)
                            if (player.shuffleModeEnabled) {
                                viewModel.toggleShuffle()
                            }

                        } else {
                            Toast.makeText(context, "Album is empty!", Toast.LENGTH_SHORT).show()
                        }
                    })
                {
                    Text(
                        text = "Play All",
                        color = White90.copy(.8f),
                        style = SoundScapeThemes.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                itemsIndexed(albumSongs) { index, songId ->
                    val isSelected = selectedSongs[songId.id] ?: false
                    SongItem(
                        key = songId.id,
                        isSelected = isSelected,
                        selectedSongs = selectedSongs,
                        song = songId,
                        onLongPress = {
                            if (!selectedSongs.any { selection ->
                                    selection.value
                                }) {
                                selectedSongs[songId.id] =
                                    !(selectedSongs[songId.id] ?: false)
                            }
                        },
                        onTap = {
                            if (selectedSongs.any { selection ->
                                    selection.value
                                }) {
                                selectedSongs[songId.id] =
                                    !(selectedSongs[songId.id] ?: false)
                            } else {
                                viewModel.setMediaItemFlag(false)
                                val selectedAudio = albumSongs.firstOrNull { it.id == songId.id }

                                viewModel.setCurrentPlayingSection(3)
                                viewModel.setCurrentPlayingAlbum(albumSongs[0].albumId.toLongOrNull()?:0L)

                                if (!setMediaItems.value) {
                                    viewModel.setMediaItems(albumSongs,context)
                                    selectedAudio.let {
                                        viewModel.setMediaItems(albumSongs,context)
                                        viewModel.play(albumSongs.indexOf(selectedAudio))
                                    }
//                                    viewModel.playFromAlbum(index)

                                    startService(context)
                                } else if (viewModel.currentSelectedAudio != songId.id) {
//                                    viewModel.playFromAlbum(index)
                                    viewModel.play(albumSongs.indexOf(selectedAudio))
                                }
                                setMediaItems.value = true
                                navController.navigate(ScreenRoute.NowPlayingScreen.route)
                            }
                        },
                        onIconClick = {
                            showSheet.value = true
                            selectedSong = songId
                            current.longValue = songId.id
                        },
                        context = context,
                        isPlaying = (playbackState.lastPlayedSong.toLongOrNull() ?: 0L) == songId.id,
                    )
                }
            }

            if (showSheet.value) {
                MainBottomSheet(
                    showSheet = showSheet,
                    sheetState = sheetState,
                    context = context,
                    onFavClick = {
                        viewModel.toggleFavorite(current.longValue)
                        viewModel.getFavoritesSongs()
                    },
                    onPlayClick = {
                        showSheet.value = false
                        viewModel.setSingleMediaItem(selectedSong!!)
                        startService(context)
                        viewModel.setCurrentPlayingSection(0)
                    },
                    onAddToPlaylistClick = {
                        showSheet.value = false
                        showAddPlaylistDialog.value = true
                    },
                    onDetailsClick = {
                        showSheet.value = false
                        showSongDetailsDialog.value = true
                    },
                    current = current,
                    currentPlayListSongs = currentFavSongs,
                    selectedSong = selectedSong
                )
            }

            if (showAddPlaylistDialog.value || showAddSongsToPlaylistDialog.value) {
                AddSelectedSongsToPlaylist(
                    showAddPlaylistDialog = showAddPlaylistDialog,
                    confirmAddSong = showAddSongsToPlaylistDialog,
                    selectedPlaylists = selectedPlaylists,
                    enableButton = enableButton,
                    createPlaylist = createPlaylist,
                    playlists = playLists,
                    maxVisiblePlaylists = maxVisiblePlaylists,
                    selectedSongs = selectedSongs,
                    viewModel = viewModel,
                    context = context,
                    selectedSong = selectedSong
                )
            }
            if (createPlaylist.value) {
                CreatePlaylistAndAddSong(
                    createPlaylist = createPlaylist,
                    playlistName = playlistName,
                    onValueChange = { newValue ->
                        playlistName = newValue
                    },
                    confirmAddSong = showAddSongsToPlaylistDialog,
                    selectedSongs = selectedSongs,
                    selectedSong = selectedSong,
                    viewModel = viewModel,
                    showAddPlaylistDialog = showAddPlaylistDialog,
                    context = context
                )
            }
            if (showSongDetailsDialog.value) {
                ShowSongDetailsDialog(
                    selectedSong = selectedSong!!,
                    showSongDetails = showSongDetailsDialog
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

//private fun startService(context: Context) {
//    val intent = Intent(context, MusicService::class.java)
//    if (!isMediaSessionServiceRunning(context)) {
//        startForegroundService(context, intent)
//    }
//}