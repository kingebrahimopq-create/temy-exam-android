package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AcademicBlueDark,
    secondary = GoldenAmberDark,
    tertiary = Color(0xFFA7F3D0),
    background = CharcoalDark,
    surface = CardSlateDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = LightTextDark,
    onSurface = LightTextDark
)

private val LightColorScheme = lightColorScheme(
    primary = AcademicBlueLight,
    secondary = GoldenAmberLight,
    tertiary = Color(0xFF065F46),
    background = SoftCreamLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkTextLight,
    onSurface = DarkTextLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
