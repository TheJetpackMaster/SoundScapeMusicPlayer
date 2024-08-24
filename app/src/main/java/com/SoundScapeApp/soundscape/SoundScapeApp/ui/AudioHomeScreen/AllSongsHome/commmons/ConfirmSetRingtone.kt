package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons

import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@Composable
fun ConfirmSetRingtone(
    onConfirm: () -> Unit,
    selectedSong: Audio,
    showConfirmSetRingtoneDialog: MutableState<Boolean>
) {
    AlertDialog(
        containerColor = SoundScapeThemes.colorScheme.secondary,
        onDismissRequest = {
            showConfirmSetRingtoneDialog.value = false
        },
        title = {
            Text(
                text = "set as ringtone?",
                color = White90
            )
        },
        text = {
            Text(
                text = selectedSong.title, color = White50,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = White50.copy(.5f)
                ),
                onClick = {
                    onConfirm()

                })
            {
                Text(
                    text = "Confirm",
                    color = SoundScapeThemes.colorScheme.secondary
                )
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                   showConfirmSetRingtoneDialog.value = false

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