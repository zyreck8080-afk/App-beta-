package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Color(0xFFF7CADA),
    background = Color(0xFF100C0D),
    surface = Color(0xFF1A1416),
    onPrimary = Color(0xFF4A1A22),
    onSecondary = Color(0xFF4A1A22),
    onBackground = Color(0xFFFFFAFB),
    onSurface = Color(0xFFFFFAFB),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Color(0xFFF7CADA),
    background = Background,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = TextMain,
    onBackground = TextMain,
    onSurface = TextMain,
  )

@Composable
fun MyApplicationTheme(
  primaryColorHex: String = "#FFFFB6C1",
  typographyStyle: String = "SansSerif",
  darkTheme: Boolean = isSystemInDarkTheme(),
  isOledMode: Boolean = false,
  content: @Composable () -> Unit,
) {
  val customPrimary = try {
      Color(android.graphics.Color.parseColor(primaryColorHex))
  } catch (e: Exception) {
      Primary
  }

  var colorScheme =
    when {
      darkTheme -> DarkColorScheme.copy(primary = customPrimary)
      else -> LightColorScheme.copy(primary = customPrimary)
    }

  if (darkTheme && isOledMode) {
      colorScheme = colorScheme.copy(
          background = Color.Black,
          surface = Color.Black,
          surfaceVariant = Color(0xFF121212)
      )
  }

  val typography = getTypography(typographyStyle)

  MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
}
