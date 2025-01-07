package com.coffeevoyager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coffeevoyager.features.brew.BrewCoffeeMainScreen
import com.coffeevoyager.features.brew.navigation.BrewCoffeeMainRoute
import com.coffeevoyager.features.shop.products.CoffeeShopNavigationEvent
import com.coffeevoyager.features.shop.products.navigation.CoffeeShopBaseRoute
import com.coffeevoyager.features.shop.products.navigation.coffeeShopGraph

@Composable
fun CoffeeNavHost(
    topLevelNavigationState: TopLevelNavigationState,
    modifier: Modifier = Modifier
) {
    val navController = topLevelNavigationState.navController
    NavHost(
        navController = navController,
        startDestination = CoffeeShopBaseRoute,
        modifier = modifier
    ) {
        coffeeShopGraph(
            onNavigationEvent = { navigationEvent ->
                when (navigationEvent) {
                    CoffeeShopNavigationEvent.Cart -> {} // TODO
                    is CoffeeShopNavigationEvent.ProductDetails -> {} // TODO
                }
            }
        ) {
//            bottomSheet<CoffeeShopFiltersRoute> {  } can be used when https://issuetracker.google.com/issues/328949006 is fixed
        }

        composable<BrewCoffeeMainRoute> {
            BrewCoffeeMainScreen()
        }
    }
}
