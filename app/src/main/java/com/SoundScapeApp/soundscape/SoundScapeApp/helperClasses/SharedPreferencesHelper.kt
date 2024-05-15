package com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.edit
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.SortType
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Playlist
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) {

    private val PLAYLISTS_KEY = "playlists_key"
    private val FAVORITES_KEY = "favorites_key"

    private val PLAYING_FROM_ALL_KEY = "playing_from_all_key"
    private val SHUFFLE_KEY = "shuffle_key"
    private val CURRENT_PLAYLIST_ID_KEY = "current_playlist_id_key"
    private val SORT_TYPE_KEY = "sort_type_key"
    private val PLAYBACK_SPEED_KEY = "playback_speed_key"
    private val DEFAULT_PLAYBACK_SPEED = 1f
    private val SONGS_SCAN_LENGTH_KEY = "songs_length_scan_key"
    private val THEME_KEY = "theme_key"
    private val CURRENT_SONG_KEY = "current_song_key"



//    fun getPlaylists(): List<Playlist> {
//        val playlistsJson = sharedPreferences.getString(PLAYLISTS_KEY, null)
//        return if (playlistsJson.isNullOrEmpty()) {
//            emptyList()
//        } else {
//            val typeToken = object : TypeToken<List<Playlist>>() {}.type
//            Gson().fromJson(playlistsJson, typeToken)
//        }
//    }

    fun getPlaylists(): List<Playlist> {
        val playlistsJson = sharedPreferences.getString(PLAYLISTS_KEY, null)
        return if (playlistsJson != null) {
            val listType = object : TypeToken<List<Playlist>>() {}.type
            Gson().fromJson(playlistsJson, listType)
        } else {
            emptyList()
        }
    }


    //    Remove songs which are deleted from device
    fun removeDeletedSongsFromPlaylists(deletedSongIds: List<Long>) {
        val playlists = getPlaylists().map { playlist ->
            val updatedSongIds = playlist.songIds.toMutableList() ?: mutableListOf()
            updatedSongIds.removeAll { deletedSongIds.contains(it) }
            playlist.copy(songIds = updatedSongIds)
        }
        savePlaylists(playlists)
    }


    fun savePlaylists(playlists: List<Playlist>) {
        val playlistsJson = Gson().toJson(playlists)
        sharedPreferences.edit().putString(PLAYLISTS_KEY, playlistsJson).apply()
    }


    fun editPlaylistName(playlistId: Long, newName: String) {
        val playlists = getPlaylists().toMutableList()

        val updatedPlaylists = playlists.map {
            if (it.id == playlistId) {
                it.copy(name = newName)
            } else {
                it
            }
        }
        savePlaylists(updatedPlaylists)
    }
//
    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>, context: Context) {
        val playlists = getPlaylists().toMutableList()

        val updatedPlaylists = playlists.map {
            if (it.id == playlistId) {
                val existingSongIds = it.songIds ?: emptyList()
                val newSongIds = existingSongIds.toMutableList()

                // Add only those song IDs that are not already present in the playlist
                songIds.forEach { songId ->
                    if (!existingSongIds.contains(songId)) {
                        newSongIds.add(songId)
                    } else {
                        Toast.makeText(context, "song already exists", Toast.LENGTH_SHORT).show()
                    }
                }

                it.copy(songIds = newSongIds)
            } else {
                it
            }
        }
        savePlaylists(updatedPlaylists)
    }

//
//    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>, context: Context) {
//        withContext(Dispatchers.IO) {
//            val playlists = getPlaylists().toMutableList()
//
//            val updatedPlaylists = playlists.map {
//                if (it.id == playlistId) {
//                    val existingSongIds = it.songIds ?: emptyList()
//                    val newSongIds = existingSongIds.toMutableList()
//
//                    // Add only those song IDs that are not already present in the playlist
//                    songIds.forEach { songId ->
//                        if (!existingSongIds.contains(songId)) {
//                            newSongIds.add(songId)
//                        } else {
//                            // Avoid UI-related operations in the background thread
//                            // Instead, use a callback or return the result to the UI
//                            // You can log the message or use a callback to display it in the UI
//                            // Logging the message as an example
//                            println("Song already exists")
//                        }
//                    }
//
//                    it.copy(songIds = newSongIds)
//                } else {
//                    it
//                }
//            }
//            savePlaylists(updatedPlaylists)
//        }
//    }

//    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>, context: Context) {
//        val playlists = getPlaylists().toMutableList()
//
//        val updatedPlaylists = playlists.map {
//            if (it.id == playlistId) {
//                val existingSongIds = it.songIds ?: emptyList()
//                val newSongIds = existingSongIds.toMutableList()
//
//                // Add only those song IDs that are not already present in the playlist
//                songIds.forEach { songId ->
//                    if (!existingSongIds.contains(songId)) {
//                        newSongIds.add(songId)
//                    } else {
//                        Toast.makeText(context, "song already exists", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                it.copy(songIds = newSongIds)
//            } else {
//                it
//            }
//        }
//        savePlaylists(updatedPlaylists)
//    }



