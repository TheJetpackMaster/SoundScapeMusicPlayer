package com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.media3.exoplayer.ExoPlayer
import com.SoundScapeApp.soundscape.SoundScapeApp.data.LocalMediaProvider
import com.SoundScapeApp.soundscape.SoundScapeApp.data.MusicRepository
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.EqualizerSharedPreferencesHelper
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.MusicServiceHandler
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.AudioSharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val audioServiceHandler: MusicServiceHandler,
    private val repository: MusicRepository,
    private val player: ExoPlayer,
    private val audioSharedPreferencesHelper: AudioSharedPreferencesHelper,
    private val equalizerSharedPreferencesHelper: EqualizerSharedPreferencesHelper,
    audioStateHandle: SavedStateHandle,
    private val localMediaProvider: LocalMediaProvider

) : ViewModel() {

    //Permission Queue
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    //Theme Track
    private val _currentTheme = MutableStateFlow(1)
    val currentTheme: StateFlow<Int> = _currentTheme

    //Audio playing screen design
    private val _screenDesign = MutableStateFlow(1)
    val screenDesign: StateFlow<Int> = _screenDesign


    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun setTheme(chooseTheme: Int) {
        audioSharedPreferencesHelper.setTheme(chooseTheme)
        _currentTheme.value = chooseTheme
    }

    fun getTheme() {
        _currentTheme.value = audioSharedPreferencesHelper.getTheme()
    }


    fun setScreenDesign(chooseDesign: Int) {
        _screenDesign.value = chooseDesign
    }
}
