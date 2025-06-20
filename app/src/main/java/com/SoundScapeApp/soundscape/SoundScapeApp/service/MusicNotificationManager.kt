package com.SoundScapeApp.soundscape.SoundScapeApp.service

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.SoundScapeApp.soundscape.MainActivity
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.AudioSharedPreferencesHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


private const val NOTIFICATION_ID = 101
private const val NOTIFICATION_CHANNEL_name = "musicNotification"
private const val NOTIFICATION_CHANNEL_ID = "musicNotification id 1"

class MusicNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer,
    private val audioSharedPreferencesHelper: AudioSharedPreferencesHelper

    ) {
    val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    private val contentIntent = PendingIntent.getActivity(
        context,
        0,
        getMainActivityIntent(context),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    init {
        createNotificationChannel()
    }


    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildNotification(mediaSession, mediaSessionService)
        startForeGroundNotificationService(mediaSessionService)
    }

    private fun startForeGroundNotificationService(mediaSessionService: MediaSessionService) {
        val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    @OptIn(UnstableApi::class)
    private fun buildNotification(
        mediaSession: MediaSession,
        mediaSessionService: MediaSessionService
    ) {
        PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID
        )
            .setCustomActionReceiver(MyCustomActionReceiver(mediaSessionService))
            .setMediaDescriptionAdapter(
                NotificationAdapter(
                    context = context,
                    contentIntent
                )
            )
            .setSmallIconResourceId(
                R.drawable.notificationappicon
            )
            .setChannelImportance(NotificationUtil.IMPORTANCE_HIGH)
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if(!exoPlayer.isPlaying){
                        notificationManager.cancel(notificationId)
                    }else{
                        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
                    }

                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    if (dismissedByUser) {
                        if (!exoPlayer.isPlaying && !isAppInForeground(context)) {
                            notificationManager.cancel(NOTIFICATION_ID)
                            mediaSessionService.stopSelf()
                            mediaSessionService.stopForeground(notificationId)
                        }
                    }
                }
            })
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseNextActionInCompactView(true)
                it.setUsePreviousActionInCompactView(true)
                it.setUseFastForwardAction(false)
//                it.setUseStopAction(true)
                it.setUseRewindAction(false)
                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setPlayer(exoPlayer)
            }
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_name,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val packageName = context.packageName
        val appProcesses = activityManager?.runningAppProcesses
        if (appProcesses != null) {
            for (appProcess in appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName == packageName
                ) {
                    return true
                }
            }
        }
        return false
    }

    private fun getMainActivityIntent(context: Context): Intent {
        val intent = Intent(context, MainActivity::class.java)
        if (isAppInForeground(context)) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return intent
    }


    @UnstableApi
    inner class MyCustomActionReceiver(private val mediaSessionService: MediaSessionService) :
        PlayerNotificationManager.CustomActionReceiver {

        private val stopIntent = Intent(context, MusicService::class.java).apply {
            action = "STOP_MUSIC_SERVICE" // Custom action for stopping the service
        }

        private val stopPendingIntent = PendingIntent.getService(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        override fun createCustomActions(
            context: Context,
            instanceId: Int
        ): MutableMap<String, NotificationCompat.Action> {
            val customActions = mutableMapOf<String, NotificationCompat.Action>()

            // Add your custom action here
            val stopAction = NotificationCompat.Action.Builder(
                R.drawable.baseline_close_24,
                "stop_app",
                stopPendingIntent
            ).build()
            customActions["stop_app"] = stopAction // Adjusted action key

            return customActions
        }

        override fun getCustomActions(player: Player): MutableList<String> {
            // Return a list of custom action keys
            return mutableListOf("stop_app") // Adjusted action key
        }

        override fun onCustomAction(player: Player, action: String, intent: Intent) {
            if (action == "stop_app") {

                // Handle stop action her
                // Remove the notification
                notificationManager.cancel(NOTIFICATION_ID)
                notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)

            }
        }
    }
}