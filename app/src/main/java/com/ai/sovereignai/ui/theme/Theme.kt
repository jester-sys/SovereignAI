package com.ai.sovereignai.ui.theme

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


/**
 * Neutral Dark Color Scheme
 * A highly neutral dark theme with true black for OLED displays
 */
private val DarkColors = darkColorScheme(
    primary = Color(0xFFE0E0E0),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF2C2C2C),
    onPrimaryContainer = Color(0xFFE0E0E0),

    secondary = Color(0xFFBDBDBD),
    onSecondary = Color(0xFF121212),
    secondaryContainer = Color(0xFF3A3A3A),
    onSecondaryContainer = Color(0xFFEEEEEE),

    tertiary = Color(0xFF9E9E9E),
    onTertiary = Color(0xFF1C1C1C),
    tertiaryContainer = Color(0xFF484848),
    onTertiaryContainer = Color(0xFFE0E0E0),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFF9E9E9E),

    outline = Color(0xFF5F5F5F),
    outlineVariant = Color(0xFF3A3A3A),

    scrim = Color(0x80000000),
)

/**
 * Neutral Light Color Scheme
 * A highly neutral light theme using shades of gray
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF404040),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E0E0),
    onPrimaryContainer = Color(0xFF1C1C1C),

    secondary = Color(0xFF5F5F5F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEEEEE),
    onSecondaryContainer = Color(0xFF2C2C2C),

    tertiary = Color(0xFF757575),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF5F5F5),
    onTertiaryContainer = Color(0xFF303030),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF5F5F5F),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),

    scrim = Color(0x80000000),
)

/**
 * Theme Mode settings
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Color Style settings
 */
enum class ColorStyle {
    DYNAMIC,
    CUSTOM,
    NEUTRAL
}


/**
 * YourOwnAI Theme
 *
 * A highly neutral theme with support for:
 * - Dynamic Color (Material You) for Android 12+
 * - Neutral gray palette for older versions
 * - Optional customization
 */
@Composable
fun SovereignAITheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    colorStyle: ColorStyle = ColorStyle.DYNAMIC,
    customAccentColor: Color? = null,
    content: @Composable () -> Unit
) {

    val context = LocalContext.current

    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        colorStyle == ColorStyle.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        colorStyle == ColorStyle.CUSTOM && customAccentColor != null -> {
            if(darkTheme) DarkColors  else LightColors
        }
        else ->{
            if(darkTheme) DarkColors  else LightColors
        }
    }

    // System bars configuration
    val view = LocalView.current
    if(!view.isInEditMode){
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }

            }
        }
    }


}