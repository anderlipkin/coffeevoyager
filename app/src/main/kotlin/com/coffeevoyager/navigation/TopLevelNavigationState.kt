package com.coffeevoyager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.coffeevoyager.features.brew.navigation.navigateToBrewCoffeeMain
import com.coffeevoyager.features.shop.products.navigation.navigateToCoffeeShop

@Composable
fun rememberTopLevelNavigationState(
    navController: NavHostController = rememberNavController()
): TopLevelNavigationState {
    return remember(navController) {
        TopLevelNavigationState(navController)
    }
}

@Stable
class TopLevelNavigationState(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestinations?
        @Composable get() {
            return topLevelDestinations.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
        }

    val topLevelDestinations: List<TopLevelDestinations> = TopLevelDestinations.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestinations) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (topLevelDestination) {
            TopLevelDestinations.SHOP -> navController.navigateToCoffeeShop(topLevelNavOptions)
            TopLevelDestinations.BREW_COFFEE -> navController.navigateToBrewCoffeeMain(topLevelNavOptions)
        }
    }
}
