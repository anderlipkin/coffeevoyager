package com.coffeevoyager.domain.shop

sealed class CoffeeGrindSize {
    data object Coarse : CoffeeGrindSize()
    data object Medium : CoffeeGrindSize()
    data object Fine : CoffeeGrindSize()
}
