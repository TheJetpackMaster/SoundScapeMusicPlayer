@file:Suppress("DEPRECATION")

package com.SoundScapeApp.soundscape.SoundScapeApp.ui.MainScreen


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.customBottomNavigation.CustomBottomNav
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.navGraph.BottomNavGraph
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.ui.theme.NavigationBarColor
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.google.accompanist.systemuicontroller.rememberSystemUiController


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
    onDeleteSong:(List<Uri>)->Unit,
    onVideoDelete:(List<Uri>)->Unit
) {

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = false
    )

    systemUiController.setNavigationBarColor(
        color = Color.Transparent,
        darkIcons = false
    )

    val navController = rememberNavController()

    val screens = listOf(
        BottomNavScreenRoutes.SongsHome,
        BottomNavScreenRoutes.VideosHome,
        BottomNavScreenRoutes.Settings
    )
    val showBottomBar = navController
        .currentBackStackEntryAsState().value?.destination?.route in screens.map { it.route }


    Scaffold(
        modifier = Modifier
            .background(color = NavigationBarColor)
            .navigationBarsPadding(),
        containerColor = Color.Transparent,
        bottomBar = {
            if(showBottomBar){
                CustomBottomNav(navController = navController, context)
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

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                val blurredBitmap =
                    BlurHelper.blur(context, drawableResId = R.drawable.themebackground, 25f)
                Image(
                    bitmap = blurredBitmap.asImageBitmap(),
                    contentDescription = "Background image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                )
            } else {
//
                GlideImage(
                    model = R.drawable.themebackground,
                    contentDescription = "Background Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(25.dp),
                    contentScale = ContentScale.FillBounds,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
//                                Color.Black.copy(.9f),
//                                Color.Black.copy(.9f)
                                SoundScapeThemes.colorScheme.primary.copy(.65f),
                                SoundScapeThemes.colorScheme.secondary.copy(.65f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset.Infinite
                        )
                    )
                    .padding(bottom = it.calculateBottomPadding())
            ) {
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
                    onVideoDelete = onVideoDelete
                )
            }
        }
    }
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