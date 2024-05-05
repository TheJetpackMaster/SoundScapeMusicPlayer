package com.example.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import com.example.soundscape.SoundScapeApp.data.Audio
import com.example.soundscape.ui.theme.SoundScapeThemes
import com.example.soundscape.ui.theme.White50
import com.example.soundscape.ui.theme.White90

@Composable
fun ShowSongDetailsDialog(
    selectedSong:Audio,
    showSongDetails:MutableState<Boolean>
) {
    AlertDialog(
        containerColor = SoundScapeThemes.colorScheme.secondary,
        onDismissRequest = {
           showSongDetails.value = false
        },
        title = {

        },
        text = {
            Text(
                text = selectedSong.data,
                color = White50
            )
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                    showSongDetails.value = false
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