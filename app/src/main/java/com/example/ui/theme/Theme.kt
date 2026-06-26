package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FintechPrimary,
    secondary = FintechSecondary,
    tertiary = FintechTeritary,
    background = FintechBgDark,
    surface = FintechSurfDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = FintechTextPrimaryDark,
    onSurface = FintechTextPrimaryDark,
    surfaceVariant = FintechSurfDarkElevated,
    onSurfaceVariant = FintechTextSecondaryDark,
    error = FintechRed
)

private val LightColorScheme = lightColorScheme(
    primary = FintechPrimaryDark,
    secondary = FintechSecondary,
    tertiary = FintechTeritary,
    background = FintechBgLight,
    surface = FintechSurfLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = FintechTextPrimaryLight,
    onSurface = FintechTextPrimaryLight,
    error = FintechRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // SettleX has a signature dark theme. Force darkTheme = true for that premium feel, or honor system
    dynamicColor: Boolean = false, // Disable to preserve signature fintech colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
