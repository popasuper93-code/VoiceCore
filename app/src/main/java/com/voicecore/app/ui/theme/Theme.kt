package com.voicecore.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VoiceCoreColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    secondary = NeonBlue,
    onSecondary = Color.White,
    tertiary = NeonCyan,
    background = BlackDeep,
    onBackground = TextPrimary,
    surface = Graphite,
    onSurface = TextPrimary,
    surfaceVariant = GraphiteLight,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = Color.White
)

@Composable
fun VoiceCoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VoiceCoreColorScheme,
        typography = Typography,
        content = content
    )
}
