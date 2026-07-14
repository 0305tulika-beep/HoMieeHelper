package com.homiee.helper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HomieeLightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = BackgroundWhite,
    secondary = TealDeep,
    background = BackgroundWhite,
    surface = BackgroundWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Color(0xFFE53E3E)
)

@Composable
fun HomieeHelperTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HomieeLightColors,
        typography = HomieeTypography,
        content = content
    )
}