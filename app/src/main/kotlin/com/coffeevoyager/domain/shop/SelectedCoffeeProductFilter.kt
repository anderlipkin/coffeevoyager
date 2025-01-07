package com.coffeevoyager.domain.shop

import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod

data class SelectedCoffeeProductFilter(
    val id: String,
    val groupId: String,
    val groupType: CoffeeProductFilterGroups,
    val selectionMethod: SelectionMethod
)
