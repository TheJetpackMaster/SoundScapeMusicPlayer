package com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses

import Video
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.edit
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoSortType
import com.SoundScapeApp.soundscape.SoundScapeApp.data.videoPlaylist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VideoPlaylistManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    private val PLAYLISTS_KEY = "video_playlists_key"
    private val SORT_TYPE_KEY = "video_sort_type_key"
    private val PREF_PLAYBACK_POSITIONS = "pref_playback_positions"
    private val VIDEO_SEEK_FORWARD_KEY = "video_seek_forward"
    private val DOUBLETAP_TO_SEEK_KEY = "doubletap_to_seek"
    private val RESUME_KEY = "resume_from_stopped_position"
    private val CONTINUES_PLAY_KEY = "continues_play_at_end"
    private val PIP_MODE_ON_LEAVE = "auto_pip_mode"
    private val SCAN_VIDEO_LENGHTTIME = "video_length_scantime"
    private val SCAN_MOVIE_LENGTHTIME = "movie_length_time"


    fun getVideoPlaylists(): List<videoPlaylist> {
        val playlistsJson = sharedPreferences.getString(PLAYLISTS_KEY, null)
        return if (playlistsJson.isNullOrEmpty()) {
            emptyList()
        } else {
            val typeToken = object : TypeToken<List<videoPlaylist>>() {}.type
            Gson().fromJson(playlistsJson, typeToken)
        }
    }

    fun saveVideoPlaylists(playlists: List<videoPlaylist>) {
        val playlistsJson = Gson().toJson(playlists)
        sharedPreferences.edit().putString(PLAYLISTS_KEY, playlistsJson).apply()
    }

    fun addVideosToPlaylist(playlistId: Long, videoIds: List<Long>,context: Context) {
        val playlists = getVideoPlaylists().toMutableList()

        val updatedPlaylists = playlists.map {
            if (it.id == playlistId) {
                val existingVideoIds = it.videoIds ?: emptyList()
                val newVideoIds = existingVideoIds.toMutableList()

                // Add only those video IDs that are not already present in the playlist
                videoIds.forEach { videoId ->
                    if (!existingVideoIds.contains(videoId)) {
                        newVideoIds.add(videoId)
                    }else{
                        Toast.makeText(context,"video already exists!",Toast.LENGTH_SHORT).show()
                    }
                }

                it.copy(videoIds = newVideoIds)
            } else {
                it
            }
        }
        saveVideoPlaylists(updatedPlaylists)
    }

    fun deleteVideosFromPlaylist(playlistId: Long, videoIdsToDelete: List<Long>) {
        val playlists = getVideoPlaylists().toMutableList()

        val updatedPlaylists = playlists.map { playlist ->
            if (playlist.id == playlistId) {
                val newVideoIds = playlist.videoIds.orEmpty().filterNot { id -> videoIdsToDelete.contains(id) }
                playlist.copy(videoIds = newVideoIds)
            } else {
                playlist
            }
        }
        saveVideoPlaylists(updatedPlaylists)
    }

    fun editVideoPlaylistName(playlistId: Long, newName: String) {
        val playlists = getVideoPlaylists().toMutableList()

        val updatedPlaylists = playlists.map {
            if (it.id == playlistId) {
                it.copy(name = newName)
            } else {
                it
            }
        }
        saveVideoPlaylists(updatedPlaylists)
    }

    fun getVideosByPlaylistId(playlistId: Long): List<Long> {
        val playlists = getVideoPlaylists()
        return playlists.firstOrNull { it.id == playlistId }?.videoIds.orEmpty()
    }

    fun deleteVideoPlaylist(playlistId: Long) {
        val playlists = getVideoPlaylists().toMutableList()
        val updatedPlaylists = playlists.filter { it.id != playlistId }
        saveVideoPlaylists(updatedPlaylists)
    }

    fun deleteVideoPlaylists(playlistIds: List<Long>) {
        val playlists = getVideoPlaylists().toMutableList()
        val updatedPlaylists = playlists.filterNot { playlistIds.contains(it.id) }
        saveVideoPlaylists(updatedPlaylists)
    }


    fun clearVideosFromPlaylist(playlistId: Long) {
        val playlists = getVideoPlaylists().toMutableList()

        val updatedPlaylists = playlists.map {
            if (it.id == playlistId) {
                it.copy(videoIds = emptyList())
            } else {
                it
            }
        }
        saveVideoPlaylists(updatedPlaylists)
    }

    fun setCurrentVideoSortType(sortType: VideoSortType) {
        sharedPreferences.edit {
            putString(SORT_TYPE_KEY, sortType.name)
            apply()
        }
    }

    fun getCurrentVideoSortType(): VideoSortType {
        val defaultSortType = VideoSortType.DATE_ADDED_DESC // Set default sort type here
        val sortTypeName = sharedPreferences.getString(SORT_TYPE_KEY, defaultSortType.name)
        return VideoSortType.valueOf(sortTypeName ?: defaultSortType.name)
    }


    // Function to save playback position to SharedPreferences
