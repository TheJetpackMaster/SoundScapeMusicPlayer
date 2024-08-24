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
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.platform.LocalContext
import getLatestVersionAndUrl
import kotlinx.coroutines.launch

@Composable
fun CheckForUpdates() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var latestVersion by remember { mutableStateOf<String?>(null) }
    var latestUrl by remember { mutableStateOf<String?>(null) }
    val currentVersion = "1.3.1" // Example current version

    LaunchedEffect(Unit) {
        val (version, url) = getLatestVersionAndUrl("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")
        latestVersion = version
        latestUrl = url
        Log.d("UpdateInfo", "Version: $latestVersion, URL: $latestUrl")
    }

    if (latestVersion != null && latestVersion!! > currentVersion) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Update Available") },
            text = { Text("A new version of the app is available. Would you like to update?") },
            confirmButton = {
                Button(onClick = {
                    latestUrl?.let { downloadApk(context, it) }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                Button(onClick = { /* handle dismiss */ }) {
                    Text("Cancel")
                }
            }
        )
    }
}