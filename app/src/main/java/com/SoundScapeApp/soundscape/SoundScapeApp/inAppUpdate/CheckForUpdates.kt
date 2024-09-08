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
import androidx.compose.material3.LinearProgressIndicator
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

@Composable
fun DownloadProgressDialog(progress: Int) {
    androidx.compose.material.AlertDialog(
        onDismissRequest = { /* Optionally handle dismiss */ },
        title = { Text("Downloading Update") },
        text = {
            Column {
                Text("Downloading the latest version of the app...")
                LinearProgressIndicator(
                    progress = { progress / 100f },
                ) // Assuming progress is a percentage
            }
        },
        buttons = {
            Button(onClick = { /* Optionally handle cancel */ }) {
                Text("Cancel")
            }
        }
    )
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}


//@Composable
//fun CheckForUpdates() {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    var latestVersion by remember { mutableStateOf<String?>(null) }
//    var latestUrl by remember { mutableStateOf<String?>(null) }
//    val currentVersion = "1.3.1"
//
//    var isDismissed by remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        val (version, url) = getLatestVersionAndUrl("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")
//        latestVersion = version
//
//        latestUrl = url
//        Log.d("UpdateInfo", "Version: $latestVersion, URL: $latestUrl")
//    }
//
//    if (latestVersion != null && latestVersion!! > currentVersion && !isDismissed) {
//        AlertDialog(
//            onDismissRequest = { isDismissed = true },
//            title = { Text("Update Available") },
//            text = { Text("A new version of the app is available. Would you like to update? Version 1.3.2") },
//            confirmButton = {
//                Button(onClick = {
//                    latestUrl?.let { downloadApk(context, it,latestVersion?:"") }
//                }) {
//                    Text("Update")
//                }
//            },
//            dismissButton = {
//                Button(onClick = {
//                    isDismissed = true
//                }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}


//REPO ACCESS TOKEN
//TOKEN = "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"