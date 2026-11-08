package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WBuildsColorScheme = darkColorScheme(
  primary = WBuildsGold,
  onPrimary = WBuildsBlack,
  primaryContainer = WBuildsGoldDark,
  onPrimaryContainer = WBuildsGoldLight,
  secondary = WBuildsGoldLight,
  onSecondary = WBuildsBlack,
  secondaryContainer = WBuildsCardElevated,
  onSecondaryContainer = WBuildsGold,
  tertiary = WBuildsSuccess,
  onTertiary = WBuildsBlack,
  background = WBuildsBlack,
  onBackground = WBuildsTextPrimary,
  surface = WBuildsDarkSurface,
  onSurface = WBuildsTextPrimary,
  surfaceVariant = WBuildsCard,
  onSurfaceVariant = WBuildsTextSecondary,
  outline = WBuildsDivider,
  outlineVariant = WBuildsDivider,
  error = WBuildsError,
  onError = WBuildsTextPrimary
)

@Composable
fun WBuildsTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = WBuildsColorScheme,
    typography = Typography,
    content = content
  )
}
