package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.AllSongs

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.AddSelectedSongsToPlaylist
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.AllSongs.common.AudioListSorting
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists.startService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.CreatePlaylistAndAddSong
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.MainBottomSheet
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.ShowSongDetailsDialog
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons.SongItem
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme2Primary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
)
@Composable
fun AllSongs(
    listState: LazyListState,
    onItemClick: (Int, Long) -> Unit,
    navController: NavController,
    context: Context,
    viewModel: AudioViewModel,
    selectedSongs: MutableMap<Long, Boolean>,
    confirmAddSong: MutableState<Boolean>,
    search: String,
    onSelectAllSongsClicked: MutableState<Boolean>,
    deleteSelectedSong: MutableState<Boolean>,
    onSongDelete: (List<Uri>) -> Unit,
    selectedSongsCount: MutableState<Int>,
    selectedSongsIds: MutableList<Long>
) {

    val currentSortType = viewModel.currentSortType
    val audioList by viewModel.scannedAudioList.collectAsState()


    val showSheet = remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()

    val showSort = remember {
        mutableStateOf(false)
    }

    var selectedSong by remember {
        mutableStateOf<Audio?>(null)
    }

    val selectedPlaylists = remember { mutableStateMapOf<Long, Boolean>() }
    val enableButton = remember {
        mutableStateOf(false)
    }

    val showAddPlaylistDialog = remember {
        mutableStateOf(false)
    }

    val playLists by viewModel.playlists.collectAsState()
    val currentPlayListSongs by viewModel.favoritesSongs.collectAsState()


    val current = remember {
        mutableLongStateOf(0L)
    }

    val createPlaylist = remember {
        mutableStateOf(false)
    }

    val showSongDetailsDialog = remember { mutableStateOf(false) }

    var playlistName by remember {
        mutableStateOf("")
    }

    val maxVisiblePlaylists = 4


//    val filteredAudioList = remember(search, audioList) {
//        if (search.isNotBlank()) {
//            viewModel.isAppSearch(true)
//            audioList.filter { audio ->
//                audio.title.contains(search, ignoreCase = true) ||
//                        audio.artist.contains(search, ignoreCase = true)
//            }
//        } else {
//            viewModel.isAppSearch(false)
//            audioList
//        }
//    }

    val filteredAudioList = if (search.isNotBlank()) {
        viewModel.isAppSearch(true)
        audioList.filter { audio ->
            audio.title.contains(search, ignoreCase = true) ||
                    audio.artist.contains(search, ignoreCase = true)
        }
    } else {
        viewModel.isAppSearch(false)
        audioList
    }

    val showDeleteDialog = remember { mutableStateOf(false) }


    LaunchedEffect(onSelectAllSongsClicked.value) {
        if (onSelectAllSongsClicked.value) {
            filteredAudioList.forEach { song ->
                selectedSongs[song.id] = true
                selectedSongsCount.value = selectedSongs.count { it.value }
                onSelectAllSongsClicked.value = false
            }
        }
    }


    LaunchedEffect(deleteSelectedSong.value) {
        if (deleteSelectedSong.value) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                showDeleteDialog.value = true
                deleteSelectedSong.value = false
            } else {
                val selectedSongsList = selectedSongs
                    .filter { it.value } // Filter out only the selected songs
                    .map { it.key } // Extract the IDs of the selected songs

                val selectedSongURIs = filteredAudioList
                    .filter { selectedSongsList.contains(it.id) } // Filter selected songs
                    .map { song -> song.uri } // Map each song to its URI

                onSongDelete(selectedSongURIs)
                deleteSelectedSong.value = false
                selectedSongs.clear()
                selectedSongsIds.clear()
                viewModel.setIsSongSelected(false)

            }
        }
    }

    val playbackState = viewModel.retrievePlaybackState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 8.dp,
                bottom = 0.dp,
            ),

        ) {

        key(audioList, search) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Text(
                            text = "Total Songs: ${filteredAudioList.size}",
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
                                contentDescription = "all songs list sorting button",
                                modifier = Modifier.size(24.dp),
                                tint = White90
                            )
                        }
                    }
                }

                items(items = filteredAudioList) { song ->
                    SongItem(
                        key = song.id, // Provide a unique key for each song
                        isSelected = selectedSongs[song.id] ?: false,
                        selectedSongs = selectedSongs,
                        song = song,
                        onLongPress = {
                            if (!selectedSongs.any { it.value }) {
                                selectedSongs.clear()
                                selectedSongs[song.id] = !(selectedSongs[song.id] ?: false)
                                selectedSongsCount.value = selectedSongs.size
                                viewModel.setIsSongSelected(selectedSongs.any { it.value })
                                toggleSongSelection(song.id, selectedSongsIds)
                            }
                        },
                        onTap = {
                            if (selectedSongs.any { it.value }) {
                                selectedSongs[song.id] = !(selectedSongs[song.id] ?: false)
                                selectedSongsCount.value = selectedSongs.count { it.value }
                                viewModel.setIsSongSelected(selectedSongs.any { it.value })
                                toggleSongSelection(song.id, selectedSongsIds)
                            } else {
                                onItemClick(0, song.id)
                                if (navController.currentBackStackEntry?.lifecycle?.currentState
                                    == Lifecycle.State.RESUMED
                                ) {
                                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
                                }
                            }
                        },
                        context = context,
                        onIconClick = {
                            showSheet.value = true
                            selectedSong = song
                            current.longValue = song.id
                        },
                        isPlaying = (playbackState.lastPlayedSong.toLongOrNull() ?: 0L) == song.id,
                        modifier = Modifier
                            .animateItemPlacement(
                                animationSpec = spring(
                                    Spring.DampingRatioLowBouncy,
                                    Spring.StiffnessLow
                                )
                            )

                    )
                }

                item {
                    Spacer(modifier = Modifier.height(74.dp))
                }
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
                    viewModel.setMediaItemFlag(false)
                    startService(context)
                    viewModel.setCurrentPlayingSection(0)