//    suspend fun getSongsByPlaylistId(playlistId: Long): List<Long> {
//        val playlists = getPlaylists()
//        return playlists.firstOrNull { it.id == playlistId }?.songIds.orEmpty()
//    }

    suspend fun getSongsByPlaylistId(playlistId: Long): List<Long> {
        return withContext(Dispatchers.IO) {
            val playlists = getPlaylists()
            playlists.firstOrNull { it.id == playlistId }?.songIds.orEmpty()
        }
    }


    fun deletePlaylist(playlistId: Long) {
        val playlists = getPlaylists().toMutableList()
        val updatedPlaylists = playlists.filter { it.id != playlistId }
        savePlaylists(updatedPlaylists)
    }

    fun deletePlaylists(playlistIds: List<Long>) {
        val playlists = getPlaylists().toMutableList()
        val updatedPlaylists = playlists.filterNot { playlistIds.contains(it.id) }
        savePlaylists(updatedPlaylists)
    }


    fun deleteSongsFromPlaylist(playlistId: Long, songIdsToDelete: List<Long>) {
        val playlists = getPlaylists().toMutableList()

        val updatedPlaylists = playlists.map { playlist ->
            if (playlist.id == playlistId) {
                val newSongIds =
                    playlist.songIds.orEmpty().filterNot { id -> songIdsToDelete.contains(id) }
                playlist.copy(songIds = newSongIds)
            } else {
                playlist
            }
        }
        savePlaylists(updatedPlaylists)
    }

    //    Clear Playlist
    fun clearSongsFromPlaylist(playlistId: Long) {
        val playlists = getPlaylists().toMutableList()

        val updatedPlaylists = playlists.map {
            if (it.id == playlistId) {
                it.copy(songIds = emptyList())
            } else {
                it
            }
        }
        savePlaylists(updatedPlaylists)
    }


    //    Favorites playlist
    private fun getFavoritesPlaylist(): Playlist {
        val favoritesJson = sharedPreferences.getString(FAVORITES_KEY, null)
        return if (favoritesJson.isNullOrEmpty()) {
            val favorites = Playlist(0, "Favorites", emptyList())
            saveFavoritesPlaylist(favorites)
            favorites
        } else {
            Gson().fromJson(favoritesJson, Playlist::class.java)
        }
    }

    private fun saveFavoritesPlaylist(favorites: Playlist) {
        val favoritesJson = Gson().toJson(favorites)
        sharedPreferences.edit().putString(FAVORITES_KEY, favoritesJson).apply()
    }

    fun addToFavorites(songId: Long) {
        val favoritesPlaylist = getFavoritesPlaylist() ?: Playlist(0, "Favorites", emptyList())
        val updatedFavorites = favoritesPlaylist.copy(songIds = favoritesPlaylist.songIds + songId)
        saveFavoritesPlaylist(updatedFavorites)
    }

    fun removeFromFavorites(songId: Long) {
        val favoritesPlaylist = getFavoritesPlaylist() ?: return
        val updatedSongIds = favoritesPlaylist.songIds.filter { it != songId }
        val updatedFavorites = favoritesPlaylist.copy(songIds = updatedSongIds)
        saveFavoritesPlaylist(updatedFavorites)
    }

    //    Remove songs if deleted from device
    fun removeDeletedSongsFromFavorites(deletedSongIds: List<Long>) {
        val favoritesPlaylist = getFavoritesPlaylist()
        val updatedSongIds = favoritesPlaylist.songIds.filterNot { deletedSongIds.contains(it) }
        val updatedFavorites = favoritesPlaylist.copy(songIds = updatedSongIds)
        saveFavoritesPlaylist(updatedFavorites)
    }

    fun getFavoriteSongs(): List<Long> {
        val favoritesPlaylist = getFavoritesPlaylist()
        return favoritesPlaylist.songIds ?: emptyList()
    }


    //    Shuffle Logic
    fun isShuffleEnabled(): Boolean {
        return sharedPreferences.getBoolean(
            SHUFFLE_KEY,
            false
        )  // Default is false, change as needed
    }

    fun setShuffleEnabled(isShuffleEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(SHUFFLE_KEY, isShuffleEnabled).apply()
    }

    fun setCurrentSortType(sortType: SortType) {
        sharedPreferences.edit {
            putString(SORT_TYPE_KEY, sortType.name)
            apply()
        }
    }

    fun getCurrentSortType(): SortType {
        val defaultSortType = SortType.TITLE_DESC // Set default sort type here
        val sortTypeName = sharedPreferences.getString(SORT_TYPE_KEY, defaultSortType.name)
        return SortType.valueOf(sortTypeName ?: defaultSortType.name)
    }


    fun setScanSongLengthTime(scanLength: Long) {
        sharedPreferences.edit {
            putLong(SONGS_SCAN_LENGTH_KEY, scanLength)
            apply()
        }
    }

    fun getScanSongLengthTime(): Long {
        return sharedPreferences.getLong(SONGS_SCAN_LENGTH_KEY, 0L)
    }


    fun setTheme(theme: Int) {
        sharedPreferences.edit().putInt(THEME_KEY, theme).apply()
    }

    fun getTheme(): Int {
        return sharedPreferences.getInt(THEME_KEY, 1)
    }

    fun setCurrentPlayingSong(currentMediaId: String) {
        sharedPreferences.edit().putString(CURRENT_SONG_KEY, currentMediaId).apply()
    }

    fun getCurrentPlayingSong(): String? {
        return sharedPreferences.getString(CURRENT_SONG_KEY, "0")
    }
}

