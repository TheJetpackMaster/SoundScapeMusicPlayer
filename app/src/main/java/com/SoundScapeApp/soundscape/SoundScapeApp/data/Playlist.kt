package com.SoundScapeApp.soundscape.SoundScapeApp.data


data class Playlist(
    val id: Long,
    val name: String,
    val songIds: List<Long>,
)

data class videoPlaylist(
    val id: Long,
    val name: String,
    val videoIds: List<Long>,
)
