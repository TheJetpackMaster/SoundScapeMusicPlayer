package com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel

import Video
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout
import com.SoundScapeApp.soundscape.SoundScapeApp.data.LocalMediaProvider
import com.SoundScapeApp.soundscape.SoundScapeApp.data.MusicRepository
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.VideoSharedPreferencesHelper
import com.SoundScapeApp.soundscape.SoundScapeApp.data.videoPlaylist
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.AudioState
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.PlayerEvent
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.AudioSharedPreferencesHelper
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.VideoServiceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@OptIn(UnstableApi::class)
@HiltViewModel
class VideoViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val videoPlaylistManager: VideoSharedPreferencesHelper,
    private val audioSharedPreferencesHelper: AudioSharedPreferencesHelper,
    private val localMediaProvider: LocalMediaProvider,
    videoStateHandle: SavedStateHandle,
    context: Context,

    ) : ViewModel() {


    private val _continuesPlayEnabled = MutableStateFlow(false)
    val continuesPlayEnabled: StateFlow<Boolean> = _continuesPlayEnabled

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @UnstableApi
    val trackSelector = DefaultTrackSelector(context)

    @UnstableApi
    val exoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(trackSelector)
            .build()

    private val videoServiceHandler = VideoServiceHandler(exoPlayer)
    var videoMediaSession: MediaSession? = null

    private val _videoList = MutableStateFlow(listOf<Video>())
    val videoList: StateFlow<List<Video>> get() = _videoList

    private val _scannedVideoList = MutableStateFlow(listOf<Video>())
    val scannedVideoList: StateFlow<List<Video>> get() = _scannedVideoList

    // Video Part sections
    private val _currentFolderVideos: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    val currentFolderVideos: StateFlow<List<Long>> = _currentFolderVideos.asStateFlow()

    private val _videoDuration = MutableStateFlow(0L)
    val videoDuration: StateFlow<Long> = _videoDuration

    private val _videoProgress = MutableStateFlow(0f)
    val videoProgress: StateFlow<Float> = _videoProgress


    @kotlin.OptIn(SavedStateHandleSaveableApi::class)
    var duration by videoStateHandle.saveable { mutableStateOf(0L) }

    @kotlin.OptIn(SavedStateHandleSaveableApi::class)
    var progress by videoStateHandle.saveable { mutableStateOf(0f) }


    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
//    private var progressString by audioStateHandle.saveable { mutableStateOf("00:00") }

    //    PipMode Track
    private val _isPipModeEnabled = MutableStateFlow(false)
    val isPipModeEnabled: StateFlow<Boolean> = _isPipModeEnabled


    // video Playback Speed
    private val _currentPlaybackSpeed = MutableStateFlow(1f)
    val currentPlaybackSpeed: StateFlow<Float> = _currentPlaybackSpeed

    private val _availableTracks = MutableStateFlow<List<TrackInfo>>(emptyList())
    val availableTracks: StateFlow<List<TrackInfo>> = _availableTracks

    private val _availableSubtitles = MutableStateFlow<List<TrackInfo>>(emptyList())
    val availableSubtitles: StateFlow<List<TrackInfo>> = _availableSubtitles


    private val _originalLanguageTracks = MutableStateFlow<List<String>>(emptyList())
    val originalLanguageTracks: StateFlow<List<String>> = _originalLanguageTracks

    private val _originalSubtitles = MutableStateFlow<List<String>>(emptyList())
    val originalSubtitles: StateFlow<List<String>> = _originalSubtitles

    //   current Video Playlist
    private val _currentVideoPlaylistId = MutableStateFlow<Long?>(null)
    val currentVideoPlaylistId: StateFlow<Long?> = _currentVideoPlaylistId

    //    Video playlist items
    private val _currentPlaylistVideos = MutableStateFlow<List<Long>>(emptyList())
    val currentPlaylistVideos: StateFlow<List<Long>> = _currentPlaylistVideos

    //    Video playlists
    private val _videoPlaylists: MutableStateFlow<List<videoPlaylist>> =
        MutableStateFlow(emptyList())
    val videoPlaylists: StateFlow<List<videoPlaylist>> get() = _videoPlaylists

    // Playlist videos
//    private val _playListVideosList: MutableStateFlow<List<Video>> = MutableStateFlow(emptyList())
//    private val playListVideosList: StateFlow<List<Video>> = _playListVideosList.asStateFlow()


    // Current video playlist Name
    private val _currentVideoPlaylistName = MutableStateFlow<String?>(null)
    val currentVideoPlaylistName: StateFlow<String?> = _currentVideoPlaylistName


    //    Current created video playlist
    private val _currentCreatedVideoPlaylistId = MutableStateFlow(0L)
    private val currentCreatedVideoPlaylistId: StateFlow<Long?> = _currentCreatedVideoPlaylistId

    //    current Video sort Type
    var currentVideoSortType: VideoSortType = getVideoSortType()


    //    Searching video
    private val _isVideoSearch = MutableStateFlow(false)
    val isVideoSearch: StateFlow<Boolean> = _isVideoSearch


    // PlaybackPosition saved
    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition

    @UnstableApi
    private val _playerState = MutableStateFlow(PlayerState())

    @UnstableApi
    val playerState = _playerState.asStateFlow()


    private val _videoSeekTime = MutableStateFlow(0L)
    val videoSeekTime: StateFlow<Long> = _videoSeekTime

    private val _scanLengthTime = MutableStateFlow(0L)
    val scanLengthTime: StateFlow<Long> = _scanLengthTime

    private val _scanMovieLengthTime = MutableStateFlow(0L)
    val scanMovieLengthTime: StateFlow<Long> = _scanMovieLengthTime

    private val _doubleTapSeekEnabled = MutableStateFlow(false)
    val doubleTapSeekEnabled: StateFlow<Boolean> = _doubleTapSeekEnabled

    private val _resumeFromLeftPositionEnabled = MutableStateFlow(false)
    val resumeFromLeftPositionEnabled: StateFlow<Boolean> = _resumeFromLeftPositionEnabled

//    private val _continuesPlayEnabled = MutableStateFlow(false)
//    val continuesPlayEnabled: StateFlow<Boolean> = _continuesPlayEnabled

    private val _autoPopupEnabled = MutableStateFlow(false)
    val autoPopupEnabled: StateFlow<Boolean> = _autoPopupEnabled


    //SELECTIONS
    private val _isPlaylistSelected = MutableStateFlow(false)
    val isPlaylistSelected: StateFlow<Boolean> = _isPlaylistSelected


    private val _isVideoSelected = MutableStateFlow(false)
    val isVideoSelected: StateFlow<Boolean> = _isVideoSelected

    private val _isMovieSelected = MutableStateFlow(false)
    val isMovieSelected: StateFlow<Boolean> = _isMovieSelected


    // SELECTED FOR DELeTION
    private val _selectedVideos = MutableStateFlow<List<Long>>(emptyList())
    val selectedVideos: StateFlow<List<Long>> = _selectedVideos


    // SELECTED FOR SetTimer
    private var timer: CountDownTimer? = null

    private val _currentTheme = MutableStateFlow(2)
    val currentTheme: StateFlow<Int> = _currentTheme

    private val _videoScreenBrightness = MutableStateFlow(0f)
    val videoScreenBrightness: StateFlow<Float> = _videoScreenBrightness

    init {
        loadVideoPlaylists()
        getSeekForwardTime()
        getDoubleTapSeekEnabled()
        getResumeFromLeftPositionEnabled()
        getContinuesPlayEnabled()
        getAutoPopupEnabled()
        getScanVideoLengthTime()
        exoPlayer.pauseAtEndOfMediaItems = !continuesPlayEnabled.value
        getScanMovieLengthTime()
        createVideoMediaSession(context)
        getTheme()
    }

    init {
        viewModelScope.launch {
            videoServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    AudioState.Initial -> {}
                    is AudioState.Buffering -> calculateProgressValue(mediaState.progress)
                    is AudioState.Playing -> _isPlaying.value = exoPlayer.isPlaying
                    is AudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is AudioState.CurrentPlaying -> {
//                        currentSelectedAudio = player.currentMediaItem?.mediaId!!.toLong()

                    }

                    is AudioState.Ready -> {
                        _videoDuration.value = mediaState.duration
                        duration = mediaState.duration
                    }
                }
            }
        }
        Log.d("intilized", "initilized")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(UnstableApi::class)
    fun loadVideoData() {
        viewModelScope.launch {
            if (videoList.value.isEmpty()) {
                val video = repository.getVideoData()
                val videos = video.filter {
                    it.duration > (scanLengthTime.value * 1000)
                }
                _videoList.value = videos
                sortVideoList(currentVideoSortType)
            }
            val length = scanLengthTime.value * 1000L
            loadScannedVideosList(length)
            removeExtraPlaybackPositions(videoList.value)
        }
    }

    fun loadScannedVideosList(
        scanLength: Long
    ) {
        if (videoList.value.isNotEmpty()) {
            val videos = videoList.value.filter {
                it.duration > scanLength
            }
            _scannedVideoList.value = videos
            sortVideoList(currentVideoSortType)
        }
        Log.d("videos", scannedVideoList.value.toString())
    }

    fun createVideoMediaSession(context: Context) {
        videoMediaSession = MediaSession.Builder(context, exoPlayer)
            .setId("videoSession")
            .build()
    }

    fun destroyVideoMediaSession() {
        videoMediaSession?.release()
        videoMediaSession = null
    }

    //    Set videos list to play
    fun setVideoMediaItems(videoList: List<Video>) {
        if (videoList.isNotEmpty()) {
            val mediaItems = videoList.map { video ->
                MediaItem.Builder()
                    .setUri(video.uri)
                    .setMediaId(video.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setDisplayTitle(video.displayName)
                            .build()
                    )
                    .build()
            }
            videoServiceHandler.setMediaItemList(mediaItems)

        }
    }

    fun setVideoMediaItemAndPlay(videoItem: Video) {
        val mediaItem = MediaItem.Builder()
            .setUri(videoItem.uri)
            .setMediaId(videoItem.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(videoItem.displayName)
                    .build()
            )
            .build()
        videoServiceHandler.setMediaItem(mediaItem)
    }


    //    Play video
    fun playVideo(videoIndex: Int) {
        videoServiceHandler.play(videoIndex)
        _playerState.update {
            it.copy(
              isPlaying = exoPlayer.isPlaying
            )
        }
    }

    //    Toggle video playPause
    fun onPlayPause() {
        if(!exoPlayer.isPlaying){
            _playerState.update {
                it.copy(
                    isPlaying = exoPlayer.isPlaying
                )
            }
        }
        videoServiceHandler.onPlayPause()
    }

    fun playNext() {
        videoServiceHandler.playNext()
        if (resumeFromLeftPositionEnabled.value) {
            val savedMedia = getPlaybackPosition(exoPlayer.currentMediaItem!!.mediaId)
            seekToSavedPosition(savedMedia)
        }

    }

    fun playPrevious() {
        videoServiceHandler.playPrevious()
        if (resumeFromLeftPositionEnabled.value) {
            val savedMedia = getPlaybackPosition(exoPlayer.currentMediaItem!!.mediaId)
            seekToSavedPosition(savedMedia)
        }
    }

    fun skipForward() {
        videoServiceHandler.skipForward(
            videoSeekTime.value
        )
    }

    fun skipRewind() {
        videoServiceHandler.skipRewind(
            videoSeekTime.value
        )
    }

    fun playBackSpeed(playSpeed: Float) {
        videoServiceHandler.playBackSpeed(playSpeed)
        _currentPlaybackSpeed.value = playSpeed
    }

