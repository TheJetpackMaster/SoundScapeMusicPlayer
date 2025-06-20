@file:Suppress("DEPRECATION")

package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.SortType
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.UIEvents
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.service.MusicService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.AddSelectedSongsToPlaylist
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.ConfirmSetRingtone
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.CreatePlaylistAndAddSong
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.MainBottomSheet
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.ShowSongDetailsDialog
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.SongItem
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.BrightGray
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme2Primary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlayListDetailsScreen(
    navController: NavController,
    viewModel: AudioViewModel,
    audioList: List<Audio>,
    player: ExoPlayer

) {
    val currentPlayListSongs by viewModel.currentPlaylistSongs.collectAsState()
    val currentPlaylistName by viewModel.currentPlaylistName.collectAsState()
    val currentPlaylistId by viewModel.currentPlaylistId.collectAsState()
    val currentPlayingPlaylist by viewModel.currentPlayingPlaylistId.collectAsState()


    val playListSongs: List<Audio> = audioList.filter { audio ->
        audio.id in currentPlayListSongs
    }

//    val playListSongs by remember {
//        mutableStateOf(audioList.filter {
//            it.id in currentPlayListSongs
//        })
//    }


    val setMediaItems = remember {
        mutableStateOf(false)
    }

    val previousPlaylistSize = remember { mutableIntStateOf(0) }
//    val scope = rememberCoroutineScope()


    val context = LocalContext.current


    //    BOTTOM SHEET
    val showSheet = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    var selectedSong by remember {
        mutableStateOf<Audio?>(null)
    }
    var selectedIndex by remember {
        mutableIntStateOf(0)
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

    val showAddSongsToPlaylistDialog = remember {
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
    val filteredPlaylists = playLists.filter { it.id != currentPlaylistId }


    //Show delete song dialog
    var showDeleteSongDialog by remember {
        mutableStateOf(false)
    }

    //Show set ringtone dialog
    val showConfirmSetRingtoneDialog = remember {
        mutableStateOf(false)
    }


    val maxVisiblePlaylists = 4


    //Song selection Part
    val selectedSongs = remember { mutableStateMapOf<Long, Boolean>() }

    var showDeleteSongsDialog by remember {
        mutableStateOf(false)
    }

    val showSelectAllDropDown = remember { mutableStateOf(false) }

    val playbackState = viewModel.retrievePlaybackState()
    val scope = rememberCoroutineScope()


    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
    DisposableEffect(backDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                val currentNavDestination = navBackStackEntry?.destination?.route
                if (selectedSongs.isNotEmpty() || selectedPlaylists.isNotEmpty()) {
                    selectedSongs.clear()

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
            if (selectedSongs.any { it.value }) {
                TopAppBar(
                    modifier = Modifier
                        .padding(end = 0.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SoundScapeThemes.colorScheme.secondary
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            selectedSongs.clear()
                        })
                        {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = White90
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showDeleteSongsDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = White90
                            )
                        }

                        IconButton(onClick = {
                            showAddSongsToPlaylistDialog.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = White90
                            )
                        }

                        IconButton(onClick = {
                            val selectedSongsList = selectedSongs
                                .filter { it.value } // Filter out only the selected songs
                                .map { it.key } // Extract the IDs of the selected songs

                            val selectedSongURIs = audioList
                                .filter { selectedSongsList.contains(it.id) } // Filter selected songs
                                .map { song -> song.uri } // Map each song to its URI

                            val selectedTitle = audioList
                                .filter { selectedSongsList.contains(it.id) } // Filter selected songs
                                .map { song -> song.title } // Map each song to its URI

                            viewModel.shareAudios(context, selectedSongURIs, selectedTitle)
                            selectedSongs.clear()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = White90
                            )
                        }
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
                                            playListSongs.forEach { song ->
                                                selectedSongs[song.id] = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            } else {
                TopAppBar(
                    modifier = Modifier
                        .padding(end = 0.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {},
                    navigationIcon = {
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
                                    tint = White90
                                )
                            }

                    },
                    actions = {

                    }
                )
            }
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
                    .padding(start = 14.dp, end = 14.dp)
                    .height(110.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(
                                data = if (currentPlayListSongs.isEmpty()) {
                                    R.drawable.sample
                                } else {
                                    playListSongs[0].artwork
                                }
                            )
                            .apply(block = fun ImageRequest.Builder.() {
                                error(R.drawable.sample)
                            }
                            ).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(.5.dp, White90, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 8.dp)
                ) {

                    Text(
                        text = "$currentPlaylistName",
                        color = White90,
                        style = SoundScapeThemes.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentPlayListSongs.size} songs",
                        color = White90,
                        style = SoundScapeThemes.typography.bodyMedium
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
                if (currentPlaylistName != "Favorites") {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(35.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White50
                        ),
                        onClick = {
                            if (navController.currentBackStackEntry?.lifecycle?.currentState
                                == Lifecycle.State.RESUMED
                            ) {
                                navController.navigate("add")
                            }
                        })
                    {
                        Text(
                            text = "Add Song",
                            color = White90.copy(.8f),
                            style = SoundScapeThemes.typography.bodyLarge
                        )
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(35.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White50
                        ),
                        onClick = {
                            if (currentPlayListSongs.isNotEmpty()) {
                                viewModel.setCurrentPlayingSection(2)
                                viewModel.setCurrentPlayingPlaylist(currentPlaylistId!!)

                                viewModel.setMediaItemFlag(false)
                                viewModel.setMediaItems(playListSongs, context)
                                viewModel.onUiEvents(UIEvents.PlayPause)

                                startService(context)

                                setMediaItems.value = true

                                if (navController.currentBackStackEntry?.lifecycle?.currentState
                                    == Lifecycle.State.RESUMED
                                ) {
                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
                                }

                                if (!player.shuffleModeEnabled) {
                                    viewModel.toggleShuffle()
                                }
                            } else {
                                Toast.makeText(context, "Playlist is empty!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    ) {
                        Text(
                            text = "Shuffle All",
                            color = White90.copy(.8f),
                            style = SoundScapeThemes.typography.bodyLarge
                        )
                    }
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
                        if (currentPlayListSongs.isNotEmpty()) {
                            viewModel.setCurrentPlayingSection(2)
                            viewModel.setCurrentPlayingPlaylist(currentPlaylistId!!)

                            viewModel.setMediaItemFlag(false)
                            viewModel.setMediaItems(playListSongs, context)
                            viewModel.onUiEvents(UIEvents.PlayPause)

                            startService(context)

                            setMediaItems.value = true

                            if (navController.currentBackStackEntry?.lifecycle?.currentState
                                == Lifecycle.State.RESUMED
                            ) {
                                navController.navigate(ScreenRoute.NowPlayingScreen.route)
                            }
                            if (player.shuffleModeEnabled) {
                                viewModel.toggleShuffle()
                            }

                        } else {
                            Toast.makeText(context, "Playlist is empty!", Toast.LENGTH_SHORT).show()
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                itemsIndexed(items = playListSongs, key = { _, playlist ->
                    playlist.id
                }) { index, song ->
                    val isSelected = selectedSongs[song.id] ?: false
                    SongItem(
                        key = song.id,
                        isSelected = isSelected,
                        selectedSongs = selectedSongs,
                        song = song,
                        onLongPress = {
                            if (!selectedSongs.any { selection ->
                                    selection.value
                                }) {
                                selectedSongs[song.id] =
                                    !(selectedSongs[song.id] ?: false)
                            }
                        },
                        onTap = {
                            if (selectedSongs.any { selection ->
                                    selection.value
                                }) {
                                selectedSongs[song.id] =
                                    !(selectedSongs[song.id] ?: false)
                            } else {
                                viewModel.setMediaItemFlag(false)
                                val selectedAudio = playListSongs.firstOrNull { it.id == song.id }

                                if (!setMediaItems.value) {
//                                        viewModel.setPlaylistMediaItems(currentPlayListSongs)
//                                        viewModel.playFromPlaylist(index)
                                    selectedAudio.let {
                                        viewModel.setMediaItems(playListSongs, context)
                                        viewModel.play(playListSongs.indexOf(selectedAudio))
                                    }

                                    startService(context)
                                } else if (viewModel.currentSelectedAudio != song.id) {

                                    viewModel.play(audioList.indexOf(selectedAudio))
//                                        viewModel.playFromPlaylist(index)
                                }
                                setMediaItems.value = true

                                if (navController.currentBackStackEntry?.lifecycle?.currentState
                                    == Lifecycle.State.RESUMED
                                ) {
                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
                                }

                                viewModel.setCurrentPlayingSection(2)
                                viewModel.setCurrentPlayingPlaylist(currentPlaylistId!!)
                            }
                        },
                        onIconClick = {
                            showSheet.value = true
                            selectedSong = song
                            selectedIndex = index
                            current.longValue = song.id
                            viewModel.getFavoritesSongs()
                        },
                        context = context,
                        isPlaying = (playbackState.lastPlayedSong.toLongOrNull() ?: 0L) == song.id,
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                        songDuration = song.duration.toLong()
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
                    isPlaylist = true,
                    onRemoveClick = {
                        showSheet.value = false
                        showDeleteSongDialog = true
                    },
                    selectedSong = selectedSong,
                    onShareClick = {
                        showSheet.value = false
                        viewModel.shareAudio(context, selectedSong!!.uri, selectedSong!!.title)
                    },
                    onSetAsRingToneClick = {
                        showConfirmSetRingtoneDialog.value = true
                        showSheet.value = false
                    }
                )
            }

            if (showAddPlaylistDialog.value || showAddSongsToPlaylistDialog.value) {
                AddSelectedSongsToPlaylist(
                    showAddPlaylistDialog = showAddPlaylistDialog,
                    confirmAddSong = showAddSongsToPlaylistDialog,
                    selectedPlaylists = selectedPlaylists,
                    enableButton = enableButton,
                    createPlaylist = createPlaylist,
                    playlists = filteredPlaylists,
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
                    onValueChange = { newName ->
                        playlistName = newName
                    },
                    confirmAddSong = showAddSongsToPlaylistDialog,
                    selectedSongs = selectedSongs,
                    viewModel = viewModel,
                    showAddPlaylistDialog = showAddPlaylistDialog,
                    context = context,
                    selectedSong = selectedSong
                )
            }

            if (showDeleteSongDialog || showDeleteSongsDialog) {
                val songMessage = if (!showDeleteSongsDialog)
                    "Remove song ${selectedSong!!.title}'?" else "Remove ${selectedSongs.size} songs'?"
                AlertDialog(
                    containerColor = SoundScapeThemes.colorScheme.secondary,
                    onDismissRequest = {
                        showDeleteSongDialog = false
                        showDeleteSongsDialog = false
                    },
                    title = {
                        Text(
                            text = "Remove Song",
                            color = White90
                        )
                    },
                    text = {
                        Text(
                            text = songMessage,
                            color = White50
                        )
                    },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            onClick = {
                                val selectedSongsList = selectedSongs
                                    .filter { song ->
                                        song.value
                                    }
                                    .map { selection ->
                                        selection.key
                                    }
                                if (showDeleteSongsDialog) {
                                    if (currentPlaylistName == "Favorites") {
                                        viewModel.removeMultipleSongsFromFavorites(selectedSongsList)
                                    } else {
                                        viewModel.deleteSongFromPlaylist(selectedSongsList)
                                    }
                                } else {
                                    if (currentPlaylistName != "Favorites") {
                                        viewModel.deleteSongFromPlaylist(
                                            listOf(
                                                selectedSong?.id ?: -1
                                            )
                                        )
                                    } else {
                                        viewModel.toggleFavorite(selectedSong!!.id)
                                    }
                                }
                                showDeleteSongDialog = false
                                showDeleteSongsDialog = false
                                selectedSongs.clear()
                            })
                        {
                            Text(
                                text = "Remove",
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
                                showDeleteSongDialog = false
                                showDeleteSongsDialog = false
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
            if (showSongDetailsDialog.value) {
                ShowSongDetailsDialog(
                    selectedSong = selectedSong!!,
                    showSongDetails = showSongDetailsDialog
                )
            }

            if(showConfirmSetRingtoneDialog.value){
                ConfirmSetRingtone(
                    onConfirm = {
                        viewModel.setRingtone(context,selectedSong!!.uri)
                        showSheet.value = false
                        showConfirmSetRingtoneDialog.value = false
                    },
                    selectedSong = selectedSong!!,
                    showConfirmSetRingtoneDialog = showConfirmSetRingtoneDialog
                )
            }
        }
    }
}

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

fun startService(context: Context) {
    val intent = Intent(context, MusicService::class.java)
    if (!isMediaSessionServiceRunning(context)) {
        startForegroundService(context, intent)
    } else {
//        startService(context)
    }
}