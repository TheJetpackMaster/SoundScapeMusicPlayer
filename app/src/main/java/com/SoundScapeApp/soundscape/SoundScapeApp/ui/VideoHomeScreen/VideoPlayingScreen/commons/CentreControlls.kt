package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.VideoPlayingScreen.commons


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes

@Composable
fun CenterControls(
    isMuted: MutableState<Boolean>,
    onLockClick: () -> Unit,
    onMuteClick: () -> Unit,
    onScreenRotationClick: () -> Unit,
    onVideoLoopClick: () -> Unit,
    isVideoLooping: Boolean
) {
    Column {
        // First row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onLockClick() },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(0.4f)),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onVideoLoopClick() },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(0.4f)),
            ) {
                Icon(
                    painter = if (isVideoLooping) painterResource(id = R.drawable.repeatone) else painterResource(id = R.drawable.repeatall),
                    contentDescription = null,
                    tint = if (isVideoLooping) Color.Green.copy(0.9f) else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onMuteClick() },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(0.4f)),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.volume),
                    contentDescription = null,
                    tint = if (isMuted.value) Color.Green.copy(0.9f) else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onScreenRotationClick() },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(0.4f)),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_screen_rotation_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