//    fun getPlaybackSpeed():Float{
//        return sharedPreferencesHelper.getPlaybackSpeed()
//    }

    fun videoVolume(volumeLevel: Float) {
        videoServiceHandler.videoVolume(volumeLevel)
    }

    fun setPipModeEnabled(pipMode: Boolean) {
        _isPipModeEnabled.value = pipMode
    }


    private fun calculateProgressValue(currentProgress: Long) {
        _videoProgress.value =
            if (currentProgress > 0) ((currentProgress.toFloat()) / videoDuration.value.toFloat()) * 100f
            else 0f
//        progressString = formatDuration(currentProgress)
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat()) / videoDuration.value.toFloat()) * 100f
            else 0f
    }

    fun onUiEvents(uiEvents: UIEvents) = viewModelScope.launch {
        when (uiEvents) {
            UIEvents.Backward -> {}
            UIEvents.Forward -> {}
            UIEvents.SeekPrevious -> {}
            UIEvents.SeekToNext -> {}
            is UIEvents.PlayPause -> {}
            is UIEvents.SeekTo -> {
                videoServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = ((videoDuration.value * uiEvents.position) / 100f).toLong()
                )
            }

            is UIEvents.PlaySelectedAudio -> {
                viewModelScope.launch {
                    videoServiceHandler.play(uiEvents.index)
                }
            }

            is UIEvents.SelectedAudioChange -> {
            }

            is UIEvents.UpdateProgress -> {
                videoServiceHandler.onPlayerEvents(
                    PlayerEvent.UpdateProgress(
                        uiEvents.newProgress
                    )
                )
                _videoProgress.value = uiEvents.newProgress
                progress = uiEvents.newProgress
            }
        }
    }

    fun onProgressSeek(newProgress: Float) {
        val totalDuration = exoPlayer.duration.toFloat()
        if (totalDuration > 0) {
            val targetPosition = (newProgress / 100f * totalDuration).toLong()
            exoPlayer.seekTo(targetPosition)
        }
        progress = exoPlayer.currentPosition.toFloat()
    }


    //SELECTION SECTION
    fun setIsPlaylistSelected(playlistSelected: Boolean) {
        _isPlaylistSelected.value = playlistSelected
    }

    fun setIsVideoSelected(songSelected: Boolean) {
        _isVideoSelected.value = songSelected
    }

    fun isMovieSelected(movieSelected: Boolean) {
        _isMovieSelected.value = movieSelected
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    fun getAvailableTracks(): List<TrackInfo> {
        val tracks = mutableListOf<TrackInfo>()
        val originalTracks = mutableListOf<String>()
        val mapped = trackSelector.currentMappedTrackInfo
        if (mapped != null) {
            val uniqueLang = HashSet<String>()
            for (i in 0 until mapped.rendererCount) {
                val trackGroups = mapped.getTrackGroups(i)
                for (j in 0 until trackGroups.length) {
                    val trackGroup = trackGroups.get(j)
                    for (k in 0 until trackGroup.length) {
                        val format = trackGroup.getFormat(k)
                        val language = format.language.toString()
                        if (mapped.getRendererType(i) == C.TRACK_TYPE_AUDIO && language.isNotBlank() && uniqueLang.add(
                                language
                            )
                        ) {
                            val formatted = Locale(language).displayLanguage
                            tracks.add(TrackInfo(formatted, format))
                            originalTracks.add(language)
                        }
                    }
                }
            }
        }
        _originalLanguageTracks.value = originalTracks
        return tracks.distinctBy { it.language }
    }

    fun updateAvailableTracks() {
        viewModelScope.launch {
            val tracks = getAvailableTracks()
            _availableTracks.value = tracks
        }
    }

    @OptIn(UnstableApi::class)
    fun getAvailableSubtitles(): List<TrackInfo> {
        val subtitles = mutableListOf<TrackInfo>()
        val originalSubtitles = mutableListOf<String>()
        val mappedSubtitles = trackSelector.currentMappedTrackInfo
        if (mappedSubtitles != null) {
            val uniqueSubtitles = HashSet<String>()
            for (i in 0 until mappedSubtitles.rendererCount) {
                val trackGroups = mappedSubtitles.getTrackGroups(i)
                for (j in 0 until trackGroups.length) {
                    val trackGroup = trackGroups.get(j)
                    for (k in 0 until trackGroup.length) {
                        val format = trackGroup.getFormat(k)
                        val language = format.language.toString()
                        if (mappedSubtitles.getRendererType(i) == C.TRACK_TYPE_TEXT && language.isNotBlank() && uniqueSubtitles.add(
                                language
                            )
                        ) {
                            val formatted = Locale(language).displayLanguage
                            subtitles.add(TrackInfo(formatted, format))
                            originalSubtitles.add(language)
                            Log.d("languagesubs", language)
                        }
                    }
                }
            }
        }
        _originalSubtitles.value = originalSubtitles
        return subtitles.distinctBy { it.language }
    }

    fun updateAvailableSubtitles() {
        viewModelScope.launch {
            val subtitles = getAvailableSubtitles()
            Log.d("subtitlesSec", subtitles.toString())
            _availableSubtitles.value = subtitles
        }
    }

    //  Screen Rotation
    @androidx.annotation.OptIn(UnstableApi::class)
    fun onRotateScreen() {
//        val orientation =
//            if (_playerState.value.orientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE) {
//                ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
//            } else {
//                ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
//            }
//        _playerState.update {
//            it.copy(orientation = orientation)
//        }
    }

    // Resize Screen
    @androidx.annotation.OptIn(UnstableApi::class)
    fun onResizeClick() {
        _playerState.update {
            it.copy(
                resizeMode = when (_playerState.value.resizeMode) {
                    AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                        AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }

                    AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }

                    else -> {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                }
            )
        }
    }

    fun resetDefaultResizeMode(){
        _playerState.update {
            it.copy(
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            )
        }
    }

    //    Load videos for folders
    fun loadVideosForFolder(bucketName: String) {
        viewModelScope.launch {
            val videosForFolderIds =
                videoList.value.filter { it.bucketName == bucketName }.map { it.id }
            _currentFolderVideos.value = videosForFolderIds
        }
    }


    //    VIDEO PLAYLIST SECTION
