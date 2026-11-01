package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestSagePrimary,
    onPrimary = ForestSageOnPrimary,
    primaryContainer = SagePrimaryContainer,
    onPrimaryContainer = SageOnPrimaryContainer,
    secondary = TerracottaSecondary,
    onSecondary = TerracottaOnSecondary,
    secondaryContainer = TerracottaSecondaryContainer,
    onSecondaryContainer = TerracottaOnSecondaryContainer,
    tertiary = MintTakenGreen,
    onTertiary = ForestSageOnPrimary,
    background = CreamBackground,
    onBackground = TextPrimary,
    surface = CreamSurface,
    onSurface = TextPrimary,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderOutline,
    outlineVariant = BorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = TerracottaOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = TerracottaSecondaryContainer,
    tertiary = MintTakenGreen,
    onTertiary = ForestSageOnPrimary,
    background = DarkBackground,
    onBackground = CreamSurface,
    surface = DarkSurface,
    onSurface = CreamSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextTertiary,
    outline = TextSecondary,
    outlineVariant = DarkSurfaceVariant
)

@Composable
fun MedicineReminderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
