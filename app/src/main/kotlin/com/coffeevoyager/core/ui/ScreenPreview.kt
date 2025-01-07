package com.coffeevoyager.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import com.coffeevoyager.core.designsystem.theme.CoffeeTheme
import com.coffeevoyager.di.appModule
import org.koin.compose.KoinApplication

@Suppress("ModifierMissing")
@Composable
fun ScreenPreview(
    screen: @Composable () -> Unit
) {
    KoinApplication(application = { modules(appModule) }) {
        CoffeeTheme {
            Surface(Modifier.fillMaxSize()) {
                screen()
            }
        }
    }
}

@Composable
fun placeholderIfPreview(placeholder: @Composable () -> Painter): Painter? =
    if (LocalInspectionMode.current) placeholder() else null
