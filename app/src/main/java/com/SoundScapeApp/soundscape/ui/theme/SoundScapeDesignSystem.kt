package com.SoundScapeApp.soundscape.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle


//Color
data class SoundScapeColorScheme(
    val background: Color,
    val onBackground: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
)


//Typography
data class SoundScapeTypography(
    val titleLarge: TextStyle,
    val titleMedium: TextStyle, //Create playlist text
    val titleSmall: TextStyle,  //Song Title
    val bodyLarge: TextStyle, //Playlist name
    val bodyMedium: TextStyle, //Artist name //song counts
    val bodySmall: TextStyle,
//    val labelLarge: TextStyle,
//    val labelMedium: TextStyle,
//    val labelSmall: TextStyle
)

//Shapes

//Size
//data class SoundScapeSizes(
//    val large: Dp,
//    val medium: Dp,
//    val normal: Dp,
//    val small: Dp
//)


val localSoundScapeColorScheme = staticCompositionLocalOf {
    SoundScapeColorScheme(
        background  = Color.Unspecified,
        onBackground = Color.Unspecified,
        primary = Color.Unspecified,
        onPrimary = Color.Unspecified,
        secondary = Color.Unspecified,
        onSecondary = Color.Unspecified
    )
}


val localSoundScapeTypography = staticCompositionLocalOf {
    SoundScapeTypography(
        titleLarge = TextStyle.Default,
        titleMedium = TextStyle.Default,
        titleSmall = TextStyle.Default,
        bodyLarge = TextStyle.Default,
        bodyMedium = TextStyle.Default,
        bodySmall = TextStyle.Default,
    )
}


//val localSoundScapeSize = staticCompositionLocalOf {
//    SoundScapeSizes(
//        large = Dp.Unspecified,
//        medium = Dp.Unspecified,
//        normal = Dp.Unspecified,
//        small = Dp.Unspecified
//    )
//}