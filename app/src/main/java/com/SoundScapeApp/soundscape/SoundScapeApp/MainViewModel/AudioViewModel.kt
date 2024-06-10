package com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.data.LocalMediaProvider
import com.SoundScapeApp.soundscape.SoundScapeApp.data.MusicRepository
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Playlist
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.AudioState
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.MusicServiceHandler
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.PlaybackState
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.PlayerEvent
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


//private val audioDummy = Audio(
//    uri = "".toUri(),
//    displayName = "",
//    id = 0L,
//    artist = "",
//    data = "",
//    duration = 0,
//    title = "",
//    albumId = "",
//    artwork = "",
//    albumName = "",
//)


enum class SortType {
    DATE_ADDED_DESC,
    DATE_ADDED_ASC,
    TITLE_ASC,
    TITLE_DESC
}

enum class VideoSortType {
    DATE_ADDED_DESC,
    DATE_ADDED_ASC,
    TITLE_ASC,
    TITLE_DESC,
    SIZE_DESC,
    SIZE_ASC
}


@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioServiceHandler: MusicServiceHandler,
    private val repository: MusicRepository,
    private val player: ExoPlayer,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    context: Context,
    sharedPreferences: SharedPreferences,
    audioStateHandle: SavedStateHandle,
    private val localMediaProvider: LocalMediaProvider

) : ViewModel() {


    private var equalizer: Equalizer? = null
    var bassBoost: BassBoost? = null
    var virtualizer: Virtualizer? = null
    var loudnessEnhancer:LoudnessEnhancer? = null

    //setting bass
    private val _currentBassLevel = MutableStateFlow(0f)
    val currentBassLevel: StateFlow<Float> = _currentBassLevel

    //setting virtualizer
    private val _currentVirtualizerLevel = MutableStateFlow(0f)
    val currentVirtualizerLevel: StateFlow<Float> = _currentVirtualizerLevel

    private val _equalizerBandLevels = MutableStateFlow(listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f))
    val equalizerBandLevels: StateFlow<List<Float>>  = _equalizerBandLevels

    private val _customEqualizerBandLevels = MutableStateFlow(listOf(0f, 0f, 0f, 0f, 0f))
    val customEqualizerBandLevels: StateFlow<List<Float>>  = _customEqualizerBandLevels

    private val _selectedPreset = MutableStateFlow(Preset.NORMAL)
    val selectedPreset : StateFlow<Preset> = _selectedPreset


    var duration by audioStateHandle.saveable { mutableStateOf(0L) }
    var progress by audioStateHandle.saveable { mutableStateOf(0f) }

    private val _songProgress = MutableStateFlow(0f)
    val songProgress: StateFlow<Float> = _songProgress

    private var progressString by audioStateHandle.saveable { mutableStateOf("00:00") }


    var isPlying by audioStateHandle.saveable { mutableStateOf(false) }

    //private val _isPlaying = MutableStateFlow<Boolean>(false)
