package com.coffeevoyager.data.shop

import com.coffeevoyager.domain.shop.SortingCoffeeProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SortingCoffeeProductRepository {

    private val _selectedSorting = MutableStateFlow(SortingCoffeeProduct.NewestArrivals)
    val selectedSorting: StateFlow<SortingCoffeeProduct> = _selectedSorting

    fun getSortingCoffeeProductList(): List<SortingCoffeeProduct> = SortingCoffeeProduct.entries

    fun updateSelectedSorting(sorting: SortingCoffeeProduct) {
        _selectedSorting.update { sorting }
    }

}
