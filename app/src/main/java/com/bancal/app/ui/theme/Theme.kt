package com.bancal.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = OnPrimaryLight,
    secondary = GreenSecondary,
    onSecondary = OnPrimaryLight,
    tertiary = BrownTertiary,
    background = CreamBackground,
    onBackground = OnBackgroundLight,
    surface = CreamSurface,
    onSurface = OnSurfaceLight,
    error = _ErrorLight,
    surfaceVariant = CreamBackground,
    onSurfaceVariant = OnSurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = OnPrimaryDark,
    secondary = GreenSecondaryDark,
    onSecondary = OnPrimaryDark,
    tertiary = BrownTertiaryDark,
    background = DarkBackground,
    onBackground = OnBackgroundDark,
    surface = DarkSurface,
    onSurface = OnSurfaceDark,
    error = _ErrorDark,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = OnSurfaceDark
)

@Composable
fun BancalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BancalTypography,
        content = content
    )
}
