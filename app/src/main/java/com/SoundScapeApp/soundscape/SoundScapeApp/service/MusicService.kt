package com.SoundScapeApp.soundscape.SoundScapeApp.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.AudioSharedPreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MusicService : MediaSessionService() {

    @Inject
    lateinit var mediaSession: MediaSession

    @Inject
    lateinit var notificationManager: MusicNotificationManager

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var audioSharedPreferencesHelper: AudioSharedPreferencesHelper


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_MUSIC_SERVICE") {
            // Stop the service

            stopSelf()
            stopForeground(true)
            return START_NOT_STICKY

        } else {
//            if (exoPlayer.isPlaying) {
//                return START_STICKY
//            } else {
//                if (!exoPlayer.playWhenReady) {
//                    exoPlayer.prepare()
//                }
            notificationManager.startNotificationService(
                mediaSession = mediaSession,
                mediaSessionService = this
            )
//            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession


    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
//        mediaSession.release()
        exoPlayer.currentMediaItem?.let { mediaItem ->
            exoPlayer.pause()
            audioSharedPreferencesHelper.savePlaybackState(
                mediaItem.mediaId,
                exoPlayer.currentPosition,
                exoPlayer.isPlaying
            )
        }

//        val intent = Intent("com.SoundScapeApp.soundscape.FINISH_ACTIVITY")
//        sendBroadcast(intent)

//        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        activityManager.runningAppProcesses?.forEach { processInfo ->
//            if (processInfo.processName == context.packageName) {
//                android.os.Process.killProcess(processInfo.pid)
//                exitProcess(0)
//            }
//        }
    }
}

