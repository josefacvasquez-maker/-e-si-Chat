package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NensiRedPrimary,
    onPrimary = NensiWhite,
    primaryContainer = NensiRedLight,
    onPrimaryContainer = NensiRedDark,
    secondary = NensiWhite,
    onSecondary = NensiRedPrimary,
    secondaryContainer = NensiRedLight,
    onSecondaryContainer = NensiRedDark,
    tertiary = NensiRedAccent,
    onTertiary = NensiWhite,
    background = NensiOffWhite,
    onBackground = NensiTextPrimary,
    surface = NensiWhite,
    onSurface = NensiTextPrimary,
    surfaceVariant = NensiChatBackground,
    onSurfaceVariant = NensiTextSecondary,
    outline = NensiBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = NensiRedAccent,
    onPrimary = NensiWhite,
    primaryContainer = NensiRedDark,
    onPrimaryContainer = NensiRedLight,
    secondary = NensiWhite,
    onSecondary = NensiRedPrimary,
    background = Color(0xFF121212),
    onBackground = NensiWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = NensiWhite,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF3E3E3E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Keep predominantly vibrant red and white as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = NensiRedDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
