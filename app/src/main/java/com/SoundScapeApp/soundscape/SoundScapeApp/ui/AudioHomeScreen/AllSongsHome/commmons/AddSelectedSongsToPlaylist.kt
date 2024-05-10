package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Playlist
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@Composable
fun AddSelectedSongsToPlaylist(
    showAddPlaylistDialog:MutableState<Boolean>,
    confirmAddSong: MutableState<Boolean>,
    selectedPlaylists:MutableMap<Long, Boolean>,
    enableButton:MutableState<Boolean>,
    createPlaylist:MutableState<Boolean>,
    playlists:List<Playlist>,
    maxVisiblePlaylists:Int,
    selectedSongs: MutableMap<Long, Boolean>,
    selectedSong:Audio? = null,
    viewModel: AudioViewModel,
    context:Context,
    dialogBackgroundColor:Color = SoundScapeThemes.colorScheme.secondary,
    isAllSongsSelected:MutableState<Boolean> = mutableStateOf(false),

){
    if(showAddPlaylistDialog.value || confirmAddSong.value) {
        AlertDialog(
            containerColor = dialogBackgroundColor,
            onDismissRequest = {
                showAddPlaylistDialog.value = false
                confirmAddSong.value = false
                selectedPlaylists.clear()
                enableButton.value = selectedPlaylists.any { it.value }
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
                                .background(SoundScapeThemes.colorScheme.secondary)
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

                    val boxHeight = if (playlists.size <= maxVisiblePlaylists) {
                        (60.dp * playlists.size).coerceAtMost(250.dp)
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
                            items(items = playlists, key = { it.id }) { playlist ->
                                val isSelected = selectedPlaylists[playlist.id] ?: false
                                val firstSongId = playlist.songIds.lastOrNull()

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
                                                enableButton.value =
                                                    selectedPlaylists.any { it.value }
                                            }
                                        ),
                                    verticalAlignment = Alignment.CenterVertically

                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SoundScapeThemes.colorScheme.secondary)
                                            .border(.1.dp, White90, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        firstSongId?.let {
                                            val imageUrl = viewModel.getSongImageUrl(it)
                                            val painter = rememberAsyncImagePainter(
                                                imageUrl
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
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )

                                        Text(
                                            text = "${playlist.songIds.size} songs",
                                            fontSize = 14.sp,
                                            color = White50,
                                        )

                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    OutlinedIconButton(
                                        border = BorderStroke(width = 1.dp, color = White90),
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = if (isSelected) White90 else Color.Transparent
                                        ),
                                        modifier = Modifier.size(22.dp),
                                        onClick = {
                                            selectedPlaylists[playlist.id] = !isSelected
                                            enableButton.value =
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
                                showAddPlaylistDialog.value = false
                                confirmAddSong.value = false
                                selectedPlaylists.clear()
                                isAllSongsSelected.value = false
                                viewModel.setIsSongSelected(false)
                                enableButton.value = selectedPlaylists.any { it.value }

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
                            enabled = enableButton.value,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                val selectedPlayListsList =
                                    selectedPlaylists.filter { it.value }.map { it.key }
                                val selectedSongsList = selectedSongs
                                    .filter { it.value }
                                    .map { it.key }
                                selectedPlayListsList.forEach { playlistId ->
                                    if (showAddPlaylistDialog.value) {
                                        viewModel.addSongToDifferentPlaylist(
                                            playlistId,
                                            listOf(selectedSong?.id ?: -1),
                                            context
                                        )
                                        Log.d("added1",selectedSong?.displayName?:"")
                                    } else {
                                        viewModel.addSongToDifferentPlaylist(
                                            playlistId,
                                            selectedSongsList,
                                            context
                                        )
                                        Log.d("added","added")
                                    }
                                }
                                selectedPlaylists.clear()
                                selectedSongs.clear()
                                enableButton.value = selectedPlaylists.any { it.value }
                                showAddPlaylistDialog.value = false
                                confirmAddSong.value = false
                                viewModel.setIsSongSelected(false)
                                isAllSongsSelected.value = false
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
}