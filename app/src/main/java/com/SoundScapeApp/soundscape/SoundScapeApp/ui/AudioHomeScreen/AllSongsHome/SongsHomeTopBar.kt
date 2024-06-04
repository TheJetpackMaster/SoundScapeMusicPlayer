package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome


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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsHomeTopAppBar(
    searchValue: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    onPlaylistClear: () -> Unit = {},
    onPlaylistDelete: () -> Unit = {},
    onSongClear: () -> Unit = {},
    onSelectAllSongs: () -> Unit = {},
    onSelectAllPlaylist: () -> Unit = {},
    onAddSong: () -> Unit = {},
    onSongDelete: () -> Unit,
    isPlaylistSelected: Boolean,
    isSongSelected: Boolean,
    navController: NavController,
    selectedSongsCount: MutableState<Int>
) {

    var search by remember {
        mutableStateOf(false)
    }

    val showAllSongDropDown = remember { mutableStateOf(false) }
    val showMoreDropDown = remember { mutableStateOf(false) }
    val showAllPlaylistDropDown = remember { mutableStateOf(false) }


    Box {
        TopAppBar(
            modifier = Modifier.height(94.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    if (!search) {
                        Text(
                            text = "SoundScape",
                            color = White90,
                            fontSize = 20.sp,

                        )
                    } else {
                        BasicTextField(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth(.85f),
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
                                            search = false
                                            onValueChange("")
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
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "cancel",
                            color = White90,
                            style = SoundScapeThemes.typography.bodyLarge,
                            modifier = Modifier.clickable {
                                search = false
                                onValueChange("")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (!search) {
                        IconButton(onClick = {
                            search = !search
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
            },
            actions = {
                if (!search) {
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
                                        text = "Audio Settings",
                                        color = White90,
                                        style = SoundScapeThemes.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    showMoreDropDown.value = false
                                    if (navController.currentBackStackEntry?.lifecycle?.currentState
                                        == Lifecycle.State.RESUMED
                                    ) {
                                        navController.navigate(ScreenRoute.AudioSettings.route)
                                    }
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
                                    if (navController.currentBackStackEntry?.lifecycle?.currentState
                                        == Lifecycle.State.RESUMED
                                    ) {
                                        navController.navigate(ScreenRoute.ThemeSettings.route)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )
        if (isPlaylistSelected) {
            TopAppBar(
                modifier = Modifier.height(94.dp),
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
                        showAllPlaylistDropDown.value = !showAllPlaylistDropDown.value
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    if (showAllPlaylistDropDown.value) {
                        DropdownMenu(
                            modifier = Modifier.background(SoundScapeThemes.colorScheme.primary),
                            expanded = showAllPlaylistDropDown.value,
                            onDismissRequest = {
                                showAllPlaylistDropDown.value = false
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
                                    showAllPlaylistDropDown.value = false
                                }
                            )
                        }
                    }
                }
            )
        }
        if (isSongSelected) {
            TopAppBar(
                modifier = Modifier.height(94.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoundScapeThemes.colorScheme.secondary
                ),
                title = {},
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onSongClear)
                        {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = White90.copy(.9f)
                            )
                        }
                        Text(
                            text = selectedSongsCount.value.toString(),
                            style = SoundScapeThemes.typography.titleLarge,
                            color = White90
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSongDelete)
                    {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    IconButton(onClick = onAddSong)
                    {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }
                    IconButton(onClick = {
                        showAllSongDropDown.value = !showAllSongDropDown.value
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = White90.copy(.9f)
                        )
                    }

                    if (showAllSongDropDown.value) {
                        DropdownMenu(
                            modifier = Modifier.background(SoundScapeThemes.colorScheme.primary),
                            expanded = showAllSongDropDown.value,
                            onDismissRequest = {
                                showAllSongDropDown.value = !showAllSongDropDown.value
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
                                    onSelectAllSongs()
                                    showAllSongDropDown.value = false
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}