//                    viewModel.setMediaItems(filteredAudioList)
//                    onItemClick(0, selectedSong!!.id)
//                    navController.navigate(ScreenRoute.NowPlayingScreen.route)
                },
                onAddToPlaylistClick = {
                    showAddPlaylistDialog.value = true
                    showSheet.value = false
                },
                onDetailsClick = {
                    showSongDetailsDialog.value = true
                    showSheet.value = false
                },
                current = current,
                currentPlayListSongs = currentPlayListSongs,
                selectedSong = selectedSong
            )
        }

        if (showAddPlaylistDialog.value || confirmAddSong.value) {
            AddSelectedSongsToPlaylist(
                showAddPlaylistDialog = showAddPlaylistDialog,
                confirmAddSong = confirmAddSong,
                selectedPlaylists = selectedPlaylists,
                enableButton = enableButton,
                createPlaylist = createPlaylist,
                playlists = playLists,
                maxVisiblePlaylists = maxVisiblePlaylists,
                selectedSongs = selectedSongs,
                viewModel = viewModel,
                context = context,
                isAllSongsSelected = onSelectAllSongsClicked,
                selectedSong = selectedSong
            )
        }

        if (createPlaylist.value) {
            CreatePlaylistAndAddSong(
                createPlaylist = createPlaylist,
                playlistName = playlistName,
                onValueChange = { playlistName = it },
                confirmAddSong = confirmAddSong,
                selectedSongs = selectedSongs,
                viewModel = viewModel,
                showAddPlaylistDialog = showAddPlaylistDialog,
                context = context,
                selectedSong = selectedSong
            )
        }

        if (showSort.value) {
            AudioListSorting(
                showSort = showSort,
                viewModel = viewModel,
                currentSortType = currentSortType,
                onSortClick = {
                    selectedSongs.clear()
                    selectedSongsIds.clear()
                }
            )
        }

        if (showSongDetailsDialog.value) {
            ShowSongDetailsDialog(
                selectedSong = selectedSong!!,
                showSongDetails = showSongDetailsDialog
            )
        }

        if (showDeleteDialog.value) {
            AlertDialog(
                containerColor = Theme2Primary,
                onDismissRequest = {
                    showDeleteDialog.value = false
                },
                title = {
                    Text(
                        text = "Delete Songs?",
                        color = White90
                    )
                },
                text = {
                    Text(
                        text = "${selectedSongsIds.size} songs will be deleted",
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
                                .filter { it.value } // Filter out only the selected songs
                                .map { it.key } // Extract the IDs of the selected songs

                            val selectedSongURIs = filteredAudioList
                                .filter { selectedSongsList.contains(it.id) } // Filter selected songs
                                .map { song -> song.uri } // Map each song to its URI


                            onSongDelete(selectedSongURIs)
                            deleteSelectedSong.value = false
                            selectedSongs.clear()
                            selectedSongsIds.clear()
                            viewModel.setIsSongSelected(false)
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
    }
}

private fun toggleSongSelection(songId: Long, selectedSongIds: MutableList<Long>) {
    if (selectedSongIds.contains(songId)) {
        selectedSongIds.remove(songId)
    } else {
        selectedSongIds.add(songId)
    }
}
