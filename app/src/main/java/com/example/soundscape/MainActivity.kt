package com.example.soundscape

import android.Manifest
import android.Manifest.permission
import android.app.ActivityManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.compose.rememberNavController
import com.example.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.example.soundscape.SoundScapeApp.MainViewModel.UIEvents
import com.example.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.example.soundscape.SoundScapeApp.ui.permissions.AudioPermissionTextProvider
import com.example.soundscape.SoundScapeApp.ui.permissions.ExternalStoragePermissionTextProvider
import com.example.soundscape.SoundScapeApp.ui.permissions.PermissionDialog
import com.example.soundscape.SoundScapeApp.ui.permissions.VideoPermissionTextProvider
import com.example.soundscape.SoundScapeApp.service.MusicService
import com.example.soundscape.SoundScapeApp.ui.BottomNavigation.routes.ScreenRoute
import com.example.soundscape.SoundScapeApp.ui.RootNav.RootNav
import com.example.soundscape.SoundScapeApp.ui.permissions.openAppSettings
import com.example.soundscape.ui.theme.SoundScapeThemes
import dagger.hilt.android.AndroidEntryPoint
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionsToRequest = arrayOf(
        permission.READ_MEDIA_VIDEO,
        permission.READ_MEDIA_AUDIO
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        super.onCreate(savedInstanceState)
        setContent {
            val currentTheme by audioViewModel.currentTheme.collectAsState()

            SoundScapeThemes(
                currentTheme
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
                                        audioViewModel.setMediaItems(audioList)
                                        audioViewModel.play(audioList.indexOf(selectedAudio))
                                        audioViewModel.setMediaItemFlag(true)
                                        player.repeatMode = Player.REPEAT_MODE_ALL

                                    } else if (audioViewModel.currentSelectedAudio != id) {
                                        audioViewModel.play(audioList.indexOf(selectedAudio))
                                    }
                                } else {
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
                        onVideoItemClick = { _, id ->
                            val selectedVideo = videoList.firstOrNull { it.id == id }
                            selectedVideo?.let {
                                videoViewModel.setVideoMediaItemAndPlay(selectedVideo)
                            }
                        }
                    )
                }
            }
        }
    }

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
    }

//    @Deprecated("Deprecated in Java")
//    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
//        if (!isInPictureInPictureMode) {
//            audioViewModel.exoPlayer.pause()
//        }
//        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)
//    }

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
    }

    override fun onResume() {
        super.onResume()
        videoViewModel.setPipModeEnabled(isInPictureInPictureMode)
    }

}
