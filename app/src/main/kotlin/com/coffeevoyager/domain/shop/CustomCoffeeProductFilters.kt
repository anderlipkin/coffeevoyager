package com.coffeevoyager.domain.shop

enum class CustomCoffeeProductFilters(val id: String, val group: CoffeeProductFilterGroups) {
    Favorite("favorite", CoffeeProductFilterGroups.Favorite),
    PriceRange("price_range", CoffeeProductFilterGroups.PriceRange)
}
