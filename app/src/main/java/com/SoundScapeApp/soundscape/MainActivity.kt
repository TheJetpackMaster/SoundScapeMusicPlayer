package com.SoundScapeApp.soundscape

import android.Manifest
import android.Manifest.permission
import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PictureInPictureParams
import android.app.RecoverableSecurityException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.compose.rememberNavController
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.UIEvents
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses.SharedPreferencesHelper
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.permissions.AudioPermissionTextProvider
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.permissions.ExternalStoragePermissionTextProvider
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.permissions.PermissionDialog
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.permissions.VideoPermissionTextProvider
import com.SoundScapeApp.soundscape.SoundScapeApp.service.MusicService
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.RootNav.RootNav
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.permissions.openAppSettings
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val audioViewModel: AudioViewModel by viewModels()
    private val videoViewModel: VideoViewModel by viewModels()


    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var mediaSession: MediaSession

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionsToRequest = arrayOf(
        permission.READ_MEDIA_VIDEO,
        permission.READ_MEDIA_AUDIO
    )


    //For deleting songs and videos
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        // DELETING MEDIA ITEMS
        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {

                    if (audioViewModel.isDeletingSong.value) {

//                        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
//                            lifecycleScope.launch {
//                                deleteSongFromExternalStorage()
//                            }
//                        }
                        Toast.makeText(
                            this,
                            "${audioViewModel.selectedSongs.value.size} songs Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        audioViewModel.setMediaItemFlag(false)
                        audioViewModel.setIsDeletingSongs(false)

                        val songs = audioViewModel.scannedAudioList.value
                        val selectedSongs = audioViewModel.selectedSongs.value
                        val sortedSelectedSongs = selectedSongs.sortedDescending()
                        Log.d("seled",selectedSongs.toString())

                        sortedSelectedSongs.asReversed().forEach { deletedSongId ->
                            val index = songs.indexOfFirst { it.id == deletedSongId }
                            if (index != -1) {
                                player.removeMediaItem(index)
                            }
                        }

                        audioViewModel.reloadSongs(audioViewModel.selectedSongs.value)
                        audioViewModel.setSelectedSongs(emptyList())



                    } else {
                        Toast.makeText(
                            this,
                            "${videoViewModel.selectedVideos.value.size} videos Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
//                    videoViewModel.setMediaItemFlag(false)
                        videoViewModel.reloadVideos(videoViewModel.selectedVideos.value)
                        videoViewModel.setSelectedVideos(emptyList())
                    }

                } else {
                    if (audioViewModel.isDeletingSong.value) {
                        audioViewModel.setSelectedSongs(emptyList())
                        audioViewModel.setIsDeletingSongs(false)
                    } else {
                        videoViewModel.setSelectedVideos(emptyList())
                    }
                }
            }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        setContent {
            val currentTheme by audioViewModel.currentTheme.collectAsState()

            SoundScapeThemes(
                currentTheme,
                this
            ) {

                val dialogQueue = audioViewModel.visiblePermissionDialogQueue

                val externalPermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        audioViewModel.onPermissionResult(
                            permission = permission.READ_EXTERNAL_STORAGE,
                            isGranted = isGranted
                        )
                    }
                )
                val externalWritePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        audioViewModel.onPermissionResult(
                            permission = permission.WRITE_EXTERNAL_STORAGE,
                            isGranted = isGranted
                        )
                    }
                )

                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { permissions ->
                        permissionsToRequest.forEach { permission ->
                            audioViewModel.onPermissionResult(
                                permission = permission,
                                isGranted = permissions[permission] == true
                            )
                        }
                    }
                )

                var lifecycle by remember {
                    mutableStateOf(Lifecycle.Event.ON_CREATE)
                }

                val lifeCycleOwner = LocalLifecycleOwner.current

                DisposableEffect(key1 = lifeCycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        lifecycle = event

                        if (event == Lifecycle.Event.ON_CREATE) {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                                multiplePermissionResultLauncher.launch(permissionsToRequest)
                            } else {
                                externalPermissionResultLauncher.launch(
                                    permission.READ_EXTERNAL_STORAGE
                                )
                                externalWritePermissionResultLauncher.launch(
                                    permission.WRITE_EXTERNAL_STORAGE
                                )

                            }
                        } else if (event == Lifecycle.Event.ON_RESUME) {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                                if (ContextCompat.checkSelfPermission(
                                        this@MainActivity,
                                        permission.READ_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    audioViewModel.loadAudioData()
                                    videoViewModel.loadVideoData()
                                }
                            } else {
                                // For Android 12 (API level 31) and above
                                val audioPermissionGranted = ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    permission.READ_MEDIA_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                val videoPermissionGranted = ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    permission.READ_MEDIA_VIDEO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (audioPermissionGranted && videoPermissionGranted) {
                                    // Both audio and video permissions are granted
                                    audioViewModel.loadAudioData()
                                    videoViewModel.loadVideoData()
                                } else if (audioPermissionGranted) {
                                    // Only audio permission is granted
                                    audioViewModel.loadAudioData()
                                } else if (videoPermissionGranted) {
                                    // Only video permission is granted
                                    videoViewModel.loadVideoData()
                                } else {
                                    // Neither audio nor video permissions are granted
                                    // Handle this case, maybe request permissions from the user
                                }
                            }
                        }
                    }
                    lifeCycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifeCycleOwner.lifecycle.removeObserver(observer)
                    }
                }


                dialogQueue.reversed()
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                Manifest.permission.READ_MEDIA_AUDIO -> {
                                    AudioPermissionTextProvider()
                                }

                                Manifest.permission.READ_MEDIA_VIDEO -> {
                                    VideoPermissionTextProvider()
                                }

                                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                                    ExternalStoragePermissionTextProvider()
                                }

                                else -> return@forEach
                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = audioViewModel::dismissDialog,
                            onOkClick = {
                                audioViewModel::dismissDialog
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                                    multiplePermissionResultLauncher.launch(
                                        arrayOf(
                                            permission
                                        )
                                    )
                                } else {
                                    externalPermissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            },
                            onGoToAppSettingsClick = {
                                this.openAppSettings()
                                audioViewModel::dismissDialog
                            }
                        )
                    }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val audioList by audioViewModel.scannedAudioList.collectAsState()
                    val videoList by videoViewModel.scannedVideoList.collectAsState()


                    RootNav(
                        navController = navController,
                        context = this,
                        onProgress = {
                            audioViewModel.onProgressSeek(it)
                        },
                        isAudioPlaying = audioViewModel.isPlying,
                        currentPlayingAudio = audioViewModel.currentSelectedAudio,
                        audioList = audioList,
                        onStart = {
                            startService()
                            audioViewModel.onUiEvents(UIEvents.PlayPause)
                        },
                        onItemClick = { _, id ->
                            val selectedAudio = audioList.firstOrNull { it.id == id }
                            selectedAudio?.let {
                                if (!audioViewModel.isSearch.value) {
                                    if (!audioViewModel.setMediaItems) {
                                        audioViewModel.setCurrentPlayingSection(1)
                                        audioViewModel.setMediaItems(audioList, this)
                                        audioViewModel.play(audioList.indexOf(selectedAudio))
                                        audioViewModel.setMediaItemFlag(true)
                                        player.repeatMode = Player.REPEAT_MODE_ALL

                                    } else if (audioViewModel.currentSelectedAudio != id) {
                                        audioViewModel.play(audioList.indexOf(selectedAudio))
                                    }
                                } else {
                                    audioViewModel.setCurrentPlayingSection(0)
                                    audioViewModel.setSingleMediaItem(selectedAudio)
                                    player.repeatMode = Player.REPEAT_MODE_ALL
                                    audioViewModel.setMediaItemFlag(false)
                                }
                            }
                            startService()
                            audioViewModel.onUiEvents(UIEvents.SelectedAudioChange(id))
                        },
                        onClick = { navController.navigate(ScreenRoute.NowPlayingScreen.route) },
                        onNext = { audioViewModel.onUiEvents(UIEvents.SeekToNext) },
                        onPrevious = { audioViewModel.onUiEvents(UIEvents.SeekPrevious) },
                        player = player,
                        audioViewModel = audioViewModel,
                        videoViewModel = videoViewModel,
                        mediaSession = mediaSession,
                        onPipClick = {
                            enterPiPMode()
                        },
                        onDeleteSong = { uri ->
                            lifecycleScope.launch {
                                deleteSongFromExternalStorage(uri)
                            }
                            audioViewModel.setIsDeletingSongs(true)
                        },
                        onVideoItemClick = { _, id ->
                            val selectedVideo = videoList.firstOrNull { it.id == id }
                            selectedVideo?.let {
                                videoViewModel.setVideoMediaItemAndPlay(selectedVideo)
                            }
                        },
                        onVideoDelete = { videoUri ->
                            lifecycleScope.launch {
                                deleteSongFromExternalStorage(videoUri)
                            }
                        }
                    )
                }
            }
        }
    }

