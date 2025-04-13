package com.example.groeiproject.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF512DA8),
    secondary = Color(0xFF7B1FA2),
    tertiary = Color(0xFF9C27B0),
    background = Color.White,
    surface = Color(0xFFF8F8F8),
    onPrimary = Color.White,
    onBackground = Color(0xFF1C1C1E),
)

@Composable
fun PurpleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}