//    val isPlaying:StateFlow<Boolean> = _isPlaying
    var currentSelectedAudio by audioStateHandle.saveable { mutableStateOf(0L) }

    // audioList
    // For AudioList
    private val _audioList = MutableStateFlow(listOf<Audio>())
    val audioList: StateFlow<List<Audio>> get() = _audioList

    private val _scannedAudioList = MutableStateFlow(listOf<Audio>())
    val scannedAudioList: StateFlow<List<Audio>> get() = _scannedAudioList

    // Playlist Songs
    private val _playListSongsList: MutableStateFlow<List<Audio>> = MutableStateFlow(emptyList())
    private val playListSongsList: StateFlow<List<Audio>> = _playListSongsList.asStateFlow()

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    // SHOULD SET ITEMS AGAIN OR NOT TRACK
    var setMediaItems by audioStateHandle.saveable { mutableStateOf(false) }


    //    PLAYLISTS PART
    private val _playlists: MutableStateFlow<List<Playlist>> = MutableStateFlow(emptyList())
    val playlists: StateFlow<List<Playlist>> get() = _playlists


    //    OnPlaylistClicked
    private val _currentPlaylistId = MutableStateFlow<Long?>(null)
    val currentPlaylistId: StateFlow<Long?> = _currentPlaylistId

    //    Last created playlist id
    private val _currentCreatedPlaylistId = MutableStateFlow(0L)
    private val currentCreatedPlaylistId: StateFlow<Long?> = _currentCreatedPlaylistId


    private val _currentPlaylistName = MutableStateFlow<String?>(null)
    val currentPlaylistName: StateFlow<String?> = _currentPlaylistName

    //    On album Clicked
    private val _currentAlbumId = MutableStateFlow<Long?>(null)
    val currentAlbumId: StateFlow<Long?> = _currentAlbumId

    //    on Artist Clicked
    private val _currentArtist = MutableStateFlow<String?>(null)
    val currentArtist: StateFlow<String?> = _currentArtist

    //    Current Album songs
    private val _currentAlbumSongs: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    val currentAlbumSongs: StateFlow<List<Long>> = _currentAlbumSongs.asStateFlow()

    //    CurrentPlayListItems
    private val _currentPlaylistSongs = MutableStateFlow<List<Long>>(emptyList())
    val currentPlaylistSongs: StateFlow<List<Long>> = _currentPlaylistSongs

    //    Favorites songs
    private val _favoritesSongs = MutableStateFlow<List<Long>>(emptyList())
    val favoritesSongs: StateFlow<List<Long>> = _favoritesSongs

    //    Current Artist Songs
    private val _currentArtistSongs = MutableStateFlow<List<Long>>(emptyList())
    val currentArtistSongs: StateFlow<List<Long>> = _currentArtistSongs


    //    Check if app is already open
    private val _isSearch = MutableStateFlow(false)
    val isSearch: StateFlow<Boolean> = _isSearch


    //    SortType
    var currentSortType: SortType = getSortType()

    // playlistSelection track
    private val _isPlaylistSelected = MutableStateFlow(false)
    val isPlaylistSelected: StateFlow<Boolean> = _isPlaylistSelected

    //AllSongListSongSelectionTrack
    // playlistSelection track
    private val _isSongSelected = MutableStateFlow(false)
    val isSongSelected: StateFlow<Boolean> = _isSongSelected


    private val _scanSongLengthTime = MutableStateFlow(0L)
    val scanSongLengthTime: StateFlow<Long> = _scanSongLengthTime


    private val _currentTheme = MutableStateFlow(1)
    val currentTheme: StateFlow<Int> = _currentTheme

    //Audio playing screen design
    private val _screenDesign = MutableStateFlow(1)
    val screenDesign: StateFlow<Int> = _screenDesign


    //PERMISSIONS
    val visiblePermissionDialogQueue = mutableStateListOf<String>()


    // SELECTED FOR DELETION
    private val _selectedSongs = MutableStateFlow<List<Long>>(emptyList())
    val selectedSongs: StateFlow<List<Long>> = _selectedSongs

    private val _isDeletingSong = MutableStateFlow(false)
    val isDeletingSong: StateFlow<Boolean> = _isDeletingSong


    // KEEPING TRACK OF CURRENT PLAYING
    private val _currentPlayingSection = MutableStateFlow<Int>(0)
    val currentPlayingSection: StateFlow<Int> = _currentPlayingSection

    //   Playing from current this playlist
    private val _currentPlayingPlaylistId = MutableStateFlow<Long>(0L)
    val currentPlayingPlaylistId: StateFlow<Long> = _currentPlayingPlaylistId

    //Playing From this Album
    private val _currentPlayingAlbumId = MutableStateFlow<Long>(0L)
    val currentPlayingAlbumId: StateFlow<Long> = _currentPlayingAlbumId

    //Playing From this Artist
    private val _currentPlayingArtist = MutableStateFlow<String?>(null)
    val currentPlayingArtist: StateFlow<String?> = _currentPlayingArtist


    // SEPARATE LISTS FOR RESUMPTION
    private val _currentPlaylistSongsForResumption = MutableStateFlow<List<Long>>(emptyList())
    val currentPlaylistSongsForResumption: StateFlow<List<Long>> =
        _currentPlaylistSongsForResumption


    private val _currentAlbumSongsForResumption: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())
    val currentAlbumSongsForResumption: StateFlow<List<Long>> = _currentAlbumSongsForResumption


    private val _currentArtistSongsForResumption: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())
    val currentArtistSongsForResumption: StateFlow<List<Long>> = _currentArtistSongsForResumption


    // PlayfromIntent
    val _currentMediaItem = MutableStateFlow<Audio?>(null)
    val currentMediaItemAudio: StateFlow<Audio?> = _currentMediaItem

    private val _isMainActivity = MutableStateFlow(false)
    val isMainActivity: StateFlow<Boolean> = _isMainActivity

    init {
//        loadAudioData()
//        loadVideoData()
//        setupAudioStateHandler()
        viewModelScope.launch {
            loadPlaylists()
            if (currentPlayingPlaylistId.value == 123L) {
                loadSongsForFavoritesResumption()
            } else {
                loadSongsForCurrentPlaylistResumption(currentPlayingPlaylistId.value)
            }
        }

        getTheme()
        getScanSongLengthTime()
        loadPlaylistAudioData()
        getFavoritesSongs()

        duration = player.duration
        player.shuffleModeEnabled = isShuffleEnabled()
        currentSelectedAudio = player.currentMediaItem?.mediaId?.toLongOrNull() ?: -1

        getMediaItemsFlags()

        getCurrentPlayingSection()
        getCurrentPlayingPlaylist()
        getCurrentPlayingAlbum()
        getCurrentPlayingArtist()

        retrievePlaybackState()

        setupAudioEffects()
        setupEqualizer()
    }