//    PLAYLISTS LOGICS
    private fun loadVideoPlaylists() {
        _videoPlaylists.value = videoPlaylistManager.getVideoPlaylists()
    }

    //    Create a  playlist
    fun saveNewVideoPlaylist(newPlaylistName: String, videoIds: List<Long> = emptyList()) {
        val existingPlaylists = _videoPlaylists.value.toMutableList()
        val newPlaylist = videoPlaylist(
            id = System.currentTimeMillis(),
            name = newPlaylistName,
            videoIds = videoIds
        )
        existingPlaylists.add(newPlaylist)

        _currentCreatedVideoPlaylistId.value = newPlaylist.id

        videoPlaylistManager.saveVideoPlaylists(existingPlaylists)

        _videoPlaylists.value = existingPlaylists
    }
//
//    fun addVideoToPlaylist(videoIds: List<Long>, context: Context) {
//        val currentVideoPlaylistId = _currentVideoPlaylistId.value
//        if (currentVideoPlaylistId != null) {
//            // Update the playlist in SharedPreferencesHelper
//            sharedPreferencesHelper.addSongsToPlaylist(currentVideoPlaylistId, videoIds, context)
//
//            // Update the current playlist songs
//            val currentPlaylistSongs =
//                videoPlaylistManager.getVideosByPlaylistId(currentVideoPlaylistId)
//            _currentPlaylistSongs.value = currentPlaylistSongs
//
//            // Update the count of songs in the playlist in the ViewModel
//            val updatedPlaylist = _playlists.value.find { it.id == currentVideoPlaylistId }
//            updatedPlaylist?.let {
//                val existingSongIds = it.songIds
//                val updatedSongIds = existingSongIds.toMutableList().apply {
//                    addAll(videoIds.filter { videoId -> !contains(videoId) })
//                }
//                _videoPlaylists.value = _videoPlaylists.value.map { playlist ->
//                    if (playlist.id == currentVideoPlaylistId) {
//                        videoPlaylist(playlist.id, playlist.name, updatedSongIds)
//                    } else {
//                        playlist
//                    }
//                }
//            }
//        }
//    }

    fun addVideoToDifferentPlaylist(playlistId: Long, songIds: List<Long>, context: Context) {
        videoPlaylistManager.addVideosToPlaylist(playlistId, songIds, context = context)

        val updatedPlaylist = _videoPlaylists.value.find { it.id == playlistId }
        updatedPlaylist?.let {
            val existingVideoIds = it.videoIds
            val updatedSongIds = existingVideoIds.toMutableList().apply {
                addAll(songIds.filter { songId -> !contains(songId) })
            }
            _videoPlaylists.value = _videoPlaylists.value.map { playlist ->
                if (playlist.id == playlistId) {
                    videoPlaylist(playlist.id, playlist.name, updatedSongIds)
                } else {
                    playlist
                }
            }
        }

        viewModelScope.launch {
            val currentPlaylistVideos =
                videoPlaylistManager.getVideosByPlaylistId(playlistId)
            _currentPlaylistVideos.value = currentPlaylistVideos
        }
    }

    //  Clicked Playlist
    fun onVideoPlaylistClicked(playlistId: Long, playListName: String) {
        _currentVideoPlaylistId.value = playlistId
        _currentVideoPlaylistName.value = playListName
        loadVideosForCurrentPlaylist(playlistId)
    }

    fun loadVideosForCurrentPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val videos = videoPlaylistManager.getVideosByPlaylistId(playlistId = playlistId)
            _currentPlaylistVideos.value = videos
        }
    }

