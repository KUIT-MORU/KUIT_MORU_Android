package com.konkuk.moru.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

val LimeGreen = Color(color = 0xFFB8EE44)
val PaleLime = Color(color = 0xFFEBFFC0)
val OliveGreen = Color(color = 0xFF7AB300)
val Black = Color(color = 0xFF000000)
val DarkGray = Color(color = 0xFF595959)
val MediumGray = Color(color = 0xFF999999)
val LightGray = Color(color = 0xFFD9D9D9)
val VeryLightGray = Color(color = 0xFFF1F3F5)
val CharcoalBlack = Color(color = 0xFF1A1A1A)
val Black50Oopacity = Color(color = 0x80000000)
val red = Color(color = 0xFFED4569)

@Immutable
data class MoruColors(
    val limeGreen: Color,
    val paleLime: Color,
    val oliveGreen: Color,
    val black: Color,
    val darkGray: Color,
    val mediumGray: Color,
    val lightGray: Color,
    val veryLightGray: Color,
    val charcoalBlack: Color,
    val black50Oopacity: Color,
    val red: Color
)

val defaultMoruColors = MoruColors(
    limeGreen = LimeGreen,
    paleLime = PaleLime,
    oliveGreen = OliveGreen,
    black = Black,
    darkGray = DarkGray,
    mediumGray = MediumGray,
    lightGray = LightGray,
    veryLightGray = VeryLightGray,
    charcoalBlack = CharcoalBlack,
    black50Oopacity = Black50Oopacity,
    red = red
)

val LocalMoruColorsProvider = staticCompositionLocalOf { defaultMoruColors }