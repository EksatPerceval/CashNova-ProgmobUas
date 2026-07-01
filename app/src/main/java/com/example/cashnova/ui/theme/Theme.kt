package com.example.cashnova.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CashNovaColorScheme = lightColorScheme(
    primary = CashNovaRed,
    onPrimary = Color.White,
    secondary = CashNovaBlue,
    onSecondary = Color.White,
    background = CashNovaBackground,
    onBackground = CashNovaText,
    surface = CashNovaSurface,
    onSurface = CashNovaText,
    surfaceVariant = Color(0xFFF0F1F5),
    onSurfaceVariant = CashNovaMuted,
    outline = CashNovaLine,
    error = CashNovaRed
)

@Composable
fun CashNovaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CashNovaColorScheme,
        typography = CashNovaTypography,
        content = content
    )
}
