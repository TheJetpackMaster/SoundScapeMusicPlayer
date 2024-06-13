package com.SoundScapeApp.soundscape.SoundScapeApp.ui.AudioHomeScreen.NowPlayingScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.AudioViewModel
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.BottomNavigation.routes.BottomNavScreenRoutes
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White50
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun ChooseAudioPlayingScreen(
    viewModel: AudioViewModel,
    navController: NavController
) {
    val isFirstTime by viewModel.isFirstTime.collectAsState()
    val currentDesign by viewModel.screenDesign.collectAsState()

    val pagerState = rememberPagerState(
        pageCount = 2,
        infiniteLoop = false,
        initialPage = if(isFirstTime) 0 else currentDesign,
        initialOffscreenLimit = 2
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 12.dp, bottom = 34.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Choose a Design!",
            color = White90,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Select a design for audio playing screen," +
                    "You can change it anytime from audio settings.",
            textAlign = TextAlign.Center,
            color = White50,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 34.dp, end = 34.dp)
        )
        Spacer(modifier = Modifier.weight(.5f))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxHeight(.8f)
                .fillMaxWidth(),
            itemSpacing = 24.dp,
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (it) {
                0 -> {
                    Screen1()
                }

                1 -> {
                    Screen2(

                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(.8f))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = Color.White
        )

        Spacer(modifier = Modifier.weight(.8f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 34.dp, end = 34.dp
                ),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SoundScapeThemes.sizes.normal)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SoundScapeThemes.colorScheme.primary,
                                SoundScapeThemes.colorScheme.secondary
                            )
                        )
                    )
                    .border(.3.dp, White50, RoundedCornerShape(8.dp)),
                onClick = {
                    if(isFirstTime) {
                        viewModel.setAudioScreenDesign(pagerState.currentPage)
                        viewModel.setIsFirstTime(false)
                        navController.popBackStack()
                        navController.navigate(BottomNavScreenRoutes.SongsHome.route)
                    }else{
                        viewModel.setAudioScreenDesign(pagerState.currentPage)
                        navController.popBackStack()
                    }
                },
            ) {
                Text(
                    text = "Confirm",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = White90
                )
            }
        }

    }
}

@Composable
fun Screen1() {
    Column(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = painterResource(id = R.drawable.screen2),
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(.5.dp, White50, RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun Screen2() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.screen1),
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(.5.dp, White50, RoundedCornerShape(12.dp))
        )
    }
}