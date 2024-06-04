package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.commmons


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons.formatDuration
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.playingSongBg


//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun SongItem(
//    key: Long,
//    isSelected: Boolean,
//    selectedSongs: MutableMap<Long, Boolean>,
//    song: Audio,
//    onLongPress: ((Offset)) -> Unit,
//    onTap: ((Offset)) -> Unit,
//    context: Context,
//    onIconClick: () -> Unit,
//    isPlaying: Boolean = false,
//    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
//) {
//
//    Row(
//        modifier = modifier
//            .fillMaxHeight()
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onLongPress = {
//                        onLongPress(it)
//                    },
//                    onTap = {
//                        onTap(it)
//                    }
//                )
//            }
//            .padding(
//                top = 4.dp,
//                bottom = 4.dp,
//                start = 5.dp, end = 5.dp
//            )
//            .clip(RoundedCornerShape(12.dp))
//            .background(
//                if(isSelected) SoundScapeThemes.colorScheme.primary else White90.copy(.02f)
//            )
//            .border(.1.dp, White90.copy(.15f), RoundedCornerShape(12.dp))
//            .padding(
//                top = 6.dp,
//                bottom = 6.dp,
//                start = 8.dp, end = 8.dp
//            )
////            .animateItemPlacement(
////                animationSpec = tween(durationMillis = 400)
////            )
//        , verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(contentAlignment = Alignment.Center) {
//            Image(
//                painter = rememberAsyncImagePainter(
//                    ImageRequest.Builder(context)
//                        .data(song.artwork)
//                        .placeholder(R.drawable.sample)
//                        .error(R.drawable.sample)
//                        .memoryCachePolicy(CachePolicy.ENABLED)
//                        .diskCachePolicy(CachePolicy.ENABLED)
//                        .build()
//                ),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(46.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
////            Spacer(
////                modifier = Modifier
////                    .size(16.dp)
////                    .clip(CircleShape)
////                    .background(Theme2Secondary)
////            )
//        }
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Column(
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = song.title,
//                style = SoundScapeThemes.typography.titleSmall,
//                color = if (isPlaying) playingSongBg else White90,
//                maxLines = 1,
//                modifier = Modifier
//                    .fillMaxWidth(.95f)
//                    .basicMarquee(
//                        iterations = if (isPlaying) 1000 else 0
//                    ),
//                overflow = TextOverflow.Ellipsis,
//            )
//            Text(
//                text = if (song.artist == "<unknown>") "Unknown Artist" else song.artist,
//                style = SoundScapeThemes.typography.bodyMedium,
//                color = White50,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier.fillMaxWidth(.95f)
//            )
//        }
//
//        if (selectedSongs.any { it.value }) {
//            OutlinedIconButton(
//                modifier = Modifier.size(20.dp),
//                border = BorderStroke(1.dp, White90),
//                onClick = {
//                    selectedSongs[song.id] =
//                        !(selectedSongs[song.id] ?: false)
//                })
//            {
//                if (isSelected
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = null,
//                        tint = White90,
//                        modifier = Modifier.size(14.dp)
//                    )
//                }
//            }
//        } else {
//            IconButton(
//                modifier = Modifier.size(32.dp),
//                onClick = {
//                    onIconClick()
//                }) {
//                Icon(
//                    imageVector = Icons.Default.MoreVert,
//                    contentDescription = null,
//                    tint = White90
//                )
//            }
//        }
//    }
//}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    key: Long,
    isSelected: Boolean,
    selectedSongs: MutableMap<Long, Boolean>,
    song: Audio,
    onLongPress: ((Offset)) -> Unit,
    onTap: ((Offset)) -> Unit,
    context: Context,
    onIconClick: () -> Unit,
    isPlaying: Boolean = false,
    songDuration:Long = 0L,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPress(it)
                    },
                    onTap = {
                        onTap(it)
                    }
                )
            }
            .padding(
                top = 2.dp,
                bottom = 2.dp,
            )
            .background(
                if (isSelected) SoundScapeThemes.colorScheme.primary else SoundScapeThemes.colorScheme.primary.copy(.01f)
            )
            .padding(
                top = 6.dp,
                bottom = 6.dp,
                start = 8.dp, end = 8.dp
            )
//            .animateItemPlacement(
//                animationSpec = tween(durationMillis = 400)
//            )
        , verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(song.artwork)
                        .placeholder(R.drawable.sample2)
                        .error(R.drawable.sample2)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

//            Spacer(
//                modifier = Modifier
//                    .size(16.dp)
//                    .clip(CircleShape)
//                    .background(Theme2Secondary)
//            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
//                style = SoundScapeThemes.typography.titleSmall,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isPlaying) playingSongBg else White90,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth(.95f)
                    .basicMarquee(
                        iterations = if (isPlaying) 1000 else 0
                    ),
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = if (song.artist == "<unknown>") "Unknown Artist" else song.artist,
//                    style = SoundScapeThemes.typography.bodyMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = White50,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(.95f)
                )
            }
        }



        Spacer(modifier = Modifier.width(3.dp))
        Text(text = formatDuration(songDuration),
            color = White90.copy(.8f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1)
        Spacer(modifier = Modifier.width(if(selectedSongs.any { it.value }) 8.dp else 0.dp))

        if (selectedSongs.any { it.value }) {
            OutlinedIconButton(
                modifier = Modifier.size(20.dp),
                border = BorderStroke(1.dp, White90),
                onClick = {
                    selectedSongs[song.id] =
                        !(selectedSongs[song.id] ?: false)
                })
            {
                if (isSelected
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = White90,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        } else {
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                    onIconClick()
                }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = White90
                )
            }
        }
    }
}