//    fun loadPlaylistVideoData() {
//        viewModelScope.launch {
//            val videos = videoList.value.filter { it.id in currentPlaylistVideos.value }
//            if (playListVideosList.value.isEmpty()) {
//                if (videos.isNotEmpty()) {
//                    _playListVideosList.value = videos
//                    Log.d("songs added", "added")
//                } else {
//                    Log.d("songs added", "empty or not found")
//                }
//            }
//        }
//    }

    fun deleteVideoPlaylist(playlistId: Long) {
        videoPlaylistManager.deleteVideoPlaylist(playlistId)
        if (_currentVideoPlaylistId.value == playlistId) {
            _currentVideoPlaylistId.value = null
            _currentPlaylistVideos.value = emptyList()
        }

        viewModelScope.launch {
            _videoPlaylists.value = videoPlaylistManager.getVideoPlaylists().toMutableList()

        }
    }

    fun createAndAddVideoToPlaylist(videoIds: List<Long>, context: Context) {
        val playlistId = currentCreatedVideoPlaylistId.value
        Log.d("currentplaylist", currentCreatedVideoPlaylistId.value.toString())
        videoPlaylistManager.addVideosToPlaylist(playlistId!!, videoIds, context = context)

//        // Update the current playlist songs
//        val currentPlaylistSongs =
//            sharedPreferencesHelper.getSongsByPlaylistId(playlistId)
//        _currentPlaylistSongs.value = currentPlaylistSongs

        // Update the count of songs in the playlist in the ViewModel
        val updatedPlaylist = _videoPlaylists.value.find { it.id == playlistId }
        updatedPlaylist?.let {
            val existingVideoIds = it.videoIds
            val updatedVideoIds = existingVideoIds.toMutableList().apply {
                addAll(videoIds.filter { videoId -> !contains(videoId) })
            }
            _videoPlaylists.value = _videoPlaylists.value.map { playlist ->
                if (playlist.id == playlistId) {
                    videoPlaylist(playlist.id, playlist.name, updatedVideoIds)
                } else {
                    playlist
                }
            }
        }
    }

    fun deleteVideoPlaylists(playlistIds: List<Long>) {
        videoPlaylistManager.deleteVideoPlaylists(playlistIds)
        val deletedCurrentPlaylist = _currentVideoPlaylistId.value in playlistIds

        if (deletedCurrentPlaylist) {
            _currentVideoPlaylistId.value = null
            _currentPlaylistVideos.value = emptyList()
        }

        viewModelScope.launch {
            _videoPlaylists.value = videoPlaylistManager.getVideoPlaylists().toMutableList()
        }
    }

