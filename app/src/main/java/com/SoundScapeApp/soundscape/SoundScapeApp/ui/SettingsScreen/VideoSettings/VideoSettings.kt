package com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.VideoSettings

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.SoundScapeApp.soundscape.ui.theme.White90
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50


@OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSettings(
    navController: NavController,
    viewModel: VideoViewModel
) {
    val showSeekDialog = remember { mutableStateOf(false) }
    val seekSheetState = rememberModalBottomSheetState()
    val currentSeekTime by viewModel.videoSeekTime.collectAsState()

    val showScanVideoLengthDialog = remember { mutableStateOf(false) }
    val scanLengthSheetState = rememberModalBottomSheetState()
    val currentScanLength by viewModel.scanLengthTime.collectAsState()

    val showScanMovieLengthDialog = remember { mutableStateOf(false) }
    val scanMovieLengthSheetState = rememberModalBottomSheetState()
    val currentScanMovieLength by viewModel.scanMovieLengthTime.collectAsState()


    val doubleTapToSeekSwitch by viewModel.doubleTapSeekEnabled.collectAsState()
    val resumeSwitch by viewModel.resumeFromLeftPositionEnabled.collectAsState()
    val continuesPlaySwitch by viewModel.continuesPlayEnabled.collectAsState()
    val autoPopupSwitch by viewModel.autoPopupEnabled.collectAsState()



    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(text = "Video Settings", color = White90)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    })
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = White90
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .padding(start = 12.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Playback",
                    color = Color.Green.copy(.5f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 12.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Doube-tap to seek",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90
                    )
                    Text(
                        text = "Double-tap to fast forward or rewind.",
                        style = SoundScapeThemes.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }
                Switch(
                    modifier = Modifier.scale(.8f),
                    checked = doubleTapToSeekSwitch,
                    onCheckedChange = { viewModel.setDoubleTapSeekEnabled(it) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(
                        enabled = doubleTapToSeekSwitch,
                        onClick = {
                            showSeekDialog.value = true
                        })
                    .padding(start = 12.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Double-tap to seek time",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = if (doubleTapToSeekSwitch) White90 else White50
                    )
                    Text(
                        text = "${(currentSeekTime / 1000)}sec",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 12.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Resume",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90
                    )
                    Text(
                        text = "Resume from point where you stopped.",
                        style = SoundScapeThemes.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }
                Switch(
                    modifier = Modifier.scale(.8f),
                    checked = resumeSwitch,
                    onCheckedChange = { viewModel.setResumeFromLeftPositionEnabled(it) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 12.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Continues play",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90
                    )
                    Text(
                        text = "Play the next video automatically after one ends.",
                        style = SoundScapeThemes.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }
                Switch(
                    modifier = Modifier.scale(.8f),
                    checked = continuesPlaySwitch,
                    onCheckedChange = {
                        viewModel.setContinuesPlayEnabled(it)
                        viewModel.exoPlayer.pauseAtEndOfMediaItems = !it
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .padding(start = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(.7f)
                ) {
                    Text(
                        text = "Auto pop-up play",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90
                    )
                    Text(
                        text = "Continue playing in floating window when you switch to other apps.",
                        style = SoundScapeThemes.typography.bodySmall,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }

                Switch(
                    modifier = Modifier.scale(.8f),
                    checked = autoPopupSwitch,
                    onCheckedChange = { viewModel.setAutoPopupEnabled(it) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        showScanVideoLengthDialog.value = true
                    }
                    .padding(start = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(.7f)
                ) {
                    Text(
                        text = "Scan videos length",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90
                    )
                    Text(
                        text = "Show videos above $currentScanLength seconds.",
                        style = SoundScapeThemes.typography.bodySmall,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        showScanMovieLengthDialog.value = true
                    }
                    .padding(start = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(.7f)
                ) {
                    Text(
                        text = "Scan movies length",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90
                    )
                    Text(
                        text = "Show videos above $currentScanMovieLength minutes as movies.",
                        style = SoundScapeThemes.typography.bodySmall,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp)
//                    .clickable {
//                        showSeekDialog.value = true
//                    }
//                    .padding(start = 16.dp, end = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Seek Time",
//                    color = White90
//                )
//
//                Text(
//                    text ="${(currentSeekTime/1000)}sec",
//                    color = White90
//                )
//            }

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp)
//                    .clickable {
//                        showSeekDialog.value = true
//                    }
//                    .padding(start = 16.dp, end = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Double Tap Seek",
//                    color = White90
//                )
//
//                Switch(
//                    colors = SwitchDefaults.colors(
//                        checkedThumbColor = GrayishGreen,
//                        uncheckedThumbColor = GrayishGreenDark
//                    ),
//                    checked = switchState.value,
//                    thumbContent = {},
//                    onCheckedChange = {switchState.value = it})
//            }
        }
        if (showSeekDialog.value) {
            ModalBottomSheet(
                shape = RoundedCornerShape(4.dp),
                containerColor = SoundScapeThemes.colorScheme.secondary,
                dragHandle = {

                },
                onDismissRequest = {
                    showSeekDialog.value = false
                },
                sheetState = seekSheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setForwardSeekTime(5000L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "5sec",
                            color =
                            if (currentSeekTime == 5000L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setForwardSeekTime(10000L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "10sec",
                            color =
                            if (currentSeekTime == 10000L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(46.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.setForwardSeekTime(15000L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "15sec",
                            color =
                            if (currentSeekTime == 15000L) Color.White else White50
                        )
                    }
                    Row(
                        modifier = Modifier
                            .height(46.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.setForwardSeekTime(20000L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "20sec",
                            color =
                            if (currentSeekTime == 20000L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setForwardSeekTime(25000L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "25sec",
                            color =
                            if (currentSeekTime == 25000L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setForwardSeekTime(30000L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "30sec",
                            color =
                            if (currentSeekTime == 30000L) Color.White else White50
                        )
                    }
                }
            }
        }
        if (showScanVideoLengthDialog.value) {
            ModalBottomSheet(
                shape = RoundedCornerShape(4.dp),
                containerColor = SoundScapeThemes.colorScheme.secondary,
                dragHandle = {

                },
                onDismissRequest = {
                    showScanVideoLengthDialog.value = false
                },
                sheetState = scanLengthSheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(1L)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "Show all",
                            color =
                            if (currentScanLength == 1L) Color.White else White50
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(5L)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "5seconds",
                            color =
                            if (currentScanLength == 5L) Color.White else White50
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(15L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "15seconds",
                            color =
                            if (currentScanLength == 15L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(38.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(30L)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "30seconds",
                            color =
                            if (currentScanLength == 30L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(38.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(60L)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1minute",
                            color =
                            if (currentScanLength == 60L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(90L)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1minute 30sec",
                            color =
                            if (currentScanLength == 90L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(180L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "3minutes",
                            color =
                            if (currentScanLength == 180L) Color.White else White50
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(300L)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "5minutes",
                            color =
                            if (currentScanLength == 300L) Color.White else White50
                        )
                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanVideoLengthTime(600L)

                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "10minutes",
                            color =
                            if (currentScanLength == 600L) Color.White else White50
                        )
                    }
                }
            }
        }

        if (showScanMovieLengthDialog.value) {
            ModalBottomSheet(
                shape = RoundedCornerShape(4.dp),
                containerColor = SoundScapeThemes.colorScheme.secondary,
                dragHandle = {

                },
                onDismissRequest = {
                    showScanMovieLengthDialog.value = false
                },
                sheetState = scanLengthSheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanMovieLengthTime(20L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "20 min",
                            color =
                            if (currentScanMovieLength == 20L) Color.White else White50
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {

                                    viewModel.setScanMovieLengthTime(30L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "30 min",
                            color =
                            if (currentScanMovieLength == 30L) Color.White else White50
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanMovieLengthTime(40L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "40 min",
                            color =
                            if (currentScanMovieLength == 40L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(38.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.setScanMovieLengthTime(50L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "50 min",
                            color =
                            if (currentScanMovieLength == 50L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .height(38.dp)
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    viewModel.setScanMovieLengthTime(60L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1 hr",
                            color =
                            if (currentScanMovieLength == 60L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanMovieLengthTime(80L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1 hr 20 min",
                            color =
                            if (currentScanMovieLength == 80L) Color.White else White50
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanMovieLengthTime(120L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "1 hr 40 min",
                            color =
                            if (currentScanMovieLength == 100L) Color.White else White50
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable(
                                onClick = {
                                    viewModel.setScanMovieLengthTime(120L)
                                }
                            )
                            .padding(start = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            text = "2 hr",
                            color =
                            if (currentScanMovieLength == 120L) Color.White else White50
                        )
                    }
                }
            }
        }
    }
}
