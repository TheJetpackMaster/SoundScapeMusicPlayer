package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.Playlists


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome.AllVideos.formatVideoDuration
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme2Secondary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalGlideComposeApi::class
)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun AddVideosToPlaylist(
    navController: NavController,
    viewModel: VideoViewModel,
) {

//    val selectedSongs by remember { mutableStateOf<MutableSet<Long>>(mutableSetOf()) }
    val selectedVideos = remember { mutableStateMapOf<Long, Boolean>() }

    var showFAB by remember {
        mutableStateOf(false)
    }
    val currentPlayListVideos by viewModel.currentPlaylistVideos.collectAsState()

    val currentPlaylistId = viewModel.currentVideoPlaylistId.collectAsState()

    var searchValue by remember {
        mutableStateOf("")
    }
    val videoList by viewModel.scannedVideoList.collectAsState()

    val filteredVideoList = if (searchValue.isNotBlank()) {
        videoList.filter { video ->
            video.displayName.contains(searchValue, ignoreCase = true)
        }
    } else {
        videoList
    }

    val lazyListState = rememberLazyListState()
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
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.currentBackStackEntry?.lifecycle?.currentState
                            == Lifecycle.State.RESUMED
                        ) {
                            navController.popBackStack()
                        }
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = White90
                        )
                    }
                },
                title = {
                    if (showSearch.value) {
                        Box {
                            Text(
                                text = if (searchValue.isEmpty()) "Search Video" else "",
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
                            text = if (selectedVideos.values.isNotEmpty()) "${selectedVideos.count { it.value }} videos selected"
                            else "Select Videos",
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
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = filteredVideoList.filterNot { video -> video.id in currentPlayListVideos },
                        key = { it.id })
                    { video ->
//                        val isSelected by remember { mutableStateOf(selectedSongs.contains(song.id)) }
                        val isSelected = selectedVideos[video.id] ?: false

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp)
                                .background(
                                    if (isSelected) SoundScapeThemes.colorScheme.primary.copy(.9f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    selectedVideos[video.id] = !(selectedVideos[video.id] ?: false)
                                    showFAB = selectedVideos.any { it.value }
                                }
                                .padding(
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    start = 12.dp,
                                    end = 8.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                GlideImage(
                                    model = video.thumbnail,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(125.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = formatVideoDuration(video.duration.toLong()),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = White90,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(.4f))
                                        .padding(end = 4.dp, start = 4.dp)

                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = video.displayName,
                                    style = SoundScapeThemes.typography.titleSmall,
                                    color = White90,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(.9f)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${video.sizeMB} MB",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = White50,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    VerticalDivider(
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = video.bucketName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = White50,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }

                            if (selectedVideos.any { it.value }) {
                                OutlinedIconButton(
                                    modifier = Modifier.size(20.dp),
                                    border = BorderStroke(1.dp, White90),
                                    onClick = {
                                        selectedVideos[video.id] =
                                            !(selectedVideos[video.id] ?: false)
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
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(74.dp))
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
                            val selectedSongsList = selectedVideos
                                .filter { it.value }
                                .map { it.key }
                            viewModel.addVideoToDifferentPlaylist(currentPlaylistId.value!!,selectedSongsList, context)
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