package com.SoundScapeApp.soundscape.SoundScapeApp.ui.SettingsScreen.ThemeSettings

import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.VideoViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.MainScreen.BlurHelper
import com.SoundScapeApp.soundscape.ui.theme.GrayIcons
import com.SoundScapeApp.soundscape.ui.theme.Pink80
import com.SoundScapeApp.soundscape.ui.theme.PurpleIcons
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.Theme10Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme10Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme11Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme11Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme12Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme12Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme13Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme13Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme1Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme2Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme3Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme4Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme5Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme6Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme7Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme8Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme8Secondary
import com.SoundScapeApp.soundscape.ui.theme.Theme9Primary
import com.SoundScapeApp.soundscape.ui.theme.Theme9Secondary
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ThemeSettings(
    navController: NavController,
    audioViewModel: AudioViewModel,
    videoViewModel: VideoViewModel
) {

    val currentTheme by audioViewModel.currentTheme.collectAsState()

    val primaryColors = listOf(
        Theme1Primary,
        Theme2Primary,
        Theme3Primary,
        Theme4Secondary,
        Theme5Secondary,
        Theme6Primary,
        Theme7Primary,
        Theme8Primary,
        Theme9Primary,
        Theme10Primary,
        Theme11Primary,
        Theme12Primary,
        Theme13Primary
    )

    val secondaryColors = listOf(
        Theme1Primary,
        Theme2Primary,
        Theme3Primary,
        Theme4Secondary,
        Theme5Secondary,
        Theme6Primary,
        Theme7Primary,
        Theme8Secondary,
        Theme9Secondary,
        Theme10Secondary,
        Theme11Secondary,
        Theme12Secondary,
        Theme13Secondary
    )

    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(text = "Choose Theme", color = White90)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.currentBackStackEntry?.lifecycle?.currentState
                            == Lifecycle.State.RESUMED
                        ) {
                            navController.popBackStack()
                        }
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = White50
                        )
                    }
                })
        }
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 12.dp, bottom = 12.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(.1f))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(.6f)
            ) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    val blurredBitmap =
                        BlurHelper.blur(context, drawableResId = R.drawable.naturesbg, 25f)
                    Image(
                        bitmap = blurredBitmap.asImageBitmap(),
                        contentDescription = "Background image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                    )
                } else {
                    GlideImage(
                        model = R.drawable.naturesbg,
                        contentDescription = "Background Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(40.dp),
                        contentScale = ContentScale.FillBounds,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    SoundScapeThemes.colorScheme.primary.copy(.9f),
                                    SoundScapeThemes.colorScheme.secondary.copy(.9f)
                                ),
                                start = Offset(0f, 0f), // Top-left corner
                                end = Offset.Infinite // Bottom-right corner
                            )
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    val image = when (currentTheme) {
//                        1 -> painterResource(id = R.drawable.theme1sample)
//                        2 -> painterResource(id = R.drawable.theme2sample)
//                        3 -> painterResource(id = R.drawable.theme3sample)
//                        4 -> painterResource(id = R.drawable.theme5sample)
//                        5 -> painterResource(id = R.drawable.theme5sample)
//                        else -> throw IllegalArgumentException("Invalid theme choice: $currentTheme")
//                    }
//                    Image(
//                        painter = image,
//                        contentDescription = null,
//                        modifier = Modifier.border(1.dp, White50)
//                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, White50)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(start = 12.dp, end = 8.dp, top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(
                                text = "SoundScape", color = White90,
                                fontSize = 10.sp, fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                modifier = Modifier.size(22.dp),
                                onClick = { /*TODO*/ })
                            {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = White90
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                modifier = Modifier.size(22.dp),
                                onClick = { /*TODO*/ })
                            {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = White90
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .padding(start = 3.dp, end = 6.dp, top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly

                        ) {
                            Column(
                                modifier = Modifier.width(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Songs", color = White90,
                                    fontSize = 8.sp, fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(
                                    modifier = Modifier.width(25.dp),
                                    thickness = 1.dp, color = Pink80
                                )
                            }

                            Column(
                                modifier = Modifier.width(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Playlists", color = White90,
                                    fontSize = 8.sp, fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
//                                HorizontalDivider(
//                                    modifier = Modifier.width(25.dp),
//                                    thickness = 1.dp, color = Pink80
//                                )
                            }

                            Column(
                                modifier = Modifier.width(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Albums", color = White90,
                                    fontSize = 8.sp, fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
//                                HorizontalDivider(
//                                    modifier = Modifier.width(25.dp),
//                                    thickness = 1.dp, color = Pink80
//                                )
                            }

                            Column(
                                modifier = Modifier.width(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Artists", color = White90,
                                    fontSize = 8.sp, fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))

//                                HorizontalDivider(
//                                    modifier = Modifier.width(25.dp),
//                                    thickness = 1.dp, color = Pink80
//                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(1.dp))
                        HorizontalDivider(
                            thickness = .1.dp, color = White90
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = "Total songs(5)",
                                color = White90,
                                fontSize = 6.sp,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { /*TODO*/ })
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.sorting),
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.sample),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(25.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.FillBounds
                            )


                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Awarapan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Arijit singh",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = White50,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(225.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { /*TODO*/ })
                            {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.sample),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(25.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.FillBounds
                            )


                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Awarapan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Arijit singh",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = White50,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(225.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { /*TODO*/ })
                            {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.sample),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(25.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.FillBounds
                            )


                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Awarapan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Arijit singh",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = White50,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(225.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { /*TODO*/ })
                            {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.sample),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(25.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.FillBounds
                            )


                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Awarapan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Arijit singh",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = White50,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(225.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { /*TODO*/ })
                            {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                    tint = White90,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }


                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .padding(start = 8.dp, end = 8.dp)
                                .clip(CircleShape)
                                .background(SoundScapeThemes.colorScheme.primary),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.padding(start = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.naturesbg),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SoundScapeThemes.colorScheme.secondary)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "No song playing",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White90,
                                    modifier = Modifier.width(80.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )

                                Text(
                                    text = "Unknown",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = White50,
                                    modifier = Modifier.width(80.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.pauseicon),
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = White90
                                )

                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.size(28.dp),
                                    color = GrayIcons,
                                    strokeWidth = .5.dp,
                                    trackColor = GrayIcons,
                                    strokeCap = StrokeCap.Round,
                                )
                                CircularProgressIndicator(
                                    progress = { .5f },
                                    modifier = Modifier.size(28.dp),
                                    color = PurpleIcons,
                                    strokeWidth = 1.2.dp,
                                    trackColor = Color.Transparent,
                                    strokeCap = StrokeCap.Round,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .background(SoundScapeThemes.colorScheme.primary),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = { /*TODO*/ })
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.audiobottom),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.Unspecified
                                )
                            }

                            IconButton(onClick = { /*TODO*/ })
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.videobottom),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = White50
                                )
                            }

                            IconButton(onClick = { /*TODO*/ })
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.settingsbottom),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = White50
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(.2f))

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                thickness = .5.dp,
                color = White90
            )
            Spacer(modifier = Modifier.height(16.dp))

            ColorThemeRow(
                primaryColorList = primaryColors,
                secondaryColorList = secondaryColors,
                currentTheme = currentTheme,
                onItemClick = { theme ->
                    if (currentTheme != theme) {
                        audioViewModel.setTheme(theme)
                    }
                },
                context = context
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ColorThemeRow(
    primaryColorList: List<Color>,
    secondaryColorList: List<Color>,
    currentTheme: Int,
    onItemClick: (Int) -> Unit = {},
    context:Context
) {

    val painter = painterResource(id = R.drawable.naturesbg)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(start = 16.dp)
    ) {
        items(primaryColorList.size) { index ->
            val isSelected = currentTheme == index + 1
            val color1 = primaryColorList[index]
            val color2 = secondaryColorList[index]
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onItemClick(index + 1) },
                contentAlignment = Alignment.Center
            ) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    val blurredBitmap =
                        BlurHelper.blur(context, drawableResId = R.drawable.naturesbg, 25f)
                    Image(
                        bitmap = blurredBitmap.asImageBitmap(),
                        contentDescription = "Background image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                    )
                } else {
                    GlideImage(
                        model = R.drawable.naturesbg,
                        contentDescription = "Background Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(40.dp),
                        contentScale = ContentScale.FillBounds,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    color1.copy(.8f),
                                    color2.copy(.8f)
                                ),
                                start = Offset(0f, 0f), // Top-left corner
                                end = Offset.Infinite // Bottom-right corner
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) White50 else Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = White90
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}
