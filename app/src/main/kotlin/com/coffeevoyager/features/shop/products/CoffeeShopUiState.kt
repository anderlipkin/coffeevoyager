package com.coffeevoyager.features.shop.products

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.coffeevoyager.core.ui.UiStringValue
import com.coffeevoyager.domain.shop.CoffeeCompany
import com.coffeevoyager.domain.shop.CoffeeProductShortWithCompany
import com.coffeevoyager.domain.shop.Currency
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.Company
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.Product

data class CoffeeShopUiState(
    val topBarState: TopBarState,
    val listState: ListState,
    val showBottomSheet: CoffeeShopBottomSheets?
) {

    sealed interface ListState {
        @Immutable
        data class Success(
            val companies: List<Company>,
            val coffeeBeanProducts: List<Product>,
            val scrollToTop: Boolean
        ) : ListState

        data class Empty(val hasAppliedFiltersOrSearch: Boolean) : ListState

        data object Loading : ListState
    }

    data class TopBarState(
        val searchFieldState: TextFieldState,
        val hasAppliedFilters: Boolean,
        val selectedSorting: UiStringValue.StringResource,
        val favoriteFilterState: FavoriteFilterState,
        val countInCart: Int
    )

    data class FavoriteFilterState(
        val visible: Boolean,
        val selected: Boolean
    )

    data class Product(
        val id: String,
        val name: String,
        val companyName: String,
        val imageUrl: String,
        val companyLogoUrl: String,
        val price: String,
        val currency: String,
        val isFavorite: Boolean,
        val addedToCart: Boolean
    )

    data class Company(
        val id: String,
        val name: String,
        val imageUrl: String,
        val selected: Boolean
    )
}

sealed interface TopAppBarAction {
    data class FavoriteFilterClick(val checked: Boolean) : TopAppBarAction
    data object CartClick : TopAppBarAction
    data object FiltersClick : TopAppBarAction
    data object SortingClick : TopAppBarAction
}

sealed interface CoffeeProductItemAction {
    val id: String

    data class FavoriteClick(
        override val id: String,
        val checked: Boolean
    ) : CoffeeProductItemAction

    data class ItemClick(override val id: String) : CoffeeProductItemAction

    data class AddToCartClick(override val id: String) : CoffeeProductItemAction
}

sealed interface CoffeeShopNavigationEvent {
    data object Cart : CoffeeShopNavigationEvent
    data class ProductDetails(val id: String) : CoffeeShopNavigationEvent
}

enum class CoffeeShopBottomSheets {
    Sorting, Filters
}

fun CoffeeProductShortWithCompany.asUiItem(isFavorite: Boolean) =
    Product(
        id = id,
        name = name,
        companyName = company.name,
        imageUrl = imageUrl,
        companyLogoUrl = company.imageUrl,
        price = price,
        currency = Currency.UAH.value,
        isFavorite = isFavorite,
        addedToCart = false
    )

fun CoffeeCompany.asUiItem(selected: Boolean) =
    Company(
        id = id,
        name = name,
        imageUrl = imageUrl,
        selected = selected
    )
