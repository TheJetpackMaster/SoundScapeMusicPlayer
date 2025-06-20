package com.SoundScapeApp.soundscape.SoundScapeApp.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.SoundScapeApp.soundscape.R


@UnstableApi
class NotificationAdapter(
    private val context: Context,
//    private val pendingIntent: PendingIntent?,
    private val notificationContentIntent: PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.title ?: "Unknown"

//    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return notificationContentIntent
    }

    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.artist ?: "Unknown"


    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        val artworkUri = player.mediaMetadata.artworkUri

        if (artworkUri != null) {
            Glide.with(context)
                .asBitmap()
                .load(artworkUri)
                .error(R.drawable.sample)
                .placeholder(R.drawable.sample)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                })
        } else {
            Glide.with(context)
                .asBitmap()
                .load(R.drawable.sample)
                .error(R.drawable.sample)
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                })
        }

        return null
    }
}