//    private val finishActivityReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            // Finish the activity when the broadcast is received
//            finish()
//        }
//    }

    private fun isMediaSessionServiceRunning(context: Context): Boolean {
        val serviceClass = MusicService::class.java
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun startService() {
        val intent = Intent(this, MusicService::class.java)
        if (!isMediaSessionServiceRunning(this)) {
            startForegroundService(intent)
        } else {
//            startService(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun enterPiPMode() {
        val aspectRatio = Rational(16, 9)
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .setSeamlessResizeEnabled(true)
            .build()
        this.enterPictureInPictureMode(params)
        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)
        videoViewModel.createVideoMediaSession(this)

        if (isInPictureInPictureMode) {
            stopService(Intent(this, MusicService::class.java))

            // Cancel the notification
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(101)
        }
    }


    private suspend fun deleteSongFromExternalStorage(songUri: List<Uri>) {
        withContext(Dispatchers.IO) {
            try {
                songUri.forEach {
                    contentResolver.delete(it, null, null)
                }

                if (audioViewModel.isDeletingSong.value) {
                    audioViewModel.setMediaItemFlag(false)

//                    val songs = audioViewModel.scannedAudioList.value
//                    val selectedSongs = audioViewModel.selectedSongs.value
//                    val sortedSelectedSongs = selectedSongs.sortedDescending()
//                    Log.d("seled",selectedSongs.toString())
//
//                    sortedSelectedSongs.asReversed().forEach { deletedSongId ->
//                        val index = songs.indexOfFirst { it.id == deletedSongId }
//                        if (index != -1) {
//                            player.removeMediaItem(index)
//                        }
//                    }
//
//                    audioViewModel.reloadSongs(audioViewModel.selectedSongs.value)
//                    audioViewModel.setSelectedSongs(emptyList())

                    audioViewModel.reloadSongs(audioViewModel.selectedSongs.value)
                    audioViewModel.setSelectedSongs(emptyList())
                    audioViewModel.setIsDeletingSongs(false)


                } else {
//                    videoViewModel.setMediaItemFlag(false)
                    videoViewModel.reloadVideos(videoViewModel.selectedVideos.value)
                    videoViewModel.setSelectedVideos(emptyList())
                }

            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(contentResolver, songUri).intentSender

                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverAbleSecurityException = e as? RecoverableSecurityException
                        recoverAbleSecurityException?.userAction?.actionIntent?.intentSender
                    }

                    else -> null
                }
                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (!isInPictureInPictureMode) {
            videoViewModel.exoPlayer.pause()
        }
        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (videoViewModel.autoPopupEnabled.value && videoViewModel.exoPlayer.playWhenReady) {
            enterPiPMode()
        }
    }

    //
    override fun onPause() {
        super.onPause()
        if (videoViewModel.exoPlayer.playWhenReady) {
            val currentMediaId = videoViewModel.exoPlayer.currentMediaItem!!.mediaId
            val currentMediaPosition = videoViewModel.exoPlayer.currentPosition
            videoViewModel.savePlaybackPosition(
                this,
                currentMediaId,
                currentMediaPosition
            )
        }
        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)

        if(!videoViewModel.isPipModeEnabled.value){
            videoViewModel.destroyVideoMediaSession()
        }
    }


    override fun onResume() {
        super.onResume()
        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)
        if(!videoViewModel.isPipModeEnabled.value){
           videoViewModel.destroyVideoMediaSession()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(finishActivityReceiver)

        if(player.currentMediaItem != null) {
            sharedPreferencesHelper.savePlaybackState(
                player.currentMediaItem!!.mediaId,
                player.currentPosition,
                player.isPlaying
            )
        }

    }
}
