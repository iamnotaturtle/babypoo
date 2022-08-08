package com.ygaberman.babypoo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = YellowDark,
    primaryVariant = YellowVariant,
    secondary = Blue
)

private val LightColorPalette = lightColors(
    primary = YellowLight,
    primaryVariant = YellowVariant,
    secondary = Blue
)

@Composable
fun BabyPooTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}