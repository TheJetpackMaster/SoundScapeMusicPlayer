package com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses


import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class MusicServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val audioSharedPreferencesHelper: AudioSharedPreferencesHelper
) : Player.Listener {

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private val _audioState: MutableStateFlow<AudioState> = MutableStateFlow(AudioState.Initial)
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private var job: Job? = null

    init {
        exoPlayer.addListener(this)
    }


    fun setMediaItemList(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        if (!exoPlayer.isPlaying) {
            exoPlayer.prepare()
        }
    }

    fun setMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun play(mediaItemIndex: Int) {
        exoPlayer.seekToDefaultPosition(mediaItemIndex)
        exoPlayer.play()
        startProgressUpdate()
    }

    private suspend fun playOrPause() {
        withContext(Dispatchers.Main) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                startProgressUpdate()
            } else {
                if (!exoPlayer.playWhenReady) {
                    exoPlayer.prepare()
                    exoPlayer.play()
                } else {
                    exoPlayer.play()
                }
                _audioState.value = AudioState.Playing(
                    isPlaying = true,
                )
                startProgressUpdate()
            }
        }
    }

    private fun stopPlayback() {
        if (!exoPlayer.isPlaying) {
            stopProgressUpdate()
        }
    }

    suspend fun onPlayerEvents(
        playerEvent: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0
    ) {
        when (playerEvent) {
            PlayerEvent.Backward -> exoPlayer.seekBack()
            PlayerEvent.Forward -> exoPlayer.seekForward()
            PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            PlayerEvent.SeekToPrevious -> exoPlayer.seekToPreviousMediaItem()
            PlayerEvent.PlayPause -> playOrPause()
            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            PlayerEvent.SelectedAudioChange -> handleSelectedAudioChange(selectedAudioIndex)
            PlayerEvent.Stop -> stopPlayback()
            is PlayerEvent.UpdateProgress -> updateProgress(playerEvent.newProgress)
            else -> {}
        }
    }

    private fun handleSelectedAudioChange(selectedAudioIndex: Int) {
        when (selectedAudioIndex) {
            exoPlayer.currentMediaItemIndex -> {
                GlobalScope.launch {
                    playOrPause()
                }
            }

            else -> {
                exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                _audioState.value = AudioState.Playing(isPlaying = true)
                exoPlayer.playWhenReady = true
                startProgressUpdate()
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> {
                _audioState.value = AudioState.Buffering(exoPlayer.currentPosition)
                audioSharedPreferencesHelper.savePlaybackState(
                    exoPlayer.currentMediaItem!!.mediaId,
                    exoPlayer.currentPosition,
                    exoPlayer.isPlaying
                )
            }


            ExoPlayer.STATE_READY -> _audioState.value =
                AudioState.Ready(exoPlayer.duration)

            ExoPlayer.STATE_ENDED -> {

            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = AudioState.Playing(isPlaying = isPlaying)

        val mediaIdString = exoPlayer.currentMediaItem?.mediaId
        val mediaIdLong = mediaIdString?.toLongOrNull() ?: -1L

        if (mediaIdString != null) {
            _audioState.value = AudioState.CurrentPlaying(mediaIdLong)
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)

        exoPlayer.currentMediaItem?.let { mediaItem ->
            val mediaIdString = mediaItem.mediaId
            val mediaIdLong = mediaIdString?.toLongOrNull() ?: -1L

            _audioState.value = AudioState.CurrentPlaying(mediaIdLong)

            audioSharedPreferencesHelper.savePlaybackState(
                mediaItem.mediaId,
                exoPlayer.currentPosition,
                exoPlayer.isPlaying
            )
        }
    }




    fun startProgressUpdate() {
        job = coroutineScope.launch {
            while (true) {
                delay(500)
                _audioState.value = AudioState.Progress(exoPlayer.currentPosition)
            }
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
    }

    private fun updateProgress(newProgress: Float) {
        exoPlayer.seekTo((exoPlayer.duration * newProgress.toLong()))
    }


    fun toggleRepeat() {
        when (exoPlayer.repeatMode) {
            ExoPlayer.REPEAT_MODE_OFF -> {
                exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }
            ExoPlayer.REPEAT_MODE_ONE -> {
                exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
            }
            ExoPlayer.REPEAT_MODE_ALL -> {
                exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_OFF
            }
        }
        // Save the updated repeat mode in SharedPreferences
        audioSharedPreferencesHelper.saveRepeatMode(exoPlayer.repeatMode)
    }


}

// Add missing player events for shuffle and repeat
sealed class PlayerEvent {
    object PlayPause : PlayerEvent()
    object SelectedAudioChange : PlayerEvent()
    object Backward : PlayerEvent()
    object SeekToNext : PlayerEvent()
    object SeekToPrevious : PlayerEvent()
    object Forward : PlayerEvent()
    object SeekTo : PlayerEvent()
    object Stop : PlayerEvent()

    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

// Add missing audio state for shuffle and repeat
sealed class AudioState {
    data object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Buffering(val progress: Long) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class CurrentPlaying(val mediaId: Long) : AudioState()
}
