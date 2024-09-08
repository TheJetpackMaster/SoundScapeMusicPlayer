package com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.AppUpdate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate.CheckForUpdates
import com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate.downloadApk
import com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate.getAppVersion
import com.SoundScapeApp.soundscape.ui.theme.White50
import getLatestVersionAndUrl
import kotlinx.coroutines.launch

@Composable
fun AppUpdateInfo() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isAvailable by remember{ mutableStateOf(false) }
    var latestVersion by remember { mutableStateOf<String?>(null) }
    var latestUrl by remember { mutableStateOf<String?>(null) }
    val currentVersion = getAppVersion(context)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White50),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text("Your app version ${currentVersion}", color = Color.Black)

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = {
            scope.launch {
                val (version, url) = getLatestVersionAndUrl("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")
                latestVersion = version
                latestUrl = url
            }

        }) {
            Text("Check for updates", color = Color.Black)
        }

        Spacer(Modifier.height(12.dp))

        if((latestVersion ?: "") > currentVersion){
            Text("New update is available :${latestVersion}")

            Button(onClick = {
                latestUrl?.let {
                    downloadApk(context, it, latestVersion!!)
                }
            }) {
                Text("Download")
            }
        }
    }
}