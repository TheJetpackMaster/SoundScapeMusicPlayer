package com.example.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons


import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.soundscape.R
import com.example.soundscape.SoundScapeApp.data.Audio
import com.example.soundscape.ui.theme.SoundScapeThemes
import com.example.soundscape.ui.theme.Theme2Primary
import com.example.soundscape.ui.theme.Theme2Secondary
import com.example.soundscape.ui.theme.White50
import com.example.soundscape.ui.theme.White90



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomSheet(
    showSheet: MutableState<Boolean>,
    sheetState: SheetState,
    context: Context,
    selectedSong: Audio? = null,
    onFavClick: () -> Unit,
    onPlayClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onDetailsClick: () -> Unit,
    current: MutableLongState,
    currentPlayListSongs: List<Long>,
    isPlaylist:Boolean = false,
    onRemoveClick:()->Unit = {},
    sheetBackgroundColor:Color = SoundScapeThemes.colorScheme.secondary

    ) {
    ModalBottomSheet(
        containerColor = sheetBackgroundColor,
        dragHandle = {},
        onDismissRequest = {
            showSheet.value = false
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 12.dp, bottom = 42.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .padding(start = 12.dp, end = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    selectedSong?.let {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(selectedSong.artwork)
                                    .placeholder(R.drawable.sample)
                                    .error(R.drawable.sample)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape),
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
                        text = selectedSong!!.displayName,
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(225.dp)
                    )
                    Text(
                        text = selectedSong.artist,
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White50,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(200.dp)
                    )
                }

                IconButton(onClick = {
                    onFavClick()
                }) {
                    Icon(
                        imageVector = if (current.longValue in currentPlayListSongs) {
                            Icons.Default.Favorite // Red heart icon
                        } else {
                            Icons.Default.FavoriteBorder // White heart icon
                        },
                        contentDescription = null,
                        tint = if (current.longValue in currentPlayListSongs) Color.Red else White90,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                color = White90,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        onPlayClick()
                    }
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    border = BorderStroke(1.dp, White90),
                    modifier = Modifier.size(22.dp),
                    onClick = {
                        onPlayClick()
                    })
                {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = White90,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Play",
                    color = White90,
                    style = SoundScapeThemes.typography.bodyLarge
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        onAddToPlaylistClick()
                    }
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    border = BorderStroke(1.dp, White90),
                    modifier = Modifier.size(22.dp),
                    onClick = {
                        onAddToPlaylistClick()
                    })
                {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = White90,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Add to Playlist",
                    color = White90,
                    style = SoundScapeThemes.typography.bodyLarge
                )
            }

            if(isPlaylist) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable {
                            onRemoveClick()
                        }
                        .padding(start = 12.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedIconButton(
                        border = BorderStroke(1.dp, White90),
                        modifier = Modifier.size(22.dp),
                        onClick = {
                            onRemoveClick()
                        })
                    {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = White90,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Remove Song",
                        color = White90,
                        style = SoundScapeThemes.typography.bodyLarge
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        onDetailsClick()
                    }
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    border = BorderStroke(1.dp, White90),
                    modifier = Modifier.size(22.dp),
                    onClick = {
                        onDetailsClick()
                    })
                {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = White90,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Details",
                    color = White90,
                    style = SoundScapeThemes.typography.bodyLarge
                )
            }
        }
    }
}
