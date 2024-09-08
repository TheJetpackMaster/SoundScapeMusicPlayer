package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.AllSongs.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.SortType
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListSorting(
    showSort: MutableState<Boolean>,
    viewModel: AudioViewModel,
    currentSortType: SortType,
    onSortClick:()->Unit

) {

    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        containerColor = SoundScapeThemes.colorScheme.secondary,
        dragHandle = {},
        onDismissRequest = {
            showSort.value = false
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 12.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clickable(
                        onClick = {
                            if (currentSortType != SortType.DATE_ADDED_DESC) {
                                showSort.value = false
                                viewModel.sortAudioList(SortType.DATE_ADDED_DESC)
                                viewModel.setSortType(SortType.DATE_ADDED_DESC)
                                viewModel.setMediaItemFlag(false)
                                onSortClick()
                            }
                        }
                    )
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort by date added (Latest)",
                    color =
                    if (currentSortType == SortType.DATE_ADDED_DESC) White50 else White90,
                    style = SoundScapeThemes.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(8.dp))
                if (currentSortType == SortType.DATE_ADDED_DESC) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = White50
                    )
                }

            }
            Row(
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            if (currentSortType != SortType.DATE_ADDED_ASC) {
                                showSort.value = false
                                viewModel.sortAudioList(SortType.DATE_ADDED_ASC)
                                viewModel.setSortType(SortType.DATE_ADDED_ASC)
                                viewModel.setMediaItemFlag(false)
                                onSortClick()
                            }
                        }
                    )
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort by date added (Old)",
                    color =
                    if (currentSortType == SortType.DATE_ADDED_ASC) White50 else White90,
                    style = SoundScapeThemes.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(8.dp))
                if (currentSortType == SortType.DATE_ADDED_ASC) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = White50
                    )
                }

            }
            Row(
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            if (currentSortType != SortType.TITLE_DESC) {
                                showSort.value = false
                                viewModel.sortAudioList(SortType.TITLE_DESC)
                                viewModel.setSortType(SortType.TITLE_DESC)
                                viewModel.setMediaItemFlag(false)
                                onSortClick()
                            }
                        }
                    )
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort by title (Z-A)",
                    color =
                    if (currentSortType == SortType.TITLE_DESC) White50 else White90,
                    style = SoundScapeThemes.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(8.dp))
                if (currentSortType == SortType.TITLE_DESC) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = White50
                    )
                }

            }
            Row(
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            if (currentSortType != SortType.TITLE_ASC) {
                                showSort.value = false
                                viewModel.sortAudioList(SortType.TITLE_ASC)
                                viewModel.setSortType(SortType.TITLE_ASC)
                                viewModel.setMediaItemFlag(false)
                                onSortClick()
                            }
                        }
                    )
                    .padding(start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort by title (A-Z)",
                    color =
                    if (currentSortType == SortType.TITLE_ASC) White50 else White90,
                    style = SoundScapeThemes.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(8.dp))
                if (currentSortType == SortType.TITLE_ASC) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = White50
                    )
                }
            }
        }
    }
}
