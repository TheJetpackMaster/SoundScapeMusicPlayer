package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.PlayLists


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme2Secondary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun AddSongsToPlaylist(
    navController: NavController,
    viewModel: AudioViewModel,
) {

//    val selectedSongs by remember { mutableStateOf<MutableSet<Long>>(mutableSetOf()) }
    val selectedSongs = remember { mutableStateMapOf<Long, Boolean>() }

    var showFAB by remember {
        mutableStateOf(false)
    }
    val currentPlayListSongs by viewModel.currentPlaylistSongs.collectAsState()


    var searchValue by remember {
        mutableStateOf("")
    }
    val audioList by viewModel.scannedAudioList.collectAsState()

    val filteredAudioList = if (searchValue.isNotBlank()) {
        audioList.filter { audio ->
            audio.displayName.contains(searchValue, ignoreCase = true) ||
                    audio.artist.contains(searchValue, ignoreCase = true)
        }
    } else {
        audioList
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    val showSearch = remember { mutableStateOf(false) }


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(end = 8.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    if (showSearch.value) {
                        Box {
                            Text(
                                text = if (searchValue.isEmpty()) "Search Song" else "",
                                color = White90,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .align(Alignment.CenterStart)
                                    .alpha(.5f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                BasicTextField(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth(),
                                    keyboardActions = KeyboardActions(

                                    ),
                                    cursorBrush = SolidColor(Color.White),
                                    value = searchValue,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = White90,
                                        fontSize = 14.sp
                                    ),
                                    onValueChange = {
                                        searchValue = it
                                    },
                                    decorationBox = { innerTextField ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(.3.dp, White50, RoundedCornerShape(8.dp))
                                                .padding(start = 8.dp, end = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        )
                                        {
                                            innerTextField()

                                            IconButton(onClick = {
                                                searchValue = ""
                                            })
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = null,
                                                    tint = White90
                                                )
                                            }
                                        }
                                    }
                                )


                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(onClick = {

                                })
                                {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = White90
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = if (selectedSongs.values.isNotEmpty()) "${selectedSongs.values.size} songs selected"
                            else "Select songs",
                            color = White90
                        )
                    }
                },
                actions = {
                    if (showSearch.value) {
                        Text(text = "cancel",
                            color = White90,
                            modifier = Modifier.clickable {
                                keyboardController?.hide()
                                searchValue = ""
                                showSearch.value = false
                            }
                        )
                    } else {
                        IconButton(onClick = {
                            showSearch.value = true
                        })
                        {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = White90
                            )
                        }
                    }
                }
            )
        },
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(
                        top = 8.dp,
                        bottom = 0.dp
                    )
            ) {
                LazyColumn(
                    modifier = Modifier.padding(bottom = if (showFAB) 48.dp else 0.dp)
                ) {
                    items(
                        items = filteredAudioList.filterNot { audio -> audio.id in currentPlayListSongs },
                        key = { it.id })
                    { song ->
//                        val isSelected by remember { mutableStateOf(selectedSongs.contains(song.id)) }
                        val isSelected = selectedSongs[song.id] ?: false
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(62.dp)
                                .clickable(
                                    onClick = {
                                        selectedSongs[song.id] = !(selectedSongs[song.id] ?: false)
                                        showFAB = selectedSongs.any { it.value }
                                    }
                                )
                                .background(
                                    if (isSelected) SoundScapeThemes.colorScheme.primary.copy(.9f)
                                    else Color.Transparent
                                )
                                .padding(8.dp)
                                .animateItemPlacement(animationSpec = tween(400)),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = song.artwork)
                                            .apply(block = fun ImageRequest.Builder.() {
                                                crossfade(true)
                                                error(R.drawable.sample)
                                            }).build()
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Theme2Secondary)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier
                                    .fillParentMaxHeight(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = song.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artist,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White50,
                                    modifier = Modifier.width(220.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
            if (showFAB) {
                Row(
                    modifier = Modifier
                        .height(46.dp)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                        .background(White90)
                        .align(Alignment.BottomCenter)
                        .clickable {
                            val selectedSongsList = selectedSongs
                                .filter { it.value }
                                .map { it.key }
                            Log.d("sele",selectedSongsList.toString())
                            viewModel.addSongToPlaylist(selectedSongsList, context)
                            navController.popBackStack()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Add", color = Color.Black)
                }
            }
        }
    }
}