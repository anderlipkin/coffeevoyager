package com.coffeevoyager.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import com.coffeevoyager.features.brew.navigation.BrewCoffeeMainRoute
import com.coffeevoyager.features.shop.products.navigation.CoffeeShopBaseRoute
import com.coffeevoyager.features.shop.products.navigation.CoffeeShopRoute
import kotlin.reflect.KClass

enum class TopLevelDestinations(
    val icon: ImageVector,
//    val selectedIcon: ImageVector,
//    val unselectedIcon: ImageVector,
//    @StringRes val iconTextId: Int,
//    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route
) {
    SHOP(
        icon = Icons.Outlined.Storefront,
        route = CoffeeShopRoute::class,
        baseRoute = CoffeeShopBaseRoute::class
    ),
    BREW_COFFEE(
        icon = Icons.Outlined.Coffee,
        route = BrewCoffeeMainRoute::class
    ),
//    EVENTS(
//        icon = Icons.Outlined.Event,
//        route = ,
//    ),
//    MORE_MENU(
//        icon = Icons.Outlined.Menu,
//        route =
//    )
}
