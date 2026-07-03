package com.example.cashnova.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.cashnova.data.ThemeMode

/*
 * Color scheme mode terang.
 * Menjadi basis tampilan default saat dark mode tidak aktif.
 */
private val CashNovaLightColorScheme = lightColorScheme(
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

/*
 * Color scheme mode gelap.
 * Digunakan saat user memilih DARK atau mengikuti system dark mode.
 */
private val CashNovaDarkColorScheme = darkColorScheme(
    primary = CashNovaRed,
    onPrimary = Color.White,
    secondary = CashNovaBlue,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFA0A0A0),
    outline = Color(0xFF383838),
    error = Color(0xFFCF6679)
)

/*
 * Wrapper tema utama aplikasi.
 *
 * Prioritas pemilihan mode:
 * - LIGHT  -> paksa terang
 * - DARK   -> paksa gelap
 * - SYSTEM -> mengikuti setting sistem
 */
@Composable
fun CashNovaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) {
        CashNovaDarkColorScheme
    } else {
        CashNovaLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CashNovaTypography,
        content = content
    )
}