//    private val _audioData = MutableStateFlow<PagingData<Audio>>(PagingData.empty())
//    val audioData: Flow<PagingData<Audio>> get() = _audioData

    init {
        viewModelScope.launch {
            audioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    AudioState.Initial -> _uiState.value = UIState.Initial
                    is AudioState.Buffering -> calculateProgressValue(mediaState.progress)
                    is AudioState.Playing -> isPlying = player.isPlaying
                    is AudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is AudioState.CurrentPlaying -> {

                        val mediaId = player.currentMediaItem?.mediaId

                        currentSelectedAudio = mediaId?.toLong()
                            ?: (retrievePlaybackState().lastPlayedSong.toLongOrNull() ?: 0L)

                    }

                    is AudioState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }
    }

    //GRANT PERMISSIONS
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


    // AUDIOS
    fun sortAudioList(sortType: SortType) {
        currentSortType = sortType
        sharedPreferencesHelper.setCurrentSortType(sortType)

        val sortedList = when (sortType) {
            SortType.DATE_ADDED_ASC -> scannedAudioList.value.sortedBy { it.dateAdded }
            SortType.DATE_ADDED_DESC -> scannedAudioList.value.sortedByDescending { it.dateAdded }
            SortType.TITLE_ASC -> scannedAudioList.value.sortedBy { it.title }
            SortType.TITLE_DESC -> scannedAudioList.value.sortedByDescending { it.title }
        }
        _scannedAudioList.value = sortedList
    }


    fun loadAudioData() {
        viewModelScope.launch {
            if (audioList.value.isEmpty()) {
                val audio = repository.getAudioData()
                _audioList.value = audio

                if (player.currentMediaItemIndex > 0) {
                    currentSelectedAudio = player.currentMediaItem?.mediaId!!.toLong()
                }
                _playListSongsList.value =
                    audioList.value.filter { audios -> audios.id in currentPlaylistSongs.value }
                sortAudioList(currentSortType)
            }
            val length = scanSongLengthTime.value * 1000L
            loadScannedAudioList(length)

            if (_currentPlayingAlbumId.value != 0L) {
                loadSongsForAlbumResumption(currentPlayingAlbumId.value)
            }
            currentPlayingArtist.value?.let {
                loadSongsForArtistResumption(currentPlayingArtist.value.toString())
            }
        }
    }

    private fun loadScannedAudioList(
        scanLength: Long
    ) {
        if (audioList.value.isNotEmpty()) {
            val audiosList = audioList.value.filter {
                it.duration > scanLength
            }
            _scannedAudioList.value = audiosList
            sortAudioList(currentSortType)
        }
    }

    fun loadPlaylistAudioData() {
        viewModelScope.launch {
            val songs = audioList.value.filter { it.id in currentPlaylistSongs.value }
            if (playListSongsList.value.isEmpty()) {
                if (songs.isNotEmpty()) {
                    _playListSongsList.value = songs
                    Log.d("songs added", "added")
                } else {
                    Log.d("songs added", "empty or not found")
                }
            }
        }
    }

    //    Load album songs
    fun loadSongsForAlbum(albumId: Long) {
        viewModelScope.launch {
            val songsForAlbumIds =
                scannedAudioList.value.filter { it.albumId.toLong() == albumId }.map { it.id }
            _currentAlbumSongs.value = songsForAlbumIds
        }
    }

    fun loadSongsForAlbumResumption(albumId: Long) {
        viewModelScope.launch {
            val songsForAlbumIds =
                scannedAudioList.value.filter { it.albumId.toLong() == albumId }.map { it.id }
            _currentAlbumSongsForResumption.value = songsForAlbumIds
        }
    }

    //    Load artist songs
    fun loadSongsForArtist(artist: String) {
        viewModelScope.launch {
            val songsForArtist = scannedAudioList.value.filter { it.artist == artist }.map { it.id }
            _currentArtistSongs.value = songsForArtist
            Log.d("currentArtistSongs", currentArtistSongs.value.toString())
        }
    }

    fun loadSongsForArtistResumption(artist: String) {
        viewModelScope.launch {
            val songsForArtist = scannedAudioList.value.filter { it.artist == artist }.map { it.id }
            _currentArtistSongsForResumption.value = songsForArtist
        }
    }

    //        For All songs
    fun setMediaItems(audioList: List<Audio>, context: Context) {
        if (audioList.isNotEmpty()) {
            val mediaItems = audioList.map { audio ->
                MediaItem.Builder()
                    .setUri(audio.uri)
                    .setMediaId(audio.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(audio.artist)
                            .setAlbumArtist(audio.artist)
                            .setDisplayTitle(audio.title)
                            .setSubtitle(audio.displayName)
                            .setArtworkUri(audio.artwork.toUri())
                            .build()
                    ).build()
            }
            audioServiceHandler.setMediaItemList(mediaItems)
        }
    }

