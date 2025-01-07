package com.coffeevoyager.features.shop.products.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.coffeevoyager.features.shop.products.CoffeeShopNavigationEvent
import com.coffeevoyager.features.shop.products.CoffeeShopScreen
import kotlinx.serialization.Serializable

@Serializable
data object CoffeeShopBaseRoute

@Serializable
data object CoffeeShopRoute

fun NavController.navigateToCoffeeShop(navOptions: NavOptions? = null) {
    navigate(CoffeeShopRoute, navOptions)
}

fun NavGraphBuilder.coffeeShopGraph(
    onNavigationEvent: (CoffeeShopNavigationEvent) -> Unit,
    nestedGraph: NavGraphBuilder.() -> Unit
) {
    navigation<CoffeeShopBaseRoute>(startDestination = CoffeeShopRoute) {
        composable<CoffeeShopRoute> {
            CoffeeShopScreen(onNavigationEvent = onNavigationEvent)
        }
        nestedGraph()
    }
}
