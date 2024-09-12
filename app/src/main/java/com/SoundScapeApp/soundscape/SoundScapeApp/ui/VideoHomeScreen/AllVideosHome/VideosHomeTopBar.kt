package com.SoundScapeApp.soundscape.SoundScapeApp.ui.VideoHomeScreen.AllVideosHome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosHomeTopBar(
    searchValue: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    onPlaylistClear: () -> Unit = {},
    onPlaylistDelete: () -> Unit = {},
    onVideoClear: () -> Unit = {},
    onAddVideo: () -> Unit = {},
    isPlaylistSelected: Boolean,
    isVideoSelected: Boolean,
    isMovieSelected: Boolean,
    onMovieClear: () -> Unit = {},
    onMovieAdd: () -> Unit = {},
    onSelectAllVideos: () -> Unit = {},
    onSelectAllPlaylist: () -> Unit = {},
    onSelectAllMovies: () -> Unit = {},
    onVideoDelete: () -> Unit,
    onVideoShare: () -> Unit,
    selectedVideosCount: MutableState<Int>,
    navController: NavController,
    toggleSearchBar: MutableState<Boolean>
) {


    //Keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(toggleSearchBar.value) {
        if (toggleSearchBar.value) {
            focusRequester.requestFocus()
        }else{
            onValueChange("")
        }
    }


    val showAllPlaylistsDropDown = remember { mutableStateOf(false) }
    val showAllVideosDropDown = remember { mutableStateOf(false) }
    val showAllMoviesDropDown = remember { mutableStateOf(false) }
    val showMoreDropDown = remember { mutableStateOf(false) }


    Box {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    if (!toggleSearchBar.value) {
                        Text(
                            text = "Video Player",
                            color = White90
                        )
                    } else {
                        BasicTextField(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            keyboardActions = KeyboardActions(

                            ),
                            cursorBrush = SolidColor(Color.White),
                            value = searchValue,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = White90,
                                fontSize = 14.sp
                            ),
                            onValueChange = onValueChange,
                            decorationBox = { innerTextField ->

                                Box {
                                    Text(
                                        text = if (searchValue.isEmpty()) placeholderText else "",
                                        color = White90,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .align(Alignment.CenterStart)
                                            .alpha(.5f)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Transparent)
                                            .border(.3.dp, White50, RoundedCornerShape(8.dp))
                                            .padding(start = 8.dp, end = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    )
                                    {
                                        innerTextField(

                                        )

                                        IconButton(onClick = {
                                            onValueChange("")
                                        })
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = null,
                                                tint = White90
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        toggleSearchBar.value = !toggleSearchBar.value
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = White90
                        )
                    }
                }
            },
            actions = {
                if (!toggleSearchBar.value) {
                    IconButton(onClick = {
                        showMoreDropDown.value = true
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90
                        )

                        DropdownMenu(
                            expanded = showMoreDropDown.value,
                            onDismissRequest = {
                                showMoreDropDown.value = false
                            },
                            modifier = Modifier.background(SoundScapeThemes.colorScheme.primary)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Video Settings",
                                        color = White90,
                                        style = SoundScapeThemes.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    showMoreDropDown.value = false
                                    navController.navigate(ScreenRoute.VideoSettings.route)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Themes",
                                        color = White90,
                                        style = SoundScapeThemes.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    showMoreDropDown.value = false
                                    navController.navigate(ScreenRoute.ThemeSettings.route)
                                }
                            )
                        }
                    }
                } else {
                    Text(text = "cancel",
                        color = White90,
                        modifier = Modifier.clickable {
                            toggleSearchBar.value = false
                            onValueChange("")
                        })
                }
            }
        )
        if (isPlaylistSelected) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoundScapeThemes.colorScheme.secondary
                ),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onPlaylistClear)
                    {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onPlaylistDelete)
                    {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }
                    IconButton(onClick = {
                        showAllPlaylistsDropDown.value = !showAllPlaylistsDropDown.value
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    if (showAllPlaylistsDropDown.value) {
                        DropdownMenu(
                            modifier = Modifier.background(SoundScapeThemes.colorScheme.primary),
                            expanded = showAllPlaylistsDropDown.value,
                            onDismissRequest = {
                                showAllPlaylistsDropDown.value = false
                            })
                        {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Select All",
                                        color = White90,
                                        style = SoundScapeThemes.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onSelectAllPlaylist()
                                    showAllPlaylistsDropDown.value = false
                                }
                            )
                        }
                    }
                }
            )
        }
        if (isVideoSelected) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoundScapeThemes.colorScheme.secondary
                ),
                title = {},
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onVideoClear)
                        {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = White90.copy(.9f)
                            )
                        }
                        Text(
                            text = selectedVideosCount.value.toString(),
                            style = SoundScapeThemes.typography.titleLarge,
                            color = White90
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onVideoDelete)
                    {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    IconButton(onClick = onAddVideo)
                    {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    IconButton(onClick = onVideoShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    IconButton(onClick = {
                        showAllVideosDropDown.value = !showAllVideosDropDown.value
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    if (showAllVideosDropDown.value) {
                        DropdownMenu(
                            modifier = Modifier.background(SoundScapeThemes.colorScheme.primary),
                            expanded = showAllVideosDropDown.value,
                            onDismissRequest = {
                                showAllVideosDropDown.value = !showAllVideosDropDown.value
                            })
                        {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Select All",
                                        color = White90,
                                        style = SoundScapeThemes.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onSelectAllVideos()
                                    showAllVideosDropDown.value = false
                                }
                            )
                        }
                    }
                }
            )
        }
        if (isMovieSelected) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoundScapeThemes.colorScheme.secondary
                ),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onMovieClear)
                    {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = White90
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onMovieAdd)
                    {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = White90
                        )
                    }

                    IconButton(onClick = {
                        showAllMoviesDropDown.value = !showAllMoviesDropDown.value
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    if (showAllMoviesDropDown.value) {
                        DropdownMenu(
                            modifier = Modifier.background(SoundScapeThemes.colorScheme.primary),
                            expanded = showAllMoviesDropDown.value,
                            onDismissRequest = {
                                showAllMoviesDropDown.value = !showAllMoviesDropDown.value
                            })
                        {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Select All",
                                        color = White90,
                                        style = SoundScapeThemes.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onSelectAllMovies()
                                    showAllMoviesDropDown.value = false
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}