package com.coffeevoyager.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.coffeevoyager.navigation.CoffeeNavHost
import com.coffeevoyager.navigation.rememberTopLevelNavigationState
import org.koin.androidx.compose.KoinAndroidContext
import kotlin.reflect.KClass

@Composable
fun CoffeeApp(modifier: Modifier = Modifier) {
    val topLevelNavigationState = rememberTopLevelNavigationState()
    val currentDestination = topLevelNavigationState.currentDestination
    KoinAndroidContext {
        Scaffold(
            bottomBar = {
                val showBottomBar = currentDestination != null
                if (showBottomBar) {
                    NavigationBar {
                        topLevelNavigationState.topLevelDestinations.forEach { destination ->
                            val selected =
                                currentDestination.isRouteInHierarchy(destination.baseRoute)
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    topLevelNavigationState.navigateToTopLevelDestination(destination)
                                },
                                icon = {
                                    Icon(imageVector = destination.icon, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            },
            modifier = modifier
        ) { innerPadding ->
            CoffeeNavHost(
                topLevelNavigationState = topLevelNavigationState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false
