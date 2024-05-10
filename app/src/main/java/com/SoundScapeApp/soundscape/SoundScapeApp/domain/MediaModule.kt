package com.SoundScapeApp.soundscape.SoundScapeApp.domain

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.MusicServiceHandler
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.SharedPreferencesHelper
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.VideoPlaylistManager
import com.SoundScapeApp.soundscape.SoundScapeApp.service.MusicNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Provides
    @Singleton
    fun provideExoPlayer(
        context: Context,
        audioAttributes: AudioAttributes,
    ): ExoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(context))
            .build()


    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): MediaSession = MediaSession.Builder(context, player)
        .setId("audioSession")
        .build()

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        player: ExoPlayer,
        sharedPreferencesHelper: SharedPreferencesHelper
    ): MusicNotificationManager = MusicNotificationManager(
        context = context,
        exoPlayer = player,
        sharedPreferencesHelper = sharedPreferencesHelper
    )

    @Suppress("DEPRECATION")
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideServiceHandler(exoPlayer: ExoPlayer): MusicServiceHandler = MusicServiceHandler(
        exoPlayer
    )

    @Provides
    @Singleton
    fun provideVideoPlaylistManager(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences
    ): VideoPlaylistManager {
        return VideoPlaylistManager(context, sharedPreferences)
    }


//    @Provides
//    @Singleton
//    fun provideVideoServiceHandler(@ApplicationContext context: Context,exoPlayer: ExoPlayer): VideoServiceHandler = VideoServiceHandler(
//        context,
////        exoPlayer
//    )


    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesCoroutineScope():CoroutineScope{
        return CoroutineScope(Dispatchers.IO)
    }
}