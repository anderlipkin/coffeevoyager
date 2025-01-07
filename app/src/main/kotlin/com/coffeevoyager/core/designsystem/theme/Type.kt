package com.coffeevoyager.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.coffeevoyager.R

val caveatFontFamily = FontFamily(Font(R.font.caveat_variable_font_wght))

// Default Material 3 typography values
val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = caveatFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = caveatFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = caveatFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = caveatFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = caveatFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = caveatFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = caveatFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = caveatFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = caveatFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = caveatFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = caveatFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = caveatFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = caveatFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = caveatFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = caveatFontFamily),
)

