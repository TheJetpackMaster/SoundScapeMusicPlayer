@file:Suppress("DEPRECATION")

package com.SoundScapeApp.soundscape.SoundScapeApp.ui.MainScreen


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.data.Audio
import com.SoundScapeApp.soundscape.SoundScapeApp.inAppUpdate.CheckForUpdates
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.AllSongsHome.MainDrawerMenu
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.customBottomNavigation.CustomBottomNav
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.navGraph.BottomNavGraph
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.navGraph.RootNav
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.NavigationBarColor
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import okhttp3.Route
@SuppressLint("NewApi")
@OptIn(ExperimentalGlideComposeApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MainScreen(
    context: Context,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Long,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int, Long) -> Unit,
    onClick: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    player: ExoPlayer,
    audioViewModel: AudioViewModel,
    videoViewModel: VideoViewModel,
    mediaSession: MediaSession,
    onPipClick: () -> Unit,
    onVideoItemClick: (Int, Long) -> Unit,
    onDeleteSong: (List<Uri>) -> Unit,
    onVideoDelete: (List<Uri>) -> Unit,
    notificationData: String
) {

    val navController = rememberNavController()


    // Permissions Logic

    val activity = context as? Activity
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    var isPermissionsGranted by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        // After each permission result, re-check if all are granted
        isPermissionsGranted = hasPermissions(context, permissions)
    }

    // Check current back stack entry
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value

    // Initial permission check
    LaunchedEffect(currentBackStackEntry?.destination) {
        if (currentBackStackEntry?.destination?.route == BottomNavScreenRoutes.SongsHome.route) {
            isPermissionsGranted = hasPermissions(context, permissions)
            if (!isPermissionsGranted) {
                // Launch permission request if not granted
                permissionLauncher.launch(permissions)
            }
        }
    }


    // Observe the lifecycle to check for permission
    val lifecycleOwner = remember { context as LifecycleOwner }

    DisposableEffect(lifecycleOwner) {

        val onResume = {
            // Check if permissions are granted when resuming
            isPermissionsGranted = hasPermissions(context, permissions)
        }
        // Create a LifecycleObserver
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                onResume()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer) // Clean up observer
        }
    }


    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = false
    )

    systemUiController.setNavigationBarColor(
        color = Color.Transparent,
        darkIcons = false
    )

    val screens = listOf(
        BottomNavScreenRoutes.SongsHome,
        BottomNavScreenRoutes.VideosHome,
        BottomNavScreenRoutes.Settings
    )
    val showBottomBar = navController
        .currentBackStackEntryAsState().value?.destination?.route in screens.map { it.route } && isPermissionsGranted


    LaunchedEffect(notificationData) {
        if (notificationData == "update") {
            navController.navigate(ScreenRoute.UpdateApp.route)
        }
    }

    CheckForUpdates()

    Scaffold(
        modifier = Modifier
            .background(color = NavigationBarColor)
            .navigationBarsPadding(),
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                CustomBottomNav(
                    navController = navController,
                    context,
                    viewModel = audioViewModel
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
        ) {


//            val blurredBitmap = BlurHelper.blur(context, drawableResId = R.drawable.naturesbg, 25f)
//            Image(
//                bitmap = blurredBitmap.asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.FillBounds,
//            )

            val brushGradient = Brush.linearGradient(
                colors = listOf(
                    SoundScapeThemes.colorScheme.primary.copy(.6f),
                    SoundScapeThemes.colorScheme.secondary.copy(.6f),
                    SoundScapeThemes.colorScheme.primary.copy(.6f)


                ),
                start = Offset(0f, 0f),
                end = Offset.Infinite
            )
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//                val blurredBitmap =
//                    BlurHelper.blur(context, drawableResId = R.drawable.themebackground, 25f)
//                Image(
//                    bitmap = blurredBitmap.asImageBitmap(),
//                    contentDescription = "Background image",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.FillBounds,
//                )
//            } else {
////
            GlideImage(
                model = R.drawable.themebackground,
                contentDescription = "Background Image",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = brushGradient
                    )
                    .padding(bottom = it.calculateBottomPadding())
            ) {
                Box {
                    BottomNavGraph(
                        navController = navController,
                        context = context,
                        onProgress = onProgress,
                        audioList = audioList,
                        onStart = onStart,
                        onItemClick = onItemClick,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        player = player,
                        audioViewModel = audioViewModel,
                        videoViewModel = videoViewModel,
                        onPipClick = onPipClick,
                        onVideoItemClick = onVideoItemClick,
                        mediaSession = mediaSession,
                        onDeleteSong = onDeleteSong,
                        onVideoDelete = onVideoDelete,

                        )

                    // Permission request UI
                    if (!isPermissionsGranted && currentBackStackEntry?.destination?.route == BottomNavScreenRoutes.SongsHome.route) {
                        PermissionRequestScreen(
                            onRequestPermissions = {
                                if (permissions.any { perm ->
                                        activity?.let {
                                            isPermissionPermanentlyDenied(
                                                it,
                                                perm
                                            )
                                        } == true
                                    }) {
                                    // Show settings button if permissions are permanently denied
                                    context.openAppSettings()
                                } else {
                                    // Request permissions again
                                    permissionLauncher.launch(permissions)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
//    }
}

@Composable
fun PermissionRequestScreen(
    onRequestPermissions: () -> Unit,
) {
    // Create a gradient brush using the primary and secondary colors
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            SoundScapeThemes.colorScheme.primary.copy(alpha = 1f),
            SoundScapeThemes.colorScheme.secondary.copy(alpha = 1f)
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY // Infinite to fill the entire height
    )

    // Main container with gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush), // Apply the gradient brush
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp) // Padding for inner content
        ) {
            // Title Text with custom style
            Text(
                text = "Permissions Required",
                color = Color.White, // White color for contrast
                fontSize = 26.sp, // Custom font size
                fontWeight = FontWeight.Bold, // Bold font weight
                modifier = Modifier.padding(bottom = 12.dp) // Space below title
            )

            // Description Text with custom style
            Text(
                text = "To continue using this app, please grant the necessary storage permissions.",
                color = Color.White, // White color for contrast
                fontSize = 16.sp, // Custom font size
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 30.dp) // Space below description
            )

            // Gallery Button with a more beautiful style
            Button(
                onClick = onRequestPermissions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD0D0D0), // Light gray for a distinct look
                    contentColor = Color.Black // Black button text for contrast
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // Padding around the button
                    .height(56.dp) // Fixed height for a better touch target
                    .clip(RoundedCornerShape(8.dp)), // Slightly squared corners
            ) {
                Text(
                    text = "Grant Permissions",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium) // Custom button text style
                )
            }
        }
    }
}


// Helper to check if all permissions are granted
fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
    return permissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

// Helper to check if a permission was permanently denied (user selected "Don't ask again")
fun isPermissionPermanentlyDenied(activity: Activity, permission: String): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) &&
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
}

// Helper to open the app settings screen
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}



object BlurHelper {

    fun blur(context: Context, drawableResId: Int, radius: Float): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableResId)
            ?: throw IllegalArgumentException("Drawable not found for resource ID $drawableResId")
        return blurBitmap(context, drawable, radius)
    }

    private fun blurBitmap(context: Context, drawable: Drawable, radius: Float): Bitmap {
        val bitmap = drawableToBitmap(drawable)
        val inputBitmap =
            Bitmap.createScaledBitmap(bitmap, bitmap.width / 8, bitmap.height / 8, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val rs = RenderScript.create(context)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val allocationIn = Allocation.createFromBitmap(rs, inputBitmap)
        val allocationOut = Allocation.createFromBitmap(rs, outputBitmap)

        script.setRadius(radius.coerceAtMost(25f)) // Limit radius to avoid crashes
        script.setInput(allocationIn)
        script.forEach(allocationOut)

        allocationOut.copyTo(outputBitmap)
        rs.destroy()

        return outputBitmap
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}