//    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
//    fun setMediaItems(audioList: List<Audio>, context: Context) {
//        if (audioList.isNotEmpty()) {
//            val mediaItems = audioList.map { audio ->
//                MediaItem.Builder()
//                    .setUri(audio.uri)
//                    .setMediaId(audio.id.toString())
//                    .setMediaMetadata(
//                        MediaMetadata.Builder()
//                            .setArtist(audio.artist)
//                            .setAlbumArtist(audio.artist)
//                            .setDisplayTitle(audio.title)
//                            .setSubtitle(audio.displayName)
//                            .setArtworkUri(audio.artwork.toUri())
//                            .build()
//                    ).build()
//            }
//
//            val dataSourceFactory = DefaultDataSourceFactory(context, null)
//
//            // Create MediaSources for the MediaItems using your DataSourceFactory
//            val mediaSources = mediaItems.map { mediaItem ->
//                ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mediaItem)
//            }
//            player.prepare()
//            player.setMediaSources(mediaSources)
//        }
//    }


    //    Set single media item
    fun setSingleMediaItem(audio: Audio) {
        val mediaItem =
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaId(audio.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setDisplayTitle(audio.title)
                        .setSubtitle(audio.displayName)
                        .setArtworkUri(audio.artwork.toUri())
                        .build()
                ).build()

        audioServiceHandler.setMediaItem(mediaItem)
    }

    fun setPlayListSongsList(songsList: List<Audio>) {
        _playListSongsList.value = songsList
    }


    private fun setupAudioStateHandler() = viewModelScope.launch {
        audioServiceHandler.audioState.collectLatest { mediaState ->

        }
    }

    fun play(index: Int) {
        audioServiceHandler.play(index)
    }


    private fun calculateProgressValue(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat()) / duration.toFloat()) * 100f
            else 0f
        progressString = formatDuration(currentProgress)
        _songProgress.value =
            if (currentProgress > 0) ((currentProgress.toFloat()) / duration.toFloat()) * 100f
            else 0f
    }

    fun onUiEvents(uiEvents: UIEvents) = viewModelScope.launch {
        when (uiEvents) {
            UIEvents.Backward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Backward)
            UIEvents.Forward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Forward)
            UIEvents.SeekPrevious -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrevious)
            UIEvents.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            is UIEvents.PlayPause -> audioServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
            is UIEvents.SeekTo -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = ((duration * uiEvents.position) / 100f).toLong()
                )
            }

            is UIEvents.PlaySelectedAudio -> {
                viewModelScope.launch {
                    audioServiceHandler.play(uiEvents.index)
                }
            }

            is UIEvents.SelectedAudioChange -> {
            }

            is UIEvents.UpdateProgress -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.UpdateProgress(
                        uiEvents.newProgress
                    )
                )
                progress = uiEvents.newProgress
                _songProgress.value = uiEvents.newProgress / 1000
            }
        }
    }

    fun onProgressSeek(progress: Float) {
        val totalDuration = player.duration.toFloat()
        if (totalDuration > 0) {
            val targetPosition = (progress / 100f * totalDuration).toLong()
            player.seekTo(targetPosition)
        }
    }

    fun onCircularProgressSeek(progress: Float) {
        val totalDuration = player.duration.toFloat()
        if (totalDuration > 0) {
            val targetPosition = (progress / 300f * totalDuration).toLong()
            player.seekTo(targetPosition)
        }
    }


    //    Shuffle Logic
    private fun isShuffleEnabled(): Boolean {
        return sharedPreferencesHelper.isShuffleEnabled()
    }

    private fun setShuffleEnabled(isShuffleEnabled: Boolean) {
        sharedPreferencesHelper.setShuffleEnabled(isShuffleEnabled)
    }

    fun toggleShuffle() {
        if (!isShuffleEnabled()) {
            setShuffleEnabled(true)
            player.shuffleModeEnabled = true
        } else {
            setShuffleEnabled(false)
            player.shuffleModeEnabled = false
        }
    }

    //    Repeat Logic
    fun toggleRepeat() {
        audioServiceHandler.toggleRepeat()

    }

    //    PLAYLISTS LOGICS
    private suspend fun loadPlaylists() {
        delay(1000)
        _playlists.value = sharedPreferencesHelper.getPlaylists().toMutableList()

        updatePlaylists()
    }

    //    Create a  playlist
    fun saveNewPlaylist(newPlaylistName: String, songIds: List<Long> = emptyList()) {
        val existingPlaylists = _playlists.value.toMutableList()
        val newPlaylist = Playlist(
            id = System.currentTimeMillis(),
            name = newPlaylistName,
            songIds = songIds
        )
        existingPlaylists.add(newPlaylist)

        _currentCreatedPlaylistId.value = newPlaylist.id
        Log.d("yesnewklsd", newPlaylist.id.toString())

        sharedPreferencesHelper.savePlaylists(existingPlaylists)

        _playlists.value = existingPlaylists
    }


    //    Edit playlist Name
    fun editPlaylistName(playlistId: Long, newName: String) {
        sharedPreferencesHelper.editPlaylistName(playlistId, newName)

        // Update the UI by updating the playlists LiveData
        val updatedPlaylists = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                Playlist(playlist.id, newName, playlist.songIds)
            } else {
                playlist
            }
        }
        _playlists.value = updatedPlaylists
    }


    //  Clicked Playlist
    fun onPlaylistClicked(playlistId: Long, playListName: String) {
        _currentPlaylistId.value = playlistId
        _currentPlaylistName.value = playListName
//        viewModelScope.launch {
//            loadSongsForCurrentPlaylist(playlistId)
//        }
    }

    //    Clicked Favorites
    fun onFavoritesClicked(playlistId: Long, playListName: String) {
        _currentPlaylistId.value = playlistId
        _currentPlaylistName.value = playListName
        loadSongsForFavorites()
    }

    //    Check if playing from playList
//    fun onPlayListPlay(playingFromPlayList: Boolean) {
//        _isPlayingFromPlaylist.value = playingFromPlayList
//    }

