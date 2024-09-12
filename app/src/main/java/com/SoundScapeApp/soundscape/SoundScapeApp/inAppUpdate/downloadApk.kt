package com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate

import DownloadReceiver
import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun downloadApk(context: Context, url: String, version:String) {

    val downloadReceiver = DownloadReceiver()
    val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    context.registerReceiver(downloadReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)

    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Downloading Update")
        .setDescription("Downloading SoundScape_V${version}")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SoundScapePlayer${version}.apk")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)


    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)

}
