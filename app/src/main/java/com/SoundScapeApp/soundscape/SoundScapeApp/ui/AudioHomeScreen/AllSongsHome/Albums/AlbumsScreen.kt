package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.Albums

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.White90


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumsScreen(
    audioList: List<Audio>,
    viewModel: AudioViewModel,
    navController: NavController,
    search: String
) {
    val groupedAlbums = audioList.groupBy { it.albumName }

    val albums = groupedAlbums.keys.toList()

    val filteredAlbums = if (search.isNotBlank()) {
        albums.filter { album ->
            album.contains(search, ignoreCase = true)
        }
    } else {
        albums
    }

    val gridState = rememberLazyGridState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        LazyVerticalGrid(
            modifier = Modifier.padding(top = 12.dp, bottom = 0.dp),
            columns = GridCells.Adaptive(140.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            state = gridState
        ) {
            items(items = filteredAlbums, key = { it }) { albumId ->
                val album = groupedAlbums[albumId]?.firstOrNull()

                album?.let {
                    AlbumGridItem(
                        album = it,
                        onClick = {
                            viewModel.loadSongsForAlbum(it.albumId.toLong())
                            viewModel.albumClicked(it.albumId.toLong())
                            navController.navigate(ScreenRoute.AlbumDetailScreen.route)
                        },
                        modifier = Modifier
                            .animateItemPlacement(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            .animateContentSize()
                    )
                }
            }
        }
    }
}


@Composable
fun AlbumGridItem(
    album: Audio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val painter: Painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = album.artwork)
            .apply(block = fun ImageRequest.Builder.() {
                placeholder(R.drawable.musicnote)
                error(R.drawable.musicnote)
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
            contentDescription = "album cover art",
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop
        )

        Text(
            text = album.albumName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            color = White90,
            style = MaterialTheme.typography.labelMedium
        )
    }
}


//@Composable
//fun AlbumGridItem(
//    album: Audio,
//    onClick: () -> Unit
//) {
//    val painter: Painter =
//        rememberAsyncImagePainter(
//            ImageRequest.Builder
//                (LocalContext.current).data(data = album.artwork)
//                .apply(block = fun ImageRequest.Builder.() {
//                    placeholder(R.drawable.musicnote)
//                    error(R.drawable.musicnote)
//                }).build()
//        )
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Column(
//            modifier = Modifier
//                .clip(MaterialTheme.shapes.medium)
//                .clickable(
//                    onClick = onClick
//                )
//        ) {
//            Image(
//                painter = painter,
//                contentDescription = null,
//                modifier = Modifier
//                    .clip(MaterialTheme.shapes.medium)
//                    .size(170.dp),
//                contentScale = ContentScale.Crop
//            )
//        }
//
//        Text(
//            text = album.albumName,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier
//                .padding(top = 8.dp)
//                .fillMaxWidth(),
//            style = MaterialTheme.typography.labelMedium
//        )
//    }
//}
