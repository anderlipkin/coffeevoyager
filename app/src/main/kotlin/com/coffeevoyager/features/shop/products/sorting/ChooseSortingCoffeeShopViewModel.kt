package com.coffeevoyager.features.shop.products.sorting

import androidx.lifecycle.ViewModel
import com.coffeevoyager.R
import com.coffeevoyager.core.ui.UiStringValue
import com.coffeevoyager.data.shop.SortingCoffeeProductRepository
import com.coffeevoyager.domain.shop.SortingCoffeeProduct
import com.coffeevoyager.core.ui.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChooseSortCoffeeShopViewModel(
    private val repository: SortingCoffeeProductRepository
) : ViewModel() {

    private val _sortItems =
        MutableStateFlow(ImmutableList(repository.getSortingCoffeeProductList().map(SortingCoffeeProduct::asUiItem)))

    val sortItems = _sortItems.asStateFlow()

    fun onSortItemClick(sort: SortingCoffeeProduct) {
        repository.updateSelectedSorting(sort)
    }

}

data class SortingCoffeeShopUiItem(
    val sorting: SortingCoffeeProduct,
    val name: UiStringValue
)

fun SortingCoffeeProduct.asUiItem(): SortingCoffeeShopUiItem =
    SortingCoffeeShopUiItem(sorting = this, name = getUiStringValue())

fun SortingCoffeeProduct.getUiStringValue(): UiStringValue.StringResource =
    when (this) {
        SortingCoffeeProduct.Cheap -> UiStringValue.StringResource(R.string.sort_price_low_to_high)
        SortingCoffeeProduct.Expensive -> UiStringValue.StringResource(R.string.sort_price_high_to_low)
        SortingCoffeeProduct.NewestArrivals -> UiStringValue.StringResource(R.string.sort_price_newest_arrivals)
    }
