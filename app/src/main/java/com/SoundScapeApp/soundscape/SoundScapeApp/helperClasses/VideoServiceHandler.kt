package com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses


import androidx.media3.common.MediaItem
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


class VideoServiceHandler(
    private val exoPlayer: ExoPlayer
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

    }


    fun onPlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            startProgressUpdate()
            _audioState.value = AudioState.Playing(
                isPlaying = true,
            )
        }
    }

    private fun stopPlayback() {
        if (!exoPlayer.isPlaying) {
            stopProgressUpdate()
        }
    }

    //    Play next Video
    fun playNext() {
        exoPlayer.seekToNext()
    }

    //    Play Previous Video
    fun playPrevious() {
        exoPlayer.seekToPreviousMediaItem()
    }

    fun skipForward(seekForward: Long) {
        val pos = exoPlayer.currentPosition
        exoPlayer.seekTo(pos + seekForward)
    }

    fun skipRewind(seekBackward: Long) {
        val pos = exoPlayer.currentPosition
        exoPlayer.seekTo(pos - seekBackward)
    }

    fun playBackSpeed(playSpeed: Float) {
        exoPlayer.setPlaybackSpeed(playSpeed)
    }

    fun videoVolume(volumeLevel: Float) {
        exoPlayer.volume = volumeLevel
    }

    fun seekToSavedPosition(position: Long) {
        exoPlayer.seekTo(position)
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
            PlayerEvent.PlayPause -> onPlayPause()
            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            PlayerEvent.SelectedAudioChange -> handleSelectedAudioChange(selectedAudioIndex)
            PlayerEvent.Stop -> stopPlayback()
            is PlayerEvent.UpdateProgress -> updateProgress(playerEvent.newProgress)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleSelectedAudioChange(selectedAudioIndex: Int) {
        when (selectedAudioIndex) {
            exoPlayer.currentMediaItemIndex -> {
                GlobalScope.launch {
                    onPlayPause()
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
            ExoPlayer.STATE_BUFFERING -> _audioState.value =
                AudioState.Buffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY -> _audioState.value =
                AudioState.Ready(exoPlayer.duration)

            ExoPlayer.STATE_ENDED -> {

            }
        }
    }

    private fun updateProgress(newProgress: Float) {
        exoPlayer.seekTo((exoPlayer.duration * newProgress.toLong()))
    }

    private fun startProgressUpdate() {
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
}

