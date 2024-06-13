package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Folders.commons

import android.provider.MediaStore.Video
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderVideoListTopBar(
    isSelected: Boolean,
    title: String,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onAdd: () -> Unit,
    onMore: () -> Unit,
    onShare: () -> Unit
) {
    if (isSelected) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SoundScapeThemes.colorScheme.secondary
            ),
            title = {

            },
            navigationIcon = {
                IconButton(onClick = {
                    onClear()
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
                    onAdd()
                })
                {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = White90
                    )
                }

                IconButton(onClick = {
                    onShare()
                })
                {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = White90
                    )
                }

//                IconButton(onClick = {
//                    onMore()
//                })
//                {
//                    Icon(
//                        imageVector = Icons.Default.MoreVert,
//                        contentDescription = null,
//                        tint = White90
//                    )
//                }
            }
        )
    } else {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            title = {
                Text(
                    text = title,
                    color = White90
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBack()
                })
                {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
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