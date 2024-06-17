package com.SoundScapeApp.soundscape.SoundScapeApp.ui.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.SoundScapeApp.soundscape.R
import com.SoundScapeApp.soundscape.SoundScapeApp.ui.routes.ScreenRoute
import com.SoundScapeApp.soundscape.ui.theme.SoundScapeThemes
import com.SoundScapeApp.soundscape.ui.theme.White90
import com.SoundScapeApp.soundscape.ui.theme.sliderColor1
import com.SoundScapeApp.soundscape.ui.theme.sliderColor2
import com.SoundScapeApp.soundscape.ui.theme.test1
import com.SoundScapeApp.soundscape.ui.theme.test2
import com.SoundScapeApp.soundscape.ui.theme.test3
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreen(
    navController: NavController
) {
    val pagerState = rememberPagerState(pageCount = 3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 34.dp
            )
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState)
        {
            when (it) {
                0 -> {
                    Onboarding1()
                }

                1 -> {
                    Onboarding2()
                }

                2 -> {
                    Onboarding3()
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = sliderColor2,
            inactiveColor = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    SoundScapeThemes.colorScheme.secondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(SoundScapeThemes.sizes.normal)
                    .fillMaxWidth(),
                onClick = {
                    navController.navigate(ScreenRoute.ThemeSettings.route)
                }) {
                Text(
                    text = "Start listening",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun CustomOnBoardDesign(
    image: Int,
    mainText: String,
    paragraphText: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            Modifier
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mainText,
                color = White90,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = paragraphText,
                color = Color.LightGray,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
        }
    }

}


@Composable
fun Onboarding1() {
    CustomOnBoardDesign(
        image = R.drawable.onboarding2,
        mainText = "Un-Interrupted Music",

        paragraphText = "Enjoy your favorite music offline with no ads. Download," +
                " listen, and immerse yourself in pure audio bliss, " +
                "anytime and anywhere."
    )
}

@Composable
fun Onboarding2() {
    CustomOnBoardDesign(
        image = R.drawable.onboarding3,
        mainText = "Beautiful Design",
        paragraphText = "Enjoy a sleek and intuitive interface that makes your " +
                "offline music experience seamless." +
                " Focus on your music, not the ads."
    )
}

@Composable
fun Onboarding3() {
    CustomOnBoardDesign(
        image = R.drawable.onboarding1,
        mainText = "Feature Rich",
        paragraphText = "Customize your music experience with various themes, " +
                "advanced equalizers, " +
                "and easy-to-manage playlists. Enjoy offline listening."
    )
}


//@Composable
//fun Onboarding3() {
//    CustomOnBoardDesign(
//        image = R.drawable.frame__3_svg,
//        mainText = "Eat Well",
//        paragraphText = "Let's start a healthy life with us,we can\n" +
//                "determine your diet every day.healthy\n" +
//                "eating is fun."
//    )
//}