//    fun deletePlaylists(playlistIds: List<Long>) {
//        sharedPreferencesHelper.deletePlaylists(playlistIds)
//        val deletedCurrentPlaylist = _currentPlaylistId.value in playlistIds
//
//        if (deletedCurrentPlaylist) {
//            _currentPlaylistId.value = null
//            _currentPlaylistSongs.value = emptyList()
//        }
//
//        viewModelScope.launch {
//            _playlists.value = sharedPreferencesHelper.getPlaylists().toMutableList()
//        }
//    }

    fun deleteVideoFromPlaylist(videoIds: List<Long>) {
        val playlistId = currentVideoPlaylistId.value ?: return

        // Delete the songs from the shared preferences using the helper function
        videoPlaylistManager.deleteVideosFromPlaylist(playlistId, videoIds)

        // Update the UI or any other necessary operations in the ViewModel
        val updatedCurrentPlaylistVideos = _currentPlaylistVideos.value.toMutableList()
        updatedCurrentPlaylistVideos.removeAll(videoIds)
        _currentPlaylistVideos.value = updatedCurrentPlaylistVideos.toList()

        // Update the count of songs in the playlist
        val updatedPlaylists = _videoPlaylists.value.map { playlist ->
            if (playlist.id == playlistId) {
                val updatedVideoIds = playlist.videoIds.toMutableList()
                updatedVideoIds.removeAll(videoIds)
                videoPlaylist(playlist.id, playlist.name, updatedVideoIds.toList())
            } else {
                playlist
            }
        }
        _videoPlaylists.value = updatedPlaylists
    }

    fun deleteVideosFromPlaylist(videoIds: List<Long>, playlistId: Long) {
        videoPlaylistManager.deleteVideosFromPlaylist(playlistId, videoIds)

        // Update the UI or any other necessary operations in the ViewModel
        val updatedCurrentPlaylistVideos = _currentPlaylistVideos.value.toMutableList()
        updatedCurrentPlaylistVideos.removeAll(videoIds)
        _currentPlaylistVideos.value = updatedCurrentPlaylistVideos.toList()

        // Update the count of songs in the playlist
        val updatedPlaylists = _videoPlaylists.value.map { playlist ->
            if (playlist.id == playlistId) {
                val updatedVideoIds = playlist.videoIds.toMutableList()
                updatedVideoIds.removeAll(videoIds)
                videoPlaylist(playlist.id, playlist.name, updatedVideoIds.toList())
            } else {
                playlist
            }
        }
        _videoPlaylists.value = updatedPlaylists
    }

    //    Clear Whole PlayList
    fun clearVideoPlaylist(playlistId: Long) {
        videoPlaylistManager.clearVideosFromPlaylist(playlistId)
        val updatedCurrentPlaylistVideos = emptyList<Long>()
        _currentPlaylistVideos.value = updatedCurrentPlaylistVideos

        val updatedPlaylist = _videoPlaylists.value.find { it.id == playlistId }
        updatedPlaylist?.let {
            _videoPlaylists.value = _videoPlaylists.value.map { playlist ->
                if (playlist.id == playlistId) {
                    videoPlaylist(playlist.id, playlist.name, emptyList())
                } else {
                    playlist
                }
            }
        }
    }

    fun editVideoPlaylistName(playlistId: Long, newName: String) {
        videoPlaylistManager.editVideoPlaylistName(playlistId, newName)

        val updatedPlaylists = _videoPlaylists.value.map { playlist ->
            if (playlist.id == playlistId) {
                videoPlaylist(playlist.id, newName, playlist.videoIds)
            } else {
                playlist
            }
        }
        _videoPlaylists.value = updatedPlaylists
    }


    fun getVideoImageUrl(songId: Long): String? {
        val video = videoList.value.firstOrNull { it.id == songId }

        return video?.thumbnail
    }

    fun setVideoSortType(sortType: VideoSortType) {
        videoPlaylistManager.setCurrentVideoSortType(sortType)
    }

    private fun getVideoSortType(): VideoSortType {
        return videoPlaylistManager.getCurrentVideoSortType()
    }

    fun sortVideoList(sortType: VideoSortType) {
        currentVideoSortType = sortType
        videoPlaylistManager.setCurrentVideoSortType(sortType)

        val sortedList = when (sortType) {
            VideoSortType.DATE_ADDED_ASC -> scannedVideoList.value.sortedBy { it.dateAdded }
            VideoSortType.DATE_ADDED_DESC -> scannedVideoList.value.sortedByDescending { it.dateAdded }
            VideoSortType.TITLE_ASC -> scannedVideoList.value.sortedBy { it.displayName }
            VideoSortType.TITLE_DESC -> scannedVideoList.value.sortedByDescending { it.displayName }
            VideoSortType.SIZE_DESC -> scannedVideoList.value.sortedByDescending { it.sizeMB }
            VideoSortType.SIZE_ASC -> scannedVideoList.value.sortedBy { it.sizeMB }
        }
        _scannedVideoList.value = sortedList
    }

    fun isAppVideoSearch(isSearch: Boolean) {
        _isVideoSearch.value = isSearch
    }

    fun savePlaybackPosition(context: Context, videoId: String, position: Long) {
        videoPlaylistManager.savePlaybackPosition(context, videoId, position)
    }


    fun getPlaybackPosition(videoId: String): Long {
//        _playbackPosition.value = videoPlaylistManager.getPlaybackPosition(context,videoId)
        return videoPlaylistManager.getPlaybackPosition(videoId)
    }


    fun removeSavedPlayback(videoid: String) {
        videoPlaylistManager.removePlayedVideo(videoid)
    }


    fun seekToSavedPosition(position: Long) {
        videoServiceHandler.seekToSavedPosition(position)
    }


    //    Video Seekbar
    fun onVideoProgressSeek(progress: Float) {
        val totalDuration = exoPlayer.duration.toFloat()
        if (totalDuration > 0) {
            val targetPosition = (progress / 100f * totalDuration).toLong()
            exoPlayer.seekTo(targetPosition)
        }
    }

    fun removeExtraPlaybackPositions(videoList: List<Video>) {
        viewModelScope.launch {
            videoPlaylistManager.removePositionsForMissingVideos(videoList)
        }
    }


    //    Video Settings Controll
    @OptIn(UnstableApi::class)
    fun setForwardSeekTime(forwardSeekTime: Long) {
        videoPlaylistManager.setSeekForwardTime(forwardSeekTime)
        _videoSeekTime.value = forwardSeekTime
    }

    private fun getSeekForwardTime() {
        _videoSeekTime.value = videoPlaylistManager.getSeekForwardTime()
    }

    fun setScanVideoLengthTime(scanLength: Long) {
        videoPlaylistManager.setScanVideoLengthTime(scanLength)
        _scanLengthTime.value = scanLength

        val length = scanLength * 1000
        loadScannedVideosList(length)
    }

    fun getScanVideoLengthTime() {
        _scanLengthTime.value = videoPlaylistManager.getScanVideoLengthTime()
    }

    fun setDoubleTapSeekEnabled(enabled: Boolean) {
        videoPlaylistManager.setDoubleTapSeekEnabled(enabled)
        _doubleTapSeekEnabled.value = enabled
    }

    fun setScanMovieLengthTime(scanLength: Long) {
        videoPlaylistManager.setScanMovieLengthTime(scanLength)
        _scanMovieLengthTime.value = scanLength
    }

    fun getScanMovieLengthTime() {
        _scanMovieLengthTime.value = videoPlaylistManager.getScanMovieLengthTime()
    }

    private fun getDoubleTapSeekEnabled() {
        _doubleTapSeekEnabled.value = videoPlaylistManager.getDoubleTapSeekEnabled()
    }

    fun setResumeFromLeftPositionEnabled(enabled: Boolean) {
        videoPlaylistManager.setResumeFromLeftPositionEnabled(enabled)
        _resumeFromLeftPositionEnabled.value = enabled
    }

    private fun getResumeFromLeftPositionEnabled() {
        _resumeFromLeftPositionEnabled.value =
            videoPlaylistManager.getResumeFromLeftPositionEnabled()
    }

    fun setContinuesPlayEnabled(enabled: Boolean) {
        videoPlaylistManager.setContinuesPlayEnabled(enabled)
        _continuesPlayEnabled.value = enabled
    }

    private fun getContinuesPlayEnabled() {
        _continuesPlayEnabled.value = videoPlaylistManager.getContinuesPlayEnabled()
    }

    fun setAutoPopupEnabled(enabled: Boolean) {
        videoPlaylistManager.setAutoPopupEnabled(enabled)
        _autoPopupEnabled.value = enabled
    }

    private fun getAutoPopupEnabled() {
        _autoPopupEnabled.value = videoPlaylistManager.getAutoPopupEnabled()
    }

    fun toggleVideoVolume(isMuted: MutableState<Boolean>) {
        if (isMuted.value) {
            exoPlayer.volume = 0f
        } else {
            exoPlayer.volume = 1f
        }
    }


    fun CurrentVideoLooping(isVideoLooping: Boolean, exoPlayer: ExoPlayer) {
        exoPlayer.repeatMode =
            if (isVideoLooping) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
    }


    fun startTimer(duration: Long, onFinish: () -> Unit, exoPlayer: ExoPlayer) {
        timer?.cancel() // Cancel any existing timer

        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update UI if necessary with the remaining time
            }

            override fun onFinish() {
                onFinish()
                exoPlayer.pause()
            }
        }.start()
    }


    fun setBrightness(brightness:Float){
        _videoScreenBrightness.value = brightness
    }



    fun setSelectedVideos(selectedVideos: List<Long>) {
        _selectedVideos.value = selectedVideos
    }


    fun getTheme() {
        _currentTheme.value = audioSharedPreferencesHelper.getTheme()
    }

    // DELETION
    fun reloadVideos(selectedVideoIds: List<Long>) {
        viewModelScope.launch {
            val filteredSongs = scannedVideoList.value.filter { song ->
                song.id !in selectedVideoIds // Filter out songs whose IDs are in selectedSongIds
            }
            _videoList.value = filteredSongs
            _scannedVideoList.value = filteredSongs // Update all songs list

            if (currentVideoPlaylistId.value != null) {
                loadVideosForCurrentPlaylist(currentVideoPlaylistId.value!!)
            }
        }
    }



    fun shareVideo(context: Context, videoUri: Uri, videoTitle: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "video/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, videoTitle)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Share video via"))
    }

    fun shareVideos(context: Context, videoUris: List<Uri>, videoTitles: List<String>) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.type = "video/*"

        val uriArrayList = ArrayList<Uri>(videoUris)
        val titleArrayList = ArrayList<String>(videoTitles)

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList)
        shareIntent.putStringArrayListExtra(Intent.EXTRA_SUBJECT, titleArrayList)

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(Intent.createChooser(shareIntent, "Share videos via"))
    }




    // Playing from Intent
    private fun setMediaItem(uri: Uri, displayName: String) {
        exoPlayer.apply {
            addMediaItem(
                MediaItem.Builder()
                    .setUri(uri)
                    .setMediaMetadata(MediaMetadata.Builder().setDisplayTitle(displayName).build())
                    .build()
            )
            prepare()
            play()
        }
    }

    private fun updateCurrentVideoItem(videoItem: Video) {
        _playerState.update {
            it.copy(
                currentVideoItem = videoItem
            )
        }
        setMediaItem(_playerState.value.currentVideoItem!!.uri.toUri(),playerState.value.currentVideoItem!!.displayName)
    }

    fun onIntent(uri: Uri) {
        localMediaProvider.getVideoItemFromContentUri(uri)?.let {
            updateCurrentVideoItem(it)
        }
    }

    fun onNewIntent(uri: Uri) {
        exoPlayer.clearMediaItems()
        localMediaProvider.getVideoItemFromContentUri(uri)?.let {
            updateCurrentVideoItem(it)
        }
    }



    override fun onCleared() {
        super.onCleared()
        destroyVideoMediaSession()
        exoPlayer.release()
    }
}

@UnstableApi
data class PlayerState(
    val isPlaying: Boolean = false,
    val currentVideoItem: Video? = null,
    val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
)

data class TrackInfo(
    val language: String,
    val format: Format
)