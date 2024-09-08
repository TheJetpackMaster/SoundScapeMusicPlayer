package com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File

fun downloadApk(context: Context, url: String,version:String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Downloading Update")
        .setDescription("Downloading the latest version of the app")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SoundScapePlayer${version}.apk")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
    

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}

fun downloadOrInstallApk(context: Context, url: String, version: String) {
    // Define the destination file path
    val destinationDir = File(context.getExternalFilesDir(null), "downloads")
    if (!destinationDir.exists()) {
        destinationDir.mkdirs()
    }
    val apkFile = File(destinationDir, "SoundScapePlayer_$version.apk")

    // Check if the APK file already exists
    if (apkFile.exists()) {
        // APK already exists, install it
        installApk(context, Uri.fromFile(apkFile))
    } else {
        // File doesn't exist, start the download
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Update")
            .setDescription("Downloading the latest version of the app")
            .setDestinationUri(Uri.fromFile(apkFile)) // Set the destination using the file
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}

// Function to install APK
fun installApk(context: Context, apkUri: Uri) {
    val installIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(apkUri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(installIntent)
}