//    fun savePlaybackPosition(context: Context, videoId: String, position: Long) {
//        val savedPosition = getPlaybackPosition(videoId)
//        if (savedPosition == 0L) {
//            val editor = sharedPreferences.edit()
//            editor.putLong(videoId, position)
//            editor.apply()
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
//        } else {
//            // Video position already exists, update it
//            if (position > savedPosition) {
//                val editor = sharedPreferences.edit()
//                editor.putLong(videoId, position)
//                editor.apply()
//                Toast.makeText(context, "Position updated", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    // Function to retrieve playback position from SharedPreferences
//    fun getPlaybackPosition(videoId: String): Long {
//        return sharedPreferences.getLong(videoId, 0L)
//    }
    // Function to save playback position to SharedPreferences
    fun savePlaybackPosition(context: Context, videoId: String, position: Long) {
        val savedPosition = getPlaybackPosition("$PREF_PLAYBACK_POSITIONS$videoId") // Add prefix here
        if (savedPosition == 0L) {
            val editor = sharedPreferences.edit()
            editor.putLong("$PREF_PLAYBACK_POSITIONS$videoId", position) // Add prefix here
            editor.apply()
        } else {
            // Video position already exists, update it
            if (position > savedPosition) {
                val editor = sharedPreferences.edit()
                editor.putLong("$PREF_PLAYBACK_POSITIONS$videoId", position) // Add prefix here
                editor.apply()
            }
        }
    }

    // Function to retrieve playback position from SharedPreferences
    fun getPlaybackPosition(videoId: String): Long {
        return sharedPreferences.getLong("$PREF_PLAYBACK_POSITIONS$videoId", 0L) // Add prefix here
    }


    // Function to remove played video from SharedPreferences
    fun removePlayedVideo(videoId: String) {
        val editor = sharedPreferences.edit()
        editor.remove("$PREF_PLAYBACK_POSITIONS$videoId")
        editor.apply()
    }


    suspend fun removePositionsForMissingVideos(videoList: List<Video>) {
        val savedVideoIds = sharedPreferences.all.keys.filter { it.startsWith(PREF_PLAYBACK_POSITIONS) }
        savedVideoIds.forEach { key ->
            val videoId = key.removePrefix(PREF_PLAYBACK_POSITIONS)
            if (videoList.none { it.id.toString() == videoId }) {
                removePlayedVideo(videoId)
            }
        }
    }

    // Settings part
    fun setSeekForwardTime(seekTime:Long) {
        sharedPreferences.edit {
            putLong(VIDEO_SEEK_FORWARD_KEY, seekTime)
            apply()
        }
    }
    fun getSeekForwardTime():Long {
        return sharedPreferences.getLong(VIDEO_SEEK_FORWARD_KEY,10000L)
    }


    fun setScanVideoLengthTime(scanLength:Long) {
        sharedPreferences.edit {
            putLong(SCAN_VIDEO_LENGHTTIME, scanLength)
            apply()
        }
    }
    fun getScanVideoLengthTime():Long {
        return sharedPreferences.getLong(SCAN_VIDEO_LENGHTTIME,1L)
    }


    fun setScanMovieLengthTime(scanLength:Long) {
        sharedPreferences.edit {
            putLong(SCAN_MOVIE_LENGTHTIME, scanLength)
            apply()
        }
    }
    fun getScanMovieLengthTime():Long {
        return sharedPreferences.getLong(SCAN_MOVIE_LENGTHTIME,50L)
    }

    fun setDoubleTapSeekEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(DOUBLETAP_TO_SEEK_KEY, enabled)
            apply()
        }
    }

    fun getDoubleTapSeekEnabled(): Boolean {
        return sharedPreferences.getBoolean(DOUBLETAP_TO_SEEK_KEY, true)
    }


    fun setResumeFromLeftPositionEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(RESUME_KEY, enabled)
            apply()
        }
    }
    fun getResumeFromLeftPositionEnabled(): Boolean {
        return sharedPreferences.getBoolean(RESUME_KEY, true)
    }


    fun setContinuesPlayEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(CONTINUES_PLAY_KEY, enabled)
            apply()
        }
    }

    fun getContinuesPlayEnabled(): Boolean {
        return sharedPreferences.getBoolean(CONTINUES_PLAY_KEY, true)
    }

    fun setAutoPopupEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(PIP_MODE_ON_LEAVE, enabled)
            apply()
        }
    }

    fun getAutoPopupEnabled(): Boolean {
        return sharedPreferences.getBoolean(PIP_MODE_ON_LEAVE, false)
    }
}
