@file:Suppress("DEPRECATION")

package com.example.soundscape.SoundScapeApp.ui.MainScreen


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.RenderEffect
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.soundscape.R
import com.example.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.example.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.example.soundscape.SoundScapeApp.data.Audio
import com.example.soundscape.SoundScapeApp.ui.BottomNavigation.customBottomNavigation.CustomBottomNav
import com.example.soundscape.SoundScapeApp.ui.BottomNavigation.navGraph.BottomNavGraph
import com.example.soundscape.SoundScapeApp.ui.BottomNavigation.routes.BottomNavScreenRoutes
import com.example.soundscape.ui.theme.SoundScapeThemes
import com.example.soundscape.ui.theme.Theme2Primary
import com.example.soundscape.ui.theme.Theme2Secondary
import com.google.accompanist.systemuicontroller.rememberSystemUiController


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
    onVideoItemClick: (Int, Long) -> Unit
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
            .navigationBarsPadding(),
        bottomBar = {
            if(showBottomBar){
                CustomBottomNav(navController = navController, context)
            }
        }
    ) {
        Box(
            modifier = Modifier
        ) {

//            val painter: Painter = rememberImagePainter(
//                data = R.drawable.naturesbg,
//                builder = {
//
//                }
//            )
//
//            Image(
//                painter = painter,
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.FillBounds
//            )
//

//            BlurredBackground()

            GlideImage(
                model = R.drawable.naturesbg,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(40.dp),
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
//                                Color.Black.copy(.9f),
//                                Color.Black.copy(.9f)
                                SoundScapeThemes.colorScheme.primary.copy(.9f),
                                SoundScapeThemes.colorScheme.secondary.copy(.9f)
                            ),
                            start = Offset(0f, 0f), // Top-left corner
                            end = Offset.Infinite // Bottom-right corner
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
                    mediaSession = mediaSession
                )
            }
        }
    }
}


//@Composable
//fun BlurredBackground() {
//    val context = LocalContext.current
//    val blurredBitmap = remember {
//        BitmapFactory.decodeResource(context.resources, R.drawable.naturesbg)
//            .blur(context, 50f)
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Blurred background image
//        Image(
//            bitmap = blurredBitmap.asImageBitmap(),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize()
//                .blur(40.dp),
//            contentScale = ContentScale.FillBounds,
//        )
//
//        // Content on top of the blurred background
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // Add your content here
//        }
//    }
//}
//
//fun Bitmap.blur(context: Context, radius: Float): Bitmap {
//    val blurredBitmap = this.copy(this.config, true)
//    val rs = RenderScript.create(context)
//    val input = Allocation.createFromBitmap(rs, this, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT)
//    val output = Allocation.createTyped(rs, input.type)
//    val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
//
//    script.setRadius(radius.coerceAtMost(25f)) // Limit radius to avoid crashes
//    script.setInput(input)
//    script.forEach(output)
//    output.copyTo(blurredBitmap)
//    rs.destroy()
//
//    return blurredBitmap
//}
