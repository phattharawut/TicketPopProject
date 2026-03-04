package com.example.ticketpop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryRed,
    secondary = SurfaceGray,
    tertiary = TextGray,
    background = DarkBackground,
    surface = SurfaceGray,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    secondary = SurfaceGray,
    tertiary = TextGray,
    background = TextWhite,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = DarkBackground,
    onBackground = DarkBackground,
    onSurface = DarkBackground
)

@Composable
fun TICKETPOPTheme(
    darkTheme: Boolean = true, // Force Dark Theme for Movie Experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Always use dark theme for a cinematic look

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}