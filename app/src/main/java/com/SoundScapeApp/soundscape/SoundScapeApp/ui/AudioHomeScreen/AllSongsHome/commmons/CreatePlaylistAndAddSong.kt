package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90


@Composable
fun CreatePlaylistAndAddSong(
    createPlaylist:MutableState<Boolean>,
    playlistName:String,
    onValueChange:(String)->Unit,
    confirmAddSong:MutableState<Boolean>,
    selectedSongs: MutableMap<Long, Boolean>,
    viewModel: AudioViewModel,
    showAddPlaylistDialog:MutableState<Boolean>,
    selectedSong:Audio? = null,
    context: Context,
    dialogBoxBackgroundColor:Color = SoundScapeThemes.colorScheme.secondary,
    isAllSongsSelected:MutableState<Boolean> = mutableStateOf(false),

    ){
    AlertDialog(
        containerColor = dialogBoxBackgroundColor,
        onDismissRequest = { createPlaylist.value = false },
        title = {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                TextField(
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = White90,
                        unfocusedTextColor = White90,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    value = playlistName,
                    onValueChange = {
                        onValueChange(it)
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
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            createPlaylist.value = false
                            confirmAddSong.value = false

                        })
                    {
                        Text(
                            color = White90,
                            text = "Cancel")
                    }

                    Button(
                        enabled = playlistName.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White90,
                            disabledContainerColor = White50
                        ),
                        onClick = {
                            val selectedSongsList = selectedSongs
                                .filter { it.value }
                                .map { it.key }

                            if (playlistName != "") {
                                viewModel.saveNewPlaylist(
                                    newPlaylistName = playlistName,
                                )

                                if (showAddPlaylistDialog.value) {
                                    viewModel.createAndAddSongToPlaylist(
                                        songIds = listOf(selectedSong?.id ?: -1),
                                        context = context
                                    )
                                } else {
                                    viewModel.createAndAddSongToPlaylist(
                                        songIds = selectedSongsList,
                                        context = context
                                    )
                                }
                            }
                            createPlaylist.value = false
                            showAddPlaylistDialog.value = false
                            confirmAddSong.value = false
                            isAllSongsSelected.value = false
                            selectedSongs.clear()
                            viewModel.setIsSongSelected(false)
                        })
                    {
                        Text(
                            color = SoundScapeThemes.colorScheme.primary,
                            text = "Done")
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
