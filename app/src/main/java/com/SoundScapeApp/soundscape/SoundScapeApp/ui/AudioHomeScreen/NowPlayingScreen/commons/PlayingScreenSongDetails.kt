package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen.commons


import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayingScreenSongDetails(
    context: Context,
    currentPlayingSong: Audio?,
    onFavClick: () -> Unit,
    current: MutableLongState,
    currentPlayListSongs: List<Long>
) {


    Column {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .border(2.dp, Color.White, shape = RoundedCornerShape(24.dp))
                .weight(1f)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(data = currentPlayingSong?.artwork)
                        .apply(block = fun ImageRequest.Builder.() {
                            error(R.drawable.sample)
                        }
                        ).build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = currentPlayingSong?.title ?: "Unknown Title",
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = White90,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(280.dp)
                .basicMarquee(
                    animationMode = MarqueeAnimationMode.Immediately,
                    iterations = 1000,
                    initialDelayMillis = 1000,
                    //delayMillis = 2000,
                    velocity = 30.dp
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (currentPlayingSong?.artist == "<unknown>" || currentPlayingSong?.artist == null)
                "Unknown Artist" else currentPlayingSong.artist,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = White50,
            modifier = Modifier
                .width(280.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                onFavClick()
            })
            {
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
    }
}
