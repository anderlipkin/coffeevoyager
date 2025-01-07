package com.coffeevoyager.features.brew.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

@Serializable
data object BrewCoffeeMainRoute

fun NavController.navigateToBrewCoffeeMain(navOptions: NavOptions? = null) {
    navigate(BrewCoffeeMainRoute, navOptions)
}
