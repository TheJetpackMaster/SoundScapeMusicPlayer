package com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import getLatestVersionAndUrl
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CheckForUpdates() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var latestVersion by remember { mutableStateOf<String?>(null) }
    var latestUrl by remember { mutableStateOf<String?>(null) }
    val currentVersion = getAppVersion(context)
    Log.d("current version",currentVersion)

    var isDismissed by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0) }


    LaunchedEffect(Unit) {
        val (version, url) = getLatestVersionAndUrl("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")
        latestVersion = version
        latestUrl = url
        Log.d("UpdateInfo version", "Version: $latestVersion, URL: $latestUrl")
    }

    if (latestVersion != null && latestVersion!! > currentVersion && !isDismissed && !isDownloading) {
        AlertDialog(
            onDismissRequest = { isDismissed = true },
            title = { Text("Update Available") },
            text = { Text("A new version of the app is available. Would you like to update? Version $latestVersion") },
            confirmButton = {
                Button(onClick = {
                    latestUrl?.let {
                        isDownloading = true
                        downloadApk(context, it, latestVersion!!)
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                Button(onClick = { isDismissed = true }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}



//REPO ACCESS TOKEN
//TOKEN = "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"