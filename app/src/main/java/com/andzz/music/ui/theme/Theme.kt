package com.andzz.music.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Color Palette ─────────────────────────────────────────────────────────────

val DeepBlack    = Color(0xFF050508)
val SurfaceDark  = Color(0xFF0F0F14)
val CardDark     = Color(0xFF16161D)
val ElevatedCard = Color(0xFF1E1E28)
val Divider      = Color(0xFF2A2A35)

val Accent        = Color(0xFFB388FF)   // Electric violet
val AccentGlow    = Color(0x55B388FF)
val AccentBright  = Color(0xFFD4AAFF)
val Gold          = Color(0xFFFFD166)
val GoldDim       = Color(0xFF8A7040)

val TextPrimary   = Color(0xFFF0EEF8)
val TextSecondary = Color(0xFF9490A8)
val TextDisabled  = Color(0xFF4A4858)

// ── Dark Color Scheme ─────────────────────────────────────────────────────────

val AndzZDarkColorScheme = darkColorScheme(
    primary          = Accent,
    onPrimary        = DeepBlack,
    primaryContainer = Color(0xFF3D1F7A),
    onPrimaryContainer = AccentBright,
    secondary        = Gold,
    onSecondary      = DeepBlack,
    background       = DeepBlack,
    onBackground     = TextPrimary,
    surface          = SurfaceDark,
    onSurface        = TextPrimary,
    surfaceVariant   = CardDark,
    onSurfaceVariant = TextSecondary,
    outline          = Divider,
    error            = Color(0xFFFF6B6B)
)

// ── Typography ────────────────────────────────────────────────────────────────

val AndzZTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
)

// ── Theme ─────────────────────────────────────────────────────────────────────

@Composable
fun AndzZMusicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AndzZDarkColorScheme,
        typography  = AndzZTypography,
        content     = content
    )
}
