package dev.dongwoo.sms_auto_responder.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    secondary = PrimaryAccent,
    tertiary = PrimaryAccent,
    background = DeepMidnight,
    surface = SurfaceDark,
    onPrimary = TextHighEmphasisOnDark,
    onSecondary = TextHighEmphasisOnDark,
    onTertiary = TextHighEmphasisOnDark,
    onBackground = TextHighEmphasisOnDark,
    onSurface = TextHighEmphasisOnDark,
)

// We mainly use Dark theme as per design "Deep Midnight", but let's define light too just in case, though spec seems to imply a fixed dark-ish style for main parts.
// Spec says: "Background (Deep Midnight): #050B16 (App default background)". So we might force dark mode or custom colors.
// Let's stick to the custom palette for the "AppTheme" regardless of system setting, as the design is specific.
// However, the "Rule List" area is "Surface Light (Panel): #F4F6F8".
// This suggests a hybrid design, not a simple Dark/Light mode switch.
// I will define the MaterialTheme mostly with the "Dark" values as defaults for the overall container,
// and use specific colors in Composables.

@Composable
fun SmsAutoResponderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Not strictly used as we force specific look
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to adhere to design spec
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepMidnight.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
