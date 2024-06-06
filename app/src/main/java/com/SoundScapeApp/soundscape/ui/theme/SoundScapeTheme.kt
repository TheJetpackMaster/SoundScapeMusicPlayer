package com.SoundScapeApp.soundscape.ui.theme

import android.app.Activity
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.SoundScapeApp.soundscape.MainActivity
import com.SoundScapeApp.soundscape.PlayerActivity

private val Theme1ColorScheme = SoundScapeColorScheme(
    background = Color.Black,
    onBackground = Purple80,
    primary = Theme1Primary,
    onPrimary = PurpleGrey80,
    secondary = Theme1Secondary,
    onSecondary = Pink80
)

private val Theme2ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme2Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme2Secondary,
    onSecondary = Pink40
)

private val Theme3ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme3Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme3Secondary,
    onSecondary = Pink40
)

private val Theme4ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme4Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme4Secondary,
    onSecondary = Pink40
)

private val Theme5ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme5Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme5Secondary,
    onSecondary = Pink40
)

private val Theme6ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme6Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme6Secondary,
    onSecondary = Pink40
)

private val Theme7ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme7Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme7Secondary,
    onSecondary = Pink40
)

private val Theme8ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme8Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme8Secondary,
    onSecondary = Pink40
)

private val Theme9ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme9Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme9Secondary,
    onSecondary = Pink40
)

private val Theme10ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme10Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme10Secondary,
    onSecondary = Pink40
)
private val Theme11ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme11Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme11Secondary,
    onSecondary = Pink40
)

private val Theme12ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme12Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme12Secondary,
    onSecondary = Pink40
)

private val Theme13ColorScheme = SoundScapeColorScheme(
    background = Color.White,
    onBackground = Purple40,
    primary = Theme13Primary,
    onPrimary = PurpleGrey40,
    secondary = Theme13Secondary,
    onSecondary = Pink40
)

private val SmallTypography = SoundScapeTypography(
    titleLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    )
)

private val MediumTypography = SoundScapeTypography(
    titleLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )
)

private val LargeTypography = SoundScapeTypography(
    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
)

// Sizes
private val smallSizes = SoundScapeSizes(
    large = 325.dp,
    medium = 265.dp,
    normal = 0.dp,
    small = 0.dp
)

private val mediumSizes = SoundScapeSizes(
    large = 370.dp,
    medium = 300.dp,
    normal = 0.dp,
    small = 0.dp
)

private val largeSizes = SoundScapeSizes(
    large = 425.dp,
    medium = 340.dp,
    normal = 0.dp,
    small = 0.dp
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SoundScapeThemes(
    themeChoice: Int,
    activity: Activity,
    content: @Composable () -> Unit
) {
    val windows = calculateWindowSizeClass(activity = activity)
    val config = LocalConfiguration.current

    val colorScheme = when (themeChoice) {
        1 -> Theme1ColorScheme
        2 -> Theme2ColorScheme
        3 -> Theme3ColorScheme
        4 -> Theme4ColorScheme
        5 -> Theme5ColorScheme
        6 -> Theme6ColorScheme
        7 -> Theme7ColorScheme
        8 -> Theme8ColorScheme
        9 -> Theme9ColorScheme
        10 -> Theme10ColorScheme
        11 -> Theme11ColorScheme
        12 -> Theme12ColorScheme
        13 -> Theme13ColorScheme
        else -> throw IllegalArgumentException("Invalid theme choice: $themeChoice")
    }
    val rippleIndication = rememberRipple()

    val typography = when (windows.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            if (config.screenWidthDp <= 360) {
                SmallTypography


            } else if (config.screenWidthDp <= 411) {
                MediumTypography

            } else {
                LargeTypography
            }
        }

        else -> {
            LargeTypography // Default typography for other size classes
        }

    }

    val sizes = when (windows.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            if (config.screenWidthDp <= 360) {
                smallSizes

            } else if (config.screenWidthDp <= 411) {
                mediumSizes

            } else {
                largeSizes
            }
        }

        else -> {
            largeSizes
        }

    }

    CompositionLocalProvider(
        localSoundScapeColorScheme provides colorScheme,
        localSoundScapeTypography provides typography,
        localSoundScapeSize provides sizes,
        LocalIndication provides rippleIndication,
        content = content
    )

    val darkTheme = isSystemInDarkTheme()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars
        }
    }
}

object SoundScapeThemes {
    val colorScheme: SoundScapeColorScheme
        @Composable get() = localSoundScapeColorScheme.current

    val typography: SoundScapeTypography
        @Composable get() = localSoundScapeTypography.current

    val sizes:SoundScapeSizes
        @Composable get() = localSoundScapeSize.current
}


