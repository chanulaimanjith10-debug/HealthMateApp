package com.example.healthmateapp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB7C4FF),
    secondary = Color(0xFFD0D4F0),
    tertiary = Color(0xFFFFB6C1),
    background = Color(0xFF171923),
    surface = Color(0xFF222536)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF506293),
    secondary = Color(0xFF6979A7),
    tertiary = Color(0xFF936A87),
    background = Color(0xFFF8F7FC),
    surface = Color(0xFFE5E6F0)
)

@Composable
fun HealthMateAppTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
