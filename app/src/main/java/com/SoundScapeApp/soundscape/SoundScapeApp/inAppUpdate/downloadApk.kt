package com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

fun downloadApk(context: Context, url: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Downloading Update")
        .setDescription("Downloading the latest version of the app")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SoundScapePlayer.apk")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}