//    fun onPlayAllPlay(playingFromAll: Boolean) {
//        _isPlayingFromAll.value = playingFromAll
//    }

    //    SetMediaItemFlag
    fun setMediaItemFlag(setMediaItem: Boolean) {
        setMediaItems = setMediaItem
        setMediaItemsFlags(setMediaItem)
    }

    fun addSongToPlaylist(songIds: List<Long>, context: Context) {
        val currentPlaylistId = _currentPlaylistId.value
        if (currentPlaylistId != null) {
            // Update the playlist in SharedPreferencesHelper
            viewModelScope.launch {
                sharedPreferencesHelper.addSongsToPlaylist(
                    currentPlaylistId,
                    songIds,
                    context
                )
            }
            // Update the current playlist songs
            viewModelScope.launch {
                val currentPlaylistSongs =
                    sharedPreferencesHelper.getSongsByPlaylistId(currentPlaylistId)
                _currentPlaylistSongs.value = currentPlaylistSongs
            }

            // Update the count of songs in the playlist in the ViewModel
            val updatedPlaylist = _playlists.value.find { it.id == currentPlaylistId }
            updatedPlaylist?.let {
                val existingSongIds = it.songIds
                val updatedSongIds = existingSongIds.toMutableList().apply {
                    addAll(songIds.filter { songId -> !contains(songId) })
                }
                _playlists.value = _playlists.value.map { playlist ->
                    if (playlist.id == currentPlaylistId) {
                        Playlist(playlist.id, playlist.name, updatedSongIds)
                    } else {
                        playlist
                    }
                }
            }
        }
    }

    fun addSongToDifferentPlaylist(playlistId: Long, songIds: List<Long>, context: Context) {

        viewModelScope.launch {
            sharedPreferencesHelper.addSongsToPlaylist(playlistId, songIds, context = context)
        }

        val updatedPlaylist = _playlists.value.find { it.id == playlistId }
        updatedPlaylist?.let {
            val existingSongIds = it.songIds
            val updatedSongIds = existingSongIds.toMutableList().apply {
                addAll(songIds.filter { songId -> !contains(songId) })
            }
            _playlists.value = _playlists.value.map { playlist ->
                if (playlist.id == playlistId) {
                    Playlist(playlist.id, playlist.name, updatedSongIds)
                } else {
                    playlist
                }
            }
        }
    }

    //    Create and Add song
    fun createAndAddSongToPlaylist(songIds: List<Long>, context: Context) {
        val playlistId = currentCreatedPlaylistId.value
        viewModelScope.launch {
            sharedPreferencesHelper.addSongsToPlaylist(playlistId!!, songIds, context = context)
        }

        val updatedPlaylist = _playlists.value.find { it.id == playlistId }
        updatedPlaylist?.let {
            val existingSongIds = it.songIds
            val updatedSongIds = existingSongIds.toMutableList().apply {
                addAll(songIds.filter { songId -> !contains(songId) })
            }
            _playlists.value = _playlists.value.map { playlist ->
                if (playlist.id == playlistId) {
                    Playlist(playlist.id, playlist.name, updatedSongIds)
                } else {
                    playlist
                }
            }
        }
    }


    //   Load songs from playlist
    suspend fun loadSongsForCurrentPlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = sharedPreferencesHelper.getSongsByPlaylistId(playlistId)
            _currentPlaylistSongs.value = songs
        }
    }

    suspend fun loadSongsForCurrentPlaylistResumption(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = sharedPreferencesHelper.getSongsByPlaylistId(playlistId)
            _currentPlaylistSongsForResumption.value = songs
        }
    }


    fun deletePlaylist(playlistId: Long) {
        sharedPreferencesHelper.deletePlaylist(playlistId)

        if (_currentPlaylistId.value == playlistId) {
            _currentPlaylistId.value = null
            _currentPlaylistSongs.value = emptyList()
        }

        viewModelScope.launch {
            _playlists.value = sharedPreferencesHelper.getPlaylists().toMutableList()

        }
    }

    fun deletePlaylists(playlistIds: List<Long>) {
        sharedPreferencesHelper.deletePlaylists(playlistIds)
        val deletedCurrentPlaylist = _currentPlaylistId.value in playlistIds

        if (deletedCurrentPlaylist) {
            _currentPlaylistId.value = null
            _currentPlaylistSongs.value = emptyList()
        }

        viewModelScope.launch {
            _playlists.value = sharedPreferencesHelper.getPlaylists().toMutableList()
        }
    }

    fun deleteSongFromPlaylist(songIds: List<Long>) {
        val playlistId = currentPlaylistId.value ?: return

        // Delete the songs from the shared preferences using the helper function
        sharedPreferencesHelper.deleteSongsFromPlaylist(playlistId, songIds)

        // Update the UI or any other necessary operations in the ViewModel
        val updatedCurrentPlaylistSongs = _currentPlaylistSongs.value.toMutableList()
        updatedCurrentPlaylistSongs.removeAll(songIds)
        _currentPlaylistSongs.value = updatedCurrentPlaylistSongs.toList()

        // Update the count of songs in the playlist
        val updatedPlaylists = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                val updatedSongIds = playlist.songIds.toMutableList()
                updatedSongIds.removeAll(songIds)
                Playlist(playlist.id, playlist.name, updatedSongIds.toList())
            } else {
                playlist
            }
        }
        _playlists.value = updatedPlaylists
    }


    //    Clear Whole PlayList
    fun clearPlaylist(playlistId: Long) {
        sharedPreferencesHelper.clearSongsFromPlaylist(playlistId)

        // Update the UI or any other necessary operations in the ViewModel
        val updatedCurrentPlaylistSongs = emptyList<Long>()
        _currentPlaylistSongs.value = updatedCurrentPlaylistSongs

        // Update the count of songs in the playlist
        val updatedPlaylist = _playlists.value.find { it.id == playlistId }
        updatedPlaylist?.let {
            _playlists.value = _playlists.value.map { playlist ->
                if (playlist.id == playlistId) {
                    Playlist(playlist.id, playlist.name, emptyList())
                } else {
                    playlist
                }
            }
        }
    }

    //    Favorites Playlist section
    private fun addToFavorites(songId: Long) {
        sharedPreferencesHelper.addToFavorites(songId)
    }

    // Function to remove a song from favorites
    private fun removeFromFavorites(songId: Long) {
        sharedPreferencesHelper.removeFromFavorites(songId)
    }

    fun loadSongsForFavorites() {
        viewModelScope.launch {
            val songs = getFavoriteSongs()
            _currentPlaylistSongs.value = songs
        }
    }

    fun loadSongsForFavoritesResumption() {
        viewModelScope.launch {
            val songs = getFavoriteSongs()
            _currentPlaylistSongsForResumption.value = songs
        }
    }

    fun getFavoritesSongs() {
        val songs = getFavoriteSongs()
        _favoritesSongs.value = songs
    }

    // Function to get the list of favorite songs
    private fun getFavoriteSongs(): List<Long> {
        return sharedPreferencesHelper.getFavoriteSongs()
    }


    fun toggleFavorite(songId: Long) {
        val favoriteSongs = sharedPreferencesHelper.getFavoriteSongs()
        if (favoriteSongs.contains(songId)) {
            removeFromFavorites(songId)

            // Remove the song from the current playlist
            val updatedCurrentPlaylistSongs = _currentPlaylistSongs.value.toMutableList()
            updatedCurrentPlaylistSongs.remove(songId)
            _currentPlaylistSongs.value = updatedCurrentPlaylistSongs

            // Update the favorites songs
            val updatedFavoriteSongs = favoriteSongs.toMutableList()
            updatedFavoriteSongs.remove(songId)
            _favoritesSongs.value = updatedFavoriteSongs
        } else {
            addToFavorites(songId)
        }
    }

    fun removeMultipleSongsFromFavorites(songIds: List<Long>) {
        val favoriteSongs = sharedPreferencesHelper.getFavoriteSongs().toMutableList()
        val updatedCurrentPlaylistSongs = _currentPlaylistSongs.value.toMutableList()

        songIds.forEach { songId ->
            if (favoriteSongs.contains(songId)) {
                removeFromFavorites(songId)

                // Remove the song from the current playlist
                updatedCurrentPlaylistSongs.remove(songId)
                // Update the favorites songs
                favoriteSongs.remove(songId)
            }
        }

        _currentPlaylistSongs.value = updatedCurrentPlaylistSongs
        _favoritesSongs.value = favoriteSongs
    }


    //    ALBUMS SECTIONS
    fun albumClicked(albumId: Long) {
        _currentAlbumId.value = albumId
    }

    //    Artist
    fun artistClicked(artist: String) {
        _currentArtist.value = artist
    }

    //    search
    fun isAppSearch(isSearch: Boolean) {
        _isSearch.value = isSearch
    }

    //    EXTRAS
    @SuppressLint("DefaultLocale")
    private fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minute, seconds)
    }

    fun getSongImageUrl(songId: Long): String? {
        val audioList = audioList // Assuming you have access to the audio list in your ViewModel
        val song = audioList.value.firstOrNull { it.id == songId }
        return song?.artwork
    }

    // Function to check and remove IDs from playlists that are not in the audio list
    private fun removeExtraIdsFromPlaylists() {
        val audioIds = scannedAudioList.value.map { it.id }
        val playlists = _playlists.value

        val extraIdsMap = mutableMapOf<Long, MutableList<Long>>()

        playlists.forEach { playlist ->
            val extraIds = playlist.songIds.filterNot { audioIds.contains(it) }
            if (extraIds.isNotEmpty()) {
                extraIdsMap[playlist.id] = extraIds.toMutableList()
            }
        }

        extraIdsMap.forEach { (_, extraIds) ->
            sharedPreferencesHelper.removeDeletedSongsFromPlaylists(extraIds)
            _playlists.value = sharedPreferencesHelper.getPlaylists()
        }
    }

    private fun removeExtraIdsFromFavorites() {

        val audioIds = scannedAudioList.value.map { it.id }
        val favoriteIds = _favoritesSongs.value

        val extraIds = favoriteIds.filterNot { audioIds.contains(it) }


        sharedPreferencesHelper.removeDeletedSongsFromFavorites(extraIds)

        _favoritesSongs.value = getFavoriteSongs()

    }

    private fun updatePlaylists() {
        if (isMainActivity.value) {
            removeExtraIdsFromPlaylists()
            removeExtraIdsFromFavorites()
        }
        // You can add any other logic here if needed
    }

    fun setSortType(sortType: SortType) {
        sharedPreferencesHelper.setCurrentSortType(sortType)
    }

    private fun getSortType(): SortType {
        return sharedPreferencesHelper.getCurrentSortType()
    }


    //SELECTION SECTION
    fun setIsPlaylistSelected(playlistSelected: Boolean) {
        _isPlaylistSelected.value = playlistSelected
    }

    fun setIsSongSelected(songSelected: Boolean) {
        _isSongSelected.value = songSelected
    }


    fun setScanSongLengthTime(scanLength: Long) {
        sharedPreferencesHelper.setScanSongLengthTime(scanLength)
        _scanSongLengthTime.value = scanLength

        val length = scanLength * 1000
        loadScannedAudioList(length)
        setMediaItems = false
    }

    fun getScanSongLengthTime() {
        _scanSongLengthTime.value = sharedPreferencesHelper.getScanSongLengthTime()
    }

    fun setTheme(chooseTheme: Int) {
        sharedPreferencesHelper.setTheme(chooseTheme)
        _currentTheme.value = chooseTheme
    }

    fun getTheme() {
        _currentTheme.value = sharedPreferencesHelper.getTheme()
    }

    // Audio playing screen
    fun setScreenDesign(chooseDesign: Int) {
        _screenDesign.value = chooseDesign
    }

