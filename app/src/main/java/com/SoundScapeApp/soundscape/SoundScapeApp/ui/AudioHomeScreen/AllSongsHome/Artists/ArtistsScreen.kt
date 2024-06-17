package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.Artists

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.White90


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistsScreen(
    audioList: List<Audio>,
    viewModel: AudioViewModel,
    navController: NavController,
    context: Context,
    search: String

) {
    val groupedArtist = audioList.reversed().groupBy { it.artist }

    val artists = groupedArtist.keys.toList()

    val filteredArtists = if (search.isNotBlank()) {
        artists.filter { artist ->
            artist.contains(search, ignoreCase = true)
        }
    } else {
        artists
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        LazyVerticalGrid(
            modifier = Modifier.padding(top = 12.dp, bottom = 0.dp),
            columns = GridCells.Adaptive(140.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = filteredArtists, key = { it }) { artistId ->
                val artist = groupedArtist[artistId]?.reversed()?.firstOrNull()

                artist?.let {
                    ArtistGridItem(

                        artist = it,
                        onClick = {
                            viewModel.loadSongsForArtist(it.artist)
                            viewModel.artistClicked(it.artist)
                            navController.navigate(ScreenRoute.ArtistDetailScreen.route)
                        },
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun ArtistGridItem(
    artist: Audio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val painter: Painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = artist.artwork)
            .apply(block = fun ImageRequest.Builder.() {
                placeholder(R.drawable.musicnote)
                error(R.drawable.sample)
            }
            ).build()
    )

    // Remove outer clipping, as it's redundant
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = onClick
            )
    ) {
        Image(
            painter = painter,
            contentDescription = "artists cover art",
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop
        )


        Text(
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            text = artist.artist.ifEmpty {
                "Unknown Artist"
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp)
                .fillMaxWidth(),
            color = White90,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
