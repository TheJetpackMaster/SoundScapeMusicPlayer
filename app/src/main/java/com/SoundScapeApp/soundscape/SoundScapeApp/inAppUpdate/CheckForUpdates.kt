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
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import getLatestVersionAndUrl
import kotlinx.coroutines.launch
//
//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@Composable
//fun CheckForUpdates() {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    var latestVersion by remember { mutableStateOf<String?>(null) }
//    var latestUrl by remember { mutableStateOf<String?>(null) }
//    val currentVersion = getAppVersion(context)
//    Log.d("current version", currentVersion)
//
//    var isDismissed by remember { mutableStateOf(false) }
//    var isDownloading by remember { mutableStateOf(false) }
//    var downloadProgress by remember { mutableStateOf(0) }
//
//
//    LaunchedEffect(Unit) {
//        val (version, url) = getLatestVersionAndUrl("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"
//        ) {
//
//        }
//        latestVersion = version
//        latestUrl = url
//        Log.d("UpdateInfo version", "Version: $latestVersion, URL: $latestUrl")
//    }
//
//    if (latestVersion != null && latestVersion!! > currentVersion && !isDismissed && !isDownloading) {
//        AlertDialog(
//            onDismissRequest = { isDismissed = true },
//            title = { Text("Update Available") },
//            text = { Text("A new version of the app is available. Would you like to update? Version $latestVersion") },
//            confirmButton = {
//                Button(onClick = {
//                    latestUrl?.let {
//                        isDownloading = true
//                        downloadApk(context, it, latestVersion!!)
//                    }
//                }) {
//                    Text("Update")
//                }
//            },
//            dismissButton = {
//                Button(onClick = { isDismissed = true }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CheckForUpdates() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var latestVersion by remember { mutableStateOf<String?>(null) }
    var latestUrl by remember { mutableStateOf<String?>(null) }
    val currentVersion = getAppVersion(context)
    Log.d("current version", currentVersion)

    var isDismissed by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val (version, url) = getLatestVersionAndUrl("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"){

        }
        latestVersion = version
        latestUrl = url
        Log.d("UpdateInfo version", "Version: $latestVersion, URL: $latestUrl")

        // Save the latest version if it's different from the last shown version
        if (latestVersion != null && latestVersion != getLastShownVersion(context)) {
            saveLastShownVersion(context, latestVersion!!)
            saveUpdateDismissed(context, false) // Reset the dismissal status
            saveDismissCount(context, 0) // Reset the dismissal count
        }
    }

    val dismissCount = getDismissCount(context)
    val shouldShowUpdateDialog = latestVersion != null &&
            latestVersion!! > currentVersion &&
            !isDismissed &&
            !isDownloading &&
            dismissCount < 2

    if (shouldShowUpdateDialog) {
        AlertDialog(
            onDismissRequest = {
                isDismissed = true
                saveUpdateDismissed(context, true) // Save the dismissal
                val newDismissCount = dismissCount + 1
                saveDismissCount(context, newDismissCount) // Increment the dismissal count

                // Show a toast message if the dismissal count is 2
                if (newDismissCount == 2) {
                    Toast.makeText(context, "You can update the app from Settings.", Toast.LENGTH_LONG).show()
                }
            },
            title = { Text("Update Available") },
            text = { Text("A new version of the app is available. Would you like to update? Version $latestVersion") },
            confirmButton = {
                Button(onClick = {
                    latestUrl?.let {
                        isDownloading = true
                        downloadApk(context, it, latestVersion!!)
                        saveUpdateDismissed(context, false) // Reset the dismissal status on download
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                Button(onClick = {
                    isDismissed = true
                    saveUpdateDismissed(context, true) // Save the dismissal
                    val newDismissCount = dismissCount + 1
                    saveDismissCount(context, newDismissCount) // Increment the dismissal count

                    // Show a toast message if the dismissal count is 2
                    if (newDismissCount == 2) {
                        Toast.makeText(context, "You can update the app from Settings anytime.", Toast.LENGTH_LONG).show()
                    }
                }) {
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


private const val PREFS_NAME = "update_prefs"
private const val KEY_LAST_SHOWN_VERSION = "last_shown_version"
private const val KEY_UPDATE_DISMISSED = "update_dismissed"

private const val KEY_DISMISS_COUNT = "dismiss_count"

fun saveDismissCount(context: Context, count: Int) {
    val prefs = getSharedPreferences(context)
    prefs.edit().putInt(KEY_DISMISS_COUNT, count).apply()
}

fun getDismissCount(context: Context): Int {
    val prefs = getSharedPreferences(context)
    return prefs.getInt(KEY_DISMISS_COUNT, 0)
}

private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

fun saveLastShownVersion(context: Context, version: String) {
    val prefs = getSharedPreferences(context)
    prefs.edit().putString(KEY_LAST_SHOWN_VERSION, version).apply()
}

fun getLastShownVersion(context: Context): String? {
    val prefs = getSharedPreferences(context)
    return prefs.getString(KEY_LAST_SHOWN_VERSION, null)
}

fun saveUpdateDismissed(context: Context, dismissed: Boolean) {
    val prefs = getSharedPreferences(context)
    prefs.edit().putBoolean(KEY_UPDATE_DISMISSED, dismissed).apply()
}

//REPO ACCESS TOKEN
//TOKEN = "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"