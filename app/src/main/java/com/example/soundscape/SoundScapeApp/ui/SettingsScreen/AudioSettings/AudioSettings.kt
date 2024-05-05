package com.example.soundscape.SoundScapeApp.ui.SettingsScreen.AudioSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.example.soundscape.ui.theme.SoundScapeThemes
import com.example.soundscape.ui.theme.Theme2Primary
import com.example.soundscape.ui.theme.Theme2Secondary
import com.example.soundscape.ui.theme.White50
import com.example.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSettings(
    navController: NavController,
    viewModel:AudioViewModel
){
    val currentScanLength by viewModel.scanSongLengthTime.collectAsState()
    val showScanSongsDialog = remember{ mutableStateOf(false) }
    val scanSongsSheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(text = "Audio Settings", color = White90)
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
                    .clickable {
                        showScanSongsDialog.value = true
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
                        text = "Scan songs length",
                        style = SoundScapeThemes.typography.titleSmall,
                        color = White90
                    )
                    Text(
                        text = if(currentScanLength != 0L) "Show songs above $currentScanLength seconds." else "Show all songs.",
                        style = SoundScapeThemes.typography.bodySmall,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        fontWeight = FontWeight.Normal,
                        color = White50
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showScanSongsDialog.value) {
        ModalBottomSheet(
            shape = RoundedCornerShape(4.dp),
            containerColor = SoundScapeThemes.colorScheme.secondary,
            dragHandle = {

            },
            onDismissRequest = {
                showScanSongsDialog.value = false
            },
            sheetState = scanSongsSheetState,
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
                                if(currentScanLength != 0L) {
                                    viewModel.setScanSongLengthTime(0L)
                                }
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
                        if (currentScanLength == 0L) Color.White else White50
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clickable(
                            onClick = {
                                if(currentScanLength != 5L) {
                                    viewModel.setScanSongLengthTime(5L)
                                }
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
                                if(currentScanLength != 10L) {
                                    viewModel.setScanSongLengthTime(10L)
                                }
                            }
                        )
                        .padding(start = 16.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        text = "10seconds",
                        color =
                        if (currentScanLength == 10L) Color.White else White50
                    )

                }
                Row(
                    modifier = Modifier
                        .height(38.dp)
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                if(currentScanLength != 20L) {
                                    viewModel.setScanSongLengthTime(20L)
                                }
                            }
                        )
                        .padding(start = 16.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        text = "20seconds",
                        color =
                        if (currentScanLength == 20L) Color.White else White50
                    )

                }
                Row(
                    modifier = Modifier
                        .height(38.dp)
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                if(currentScanLength != 30L) {
                                    viewModel.setScanSongLengthTime(30L)
                                }
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
                        .fillMaxWidth()
                        .height(38.dp)
                        .clickable(
                            onClick = {
                                if(currentScanLength != 60L) {
                                    viewModel.setScanSongLengthTime(60L)
                                }
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
                                if(currentScanLength != 180L) {
                                    viewModel.setScanSongLengthTime(180L)
                                }
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
            }
        }
    }
}