//    fun getScreenDesign() {
//        _currentTheme.value = sharedPreferencesHelper.getTheme()
//    }

    fun setSelectedSongs(selectedSongs: List<Long>) {
        _selectedSongs.value = selectedSongs
    }

    fun setIsDeletingSongs(deletingSongs: Boolean) {
        _isDeletingSong.value = deletingSongs
    }


    fun reloadSongs(selectedSongIds: List<Long>) {
        viewModelScope.launch {
            val filteredSongs = scannedAudioList.value.filter { song ->
                song.id !in selectedSongIds // Filter out songs whose IDs are in selectedSongIds
            }
            _audioList.value = filteredSongs
            _scannedAudioList.value = filteredSongs // Update all songs list

            if (currentPlaylistId.value != null) {
                loadSongsForCurrentPlaylist(currentPlaylistId.value!!)
            }
            updatePlaylists()
        }
    }

    fun retrievePlaybackState(): PlaybackState {
        return sharedPreferencesHelper.retrievePlaybackState()
    }

    fun restorePlaybackState(playbackState: PlaybackState, context: Context) {
        if (playbackState.lastPlayedSong != "0") {
            // Load the song and seek to the last playback position
            val song =
                loadSong(playbackState.lastPlayedSong.toLongOrNull() ?: 0L, context = context)

            song?.let {
                when (currentPlayingSection.value) {
                    0 -> {
                        setSingleMediaItem(song)
                        play(scannedAudioList.value.indexOf(song))
                        player.seekTo(playbackState.lastPlaybackPosition)
                    }

                    1 -> {
                        setMediaItems(scannedAudioList.value, context)
                        play(scannedAudioList.value.indexOf(song))
                        player.seekTo(playbackState.lastPlaybackPosition)
                    }

                    2 -> {
                        if (currentPlayingPlaylistId.value != 0L && currentPlaylistSongsForResumption.value.isNotEmpty()) {
                            val playListSongs: List<Audio> =
                                scannedAudioList.value.filter { audio ->
                                    audio.id in currentPlaylistSongsForResumption.value
                                }

                            setMediaItems(playListSongs, context)
                            Log.d("song", song.id.toString())
                            Log.d("playlistSongs", playListSongs.toString())
                            play(playListSongs.indexOf(song))
                            player.seekTo(playbackState.lastPlaybackPosition)

                        }
                    }

                    3 -> {
                        if (currentPlayingAlbumId.value != 0L && currentAlbumSongsForResumption.value.isNotEmpty()) {
                            val albumSongs: List<Audio> =
                                scannedAudioList.value.filter { audio ->
                                    audio.id in currentAlbumSongsForResumption.value
                                }

                            setMediaItems(albumSongs, context)
                            play(albumSongs.indexOf(song))
                            player.seekTo(playbackState.lastPlaybackPosition)

                        }
                    }

                    4 -> {
                        if (currentPlayingArtist.value != null && currentArtistSongsForResumption.value.isNotEmpty()) {
                            val artistSongs: List<Audio> =
                                scannedAudioList.value.filter { audio ->
                                    audio.id in currentArtistSongsForResumption.value
                                }

                            setMediaItems(artistSongs, context)
                            play(artistSongs.indexOf(song))
                            player.seekTo(playbackState.lastPlaybackPosition)

                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun loadSong(songId: Long, context: Context): Audio? {
        val song = scannedAudioList.value.firstOrNull { it.id == songId }
        return song!!
    }

    fun setCurrentPlayingSection(playingFrom: Int) {
        _currentPlayingSection.value = playingFrom
        sharedPreferencesHelper.setCurrentPlayingSection(playingFrom)
    }

    fun getCurrentPlayingSection() {
        _currentPlayingSection.value = sharedPreferencesHelper.getCurrentPlayingSection()
    }

    fun setMediaItemsFlags(setMediaItems: Boolean) {
        sharedPreferencesHelper.setMediaItemsFlag(setMediaItems)
    }

    fun getMediaItemsFlags() {
        setMediaItems = sharedPreferencesHelper.getMediaItemsFlag()
    }

    fun setCurrentPlayingPlaylist(playlistId: Long) {
        _currentPlayingPlaylistId.value = playlistId
        sharedPreferencesHelper.setCurrentPlayingPlaylist(playlistId)
    }

    fun getCurrentPlayingPlaylist() {
        _currentPlayingPlaylistId.value = sharedPreferencesHelper.getCurrentPlayingPlaylist()
    }

    fun setCurrentPlayingAlbum(albumId: Long) {
        _currentPlayingAlbumId.value = albumId
        sharedPreferencesHelper.setCurrentPlayingAlbum(albumId)
    }

    fun getCurrentPlayingAlbum() {
        _currentPlayingAlbumId.value = sharedPreferencesHelper.getCurrentPlayingAlbum()
    }

    fun setCurrentPlayingArtist(artist: String) {
        _currentPlayingArtist.value = artist
        sharedPreferencesHelper.setCurrentPlayingArtist(artist)
    }

    fun getCurrentPlayingArtist() {
        _currentPlayingArtist.value = sharedPreferencesHelper.getCurrentPlayingArtist()
    }

    //Share songs
    fun shareAudio(context: Context, audioUri: Uri, audioTitle: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "audio/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, audioUri)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, audioTitle)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Share audio via"))
    }

    fun shareAudios(context: Context, audioUris: List<Uri>, audioTitles: List<String>) {
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.type = "audio/*"

        val uriArrayList = ArrayList<Uri>(audioUris)
        val titleArrayList = ArrayList<String>(audioTitles)

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList)
        shareIntent.putStringArrayListExtra(Intent.EXTRA_SUBJECT, titleArrayList)

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(Intent.createChooser(shareIntent, "Share audio via"))
    }


    // Equalizers and bass Boosters
    @androidx.annotation.OptIn(UnstableApi::class)
    fun setupAudioEffects() {
        val audioSessionId = player.audioSessionId
        if (audioSessionId != C.INDEX_UNSET) {
            equalizer = Equalizer(0, audioSessionId).apply { enabled = true }
            bassBoost = BassBoost(0, audioSessionId).apply { enabled = true }
            virtualizer = Virtualizer(0, audioSessionId).apply { enabled = true }
            loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply { enabled = true }
        }
    }


    fun adjustLoudnessEnhancer(gain: Float) {
        // Ensure the input value is within the range [0, 1]
        val clampedGain = gain.coerceIn(0f, 1f)

        // Define the maximum gain value in millibels
        val MAX_GAIN_MILLIBELS = 1000 // Adjust as needed

        // Map the float value to the range of gain values (in millibels)
        val desiredGain = (clampedGain * MAX_GAIN_MILLIBELS).toInt()

        try {
            loudnessEnhancer?.setTargetGain(desiredGain)
        } catch (e: IllegalStateException) {
            // Handle IllegalStateException
        } catch (e: IllegalArgumentException) {
            // Handle IllegalArgumentException
        } catch (e: UnsupportedOperationException) {
            // Handle UnsupportedOperationException
        }
    }


    fun adjustBass(level: Float) {
        val strength =
            (level * 1000).toInt().toShort()
        bassBoost?.setStrength(strength)
        _currentBassLevel.value = level
    }
    fun adjustVirtualizer(level: Float) {
        val strength =
            (level * 1000).toInt().toShort()
        virtualizer?.setStrength(strength)
        _currentVirtualizerLevel.value = level
    }

    fun setBandLevel(band: Int, level: Float) {
        val newLevels = _equalizerBandLevels.value.toMutableList().apply {
            this[band] = level
        }
        _equalizerBandLevels.value = newLevels
        _customEqualizerBandLevels.value = newLevels
        applyEqualizerBandLevels(newLevels)
    }


    fun setPreset(preset: Preset) {
        Log.d("AudioViewModel", "Setting preset: ${preset.name}")
        val presetLevels = when (preset) {
            Preset.NORMAL -> listOf(0.4f, 0.4f, 0.4f, 0.4f, 0.4f)
            Preset.JAZZ -> listOf(0.4f, 0.6f, 0.8f, 0.6f, 0.4f)
            Preset.POP -> listOf(0.8f, 0.6f, 0.4f, 0.6f, 0.8f)
            Preset.CLASSIC -> listOf(0.4f, 0.1f, 0.3f, 0.2f, 0.2f)
            Preset.CUSTOM -> _customEqualizerBandLevels.value
        }

        _equalizerBandLevels.value = presetLevels
        _selectedPreset.value = preset
        applyEqualizerBandLevels(presetLevels)
    }


    private fun setupEqualizer() {
        equalizer?.let {
            val bands = List(it.numberOfBands.toInt()) { index ->
                val level = it.getBandLevel(index.toShort()).toFloat()
                val minLevel = it.bandLevelRange[0].toFloat()
                val maxLevel = it.bandLevelRange[1].toFloat()
                (level - minLevel) / (maxLevel - minLevel)
            }
            _equalizerBandLevels.value = bands
        }
    }

    private fun applyEqualizerBandLevels(levels: List<Float>) {
        equalizer?.let { eq ->
            val minLevel = eq.bandLevelRange[0]
            val maxLevel = eq.bandLevelRange[1]

            levels.forEachIndexed { index, level ->
                try {
                    // Map slider value to the range between minLevel and maxLevel
                    var bandLevel = ((level * (maxLevel - minLevel)) + minLevel).toInt().toShort()

                    // Ensure bandLevel stays within the valid range
                    bandLevel = bandLevel.coerceIn(minLevel, maxLevel)

                    // Set the band level
                    eq.setBandLevel(index.toShort(), bandLevel)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
    }


//    fun setPreset(preset: Preset) {
//        when (preset) {
//            Preset.NO_BASS -> {
//                adjustBass(0f)
//                // Set equalizer bands for No Bass
//                equalizer?.numberOfBands?.let { bands ->
//                    for (i in 0 until bands) {
//                        adjustEqualizer(i.toShort(), 0f)
//                    }
//                }
//            }
//            Preset.JAZZ -> {
//                adjustBass(0.5f)
//                // Set equalizer bands for Jazz
//                equalizer?.numberOfBands?.let { bands ->
//                    for (i in 0 until bands) {
//                        val level = when (i.toShort()) {
//                            0.toShort() -> 0.2f
//                            1.toShort() -> 0.3f
//                            2.toShort() -> 0.4f
//                            3.toShort() -> 0.5f
//                            4.toShort() -> 0.6f
//                            else -> 0.5f
//                        }
//                        adjustEqualizer(i.toShort(), level)
//                    }
//                }
//            }
//            Preset.POP -> {
//                adjustBass(0.8f)
//                // Set equalizer bands for Pop
//                equalizer?.numberOfBands?.let { bands ->
//                    for (i in 0 until bands) {
//                        val level = when (i.toShort()) {
//                            0.toShort() -> 0.5f
//                            1.toShort() -> 0.6f
//                            2.toShort() -> 0.7f
//                            3.toShort() -> 0.8f
//                            4.toShort() -> 0.9f
//                            else -> 0.8f
//                        }
//                        adjustEqualizer(i.toShort(), level)
//                    }
//                }
//            }
//        }
//    }


    // Play From Intent
    private fun setMediaItem(uri: Uri, displayName: String, id: String) {
        player.apply {
            addMediaItem(
                MediaItem.Builder()
                    .setUri(uri)
                    .setMediaId(id)
                    .setMediaMetadata(MediaMetadata.Builder().setDisplayTitle(displayName).build())
                    .build()
            )
            prepare()
            play()
            audioServiceHandler.startProgressUpdate()
        }
    }

    private fun updateCurrentAudioItem(audioItem: Audio) {
        player.clearMediaItems()
        setMediaItemFlag(false)
        _currentMediaItem.value = audioItem
        setMediaItem(audioItem.uri, audioItem.displayName, audioItem.id.toString())
    }

    fun onIntent(uri: Uri) {
        localMediaProvider.getAudioItemFromContentUri(uri)?.let {
            updateCurrentAudioItem(it)
        }
    }

    fun onNewIntent(uri: Uri) {
        localMediaProvider.getAudioItemFromContentUri(uri)?.let {
            updateCurrentAudioItem(it)
        }
    }

    fun setIsMainActivity(isMainActivity: Boolean) {
        _isMainActivity.value = isMainActivity
    }

    override fun onCleared() {
        super.onCleared()
    }
}

sealed class UIEvents {
    data object PlayPause : UIEvents()
    data class SelectedAudioChange(val songId: Long) : UIEvents()
    data class SeekTo(val position: Float) : UIEvents()
    data object SeekToNext : UIEvents()
    data class PlaySelectedAudio(val index: Int) : UIEvents()
    data object SeekPrevious : UIEvents()
    data object Backward : UIEvents()
    data object Forward : UIEvents()

    data class UpdateProgress(val newProgress: Float) : UIEvents()
}

sealed class UIState {
    data object Initial : UIState()
    data object Ready : UIState()
}

enum class Preset {
    CUSTOM,
    NORMAL,
    JAZZ,
    POP,
    CLASSIC,
}
