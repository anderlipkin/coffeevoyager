package com.coffeevoyager.domain

sealed class CoffeeGrindSize {
    data object Coarse : CoffeeGrindSize()
    data object Medium : CoffeeGrindSize()
    data object Fine : CoffeeGrindSize()
}