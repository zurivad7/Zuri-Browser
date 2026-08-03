package com.zuri.browser.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF7C5CFF)
private val PurpleDark = Color(0xFF5B3FE0)

private val DarkColors = darkColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    background = Color(0xFF0B0B12),
    surface = Color(0xFF15151F),
    onBackground = Color(0xFFECECF1),
    onSurface = Color(0xFFECECF1),
    surfaceVariant = Color(0xFF1E1E2A),
)

private val LightColors = lightColorScheme(
    primary = PurpleDark,
    onPrimary = Color.White,
    background = Color(0xFFF7F7FB),
    surface = Color.White,
    onBackground = Color(0xFF15151F),
    onSurface = Color(0xFF15151F),
    surfaceVariant = Color(0xFFECECF1),
)

@Composable
fun ZuriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
