package com.coffeevoyager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.coffeevoyager.core.designsystem.theme.CoffeeTheme
import com.coffeevoyager.ui.CoffeeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeTheme {
                CoffeeApp()
            }
        }
    }
}
