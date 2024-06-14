package com.SoundScapeApp.soundscape.SoundScapeApp.data

data class PlaybackState(
    val lastPlayedSong:String,
    val lastPlaybackPosition:Long,
    val isPlaying:Boolean
)