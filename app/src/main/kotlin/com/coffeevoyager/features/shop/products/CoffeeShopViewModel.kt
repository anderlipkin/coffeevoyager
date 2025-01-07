package com.coffeevoyager.features.shop.products

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeevoyager.core.common.extension.filterIf
import com.coffeevoyager.core.ui.ExactlyOnceEventFlow
import com.coffeevoyager.data.shop.CoffeeShopFetcher
import com.coffeevoyager.data.shop.CoffeeShopRepository
import com.coffeevoyager.data.shop.SearchCoffeeProductRepository
import com.coffeevoyager.data.shop.SelectedFiltersCoffeeProductRepository
import com.coffeevoyager.data.shop.SortingCoffeeProductRepository
import com.coffeevoyager.data.user.datastore.UserPreferencesDataSource
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroups
import com.coffeevoyager.domain.shop.CoffeeProductShortWithCompany
import com.coffeevoyager.domain.shop.SortingCoffeeProduct
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.ListState
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.Product
import com.coffeevoyager.features.shop.products.sorting.getUiStringValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ACTIVE_SEARCH_ON_TEXT_LENGTH = 2

class CoffeeShopViewModel(
    private val coffeeShopFetcher: CoffeeShopFetcher,
    private val filterRepository: SelectedFiltersCoffeeProductRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    searchRepository: SearchCoffeeProductRepository,
    coffeeShopRepository: CoffeeShopRepository,
    sortingProductRepository: SortingCoffeeProductRepository
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(CoffeeShopViewModelState(isLoadingProductList = true))

    private val filteredCoffeeProducts = filteredCoffeeProductsFlow(
        coffeeShopRepository,
        filterRepository
    ).filterBySearch(searchRepository, viewModelState.value.searchFieldTextFlow)
        .sortedBy(sortingProductRepository)
        .asUiItems(userPreferencesDataSource.favoriteCoffeeBeanProductIdsFlow)

    private val favoriteFilterStateFlow = combine(
        filterRepository.isFavoriteFilterSelectedFlow(),
        coffeeShopRepository.getFavoriteProductIdsFlow()
            .mapLatest { it.isNotEmpty() }
            .distinctUntilChanged()
    ) { isFavoriteFilterSelected, isFavoriteProductsAvailable ->
        CoffeeShopUiState.FavoriteFilterState(
            visible = isFavoriteProductsAvailable || isFavoriteFilterSelected,
            selected = isFavoriteFilterSelected
        )
    }

    private val topBarState = combine(
        favoriteFilterStateFlow,
        filterRepository.getSelectedFiltersFlow()
            .mapLatest { it.any { it.groupId != CoffeeProductFilterGroups.Favorite.id } }
            .distinctUntilChanged(),
        sortingProductRepository.selectedSorting
    ) { favoriteFilterState, isAnyFilterSelected, selectedSorting ->
        CoffeeShopUiState.TopBarState(
            searchFieldState = viewModelState.value.searchFieldState,
            favoriteFilterState = favoriteFilterState,
            selectedSorting = selectedSorting.getUiStringValue(),
            countInCart = 0,
            hasAppliedFilters = isAnyFilterSelected
        )
    }

    private val coffeeCompaniesFlow = combine(
        coffeeShopRepository.getAllCompaniesFlow(),
        filterRepository.getSelectedCompanyFilterIdsFlow()
    ) { companies, selectedFilterCompanyIds ->
        companies.map { company ->
            company.asUiItem(selected = company.id in selectedFilterCompanyIds)
        }
    }

    private val listState: Flow<ListState> = listState(
        filteredCoffeeProducts,
        filterRepository,
        viewModelState,
        coffeeCompaniesFlow
    )

    val uiState: StateFlow<CoffeeShopUiState> = combine(
        topBarState,
        listState,
        viewModelState.mapLatest { it.showBottomSheet }.distinctUntilChanged(),
        ::CoffeeShopUiState
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CoffeeShopUiState(
            topBarState = CoffeeShopUiState.TopBarState(
                searchFieldState = viewModelState.value.searchFieldState,
                selectedSorting = sortingProductRepository.selectedSorting.value.getUiStringValue(),
                hasAppliedFilters = false,
                favoriteFilterState = CoffeeShopUiState.FavoriteFilterState(false, false),
                countInCart = 0
            ),
            listState = ListState.Loading,
            showBottomSheet = null
        )
    )

    private val scrollToTopOnFilterChangesFlow = combine(
        filterRepository.isAnyInteractingSelectedFiltersFlow()
            .flatMapLatest { hasInteractingFilters ->
                if (hasInteractingFilters) {
                    emptyFlow()
                } else {
                    filterRepository.getSelectedFiltersFlow()
                }
            },
        viewModelState.value.searchFieldTextFlow,
        sortingProductRepository.selectedSorting
    ) { _, _, _ ->
        true
    }.drop(1).mapLatest {
        viewModelState.update { it.copy(scrollToTop = true) }
    }

    private val _navigationEvents = ExactlyOnceEventFlow<CoffeeShopNavigationEvent>()
    val navigationEvents: Flow<CoffeeShopNavigationEvent> = _navigationEvents

    init {
        refresh()
        scrollToTopOnFilterChangesFlow.launchIn(viewModelScope)
    }

    fun updateFilterCompanySelected(companyId: String, selected: Boolean) {
        viewModelScope.launch {
            filterRepository.updateCompanyFilterSelected(companyId, selected)
        }
    }

    fun onCoffeeProductItemAction(itemAction: CoffeeProductItemAction) {
        when (itemAction) {
            is CoffeeProductItemAction.FavoriteClick ->
                updateFavoriteCoffeeProduct(itemAction.id, itemAction.checked)

            is CoffeeProductItemAction.AddToCartClick,
            is CoffeeProductItemAction.ItemClick ->
                navigateTo(CoffeeShopNavigationEvent.ProductDetails(itemAction.id))
        }
    }

    private fun updateFavoriteCoffeeProduct(productId: String, favorite: Boolean) {
        viewModelScope.launch {
            userPreferencesDataSource.setFavoriteCoffeeProductId(productId, favorite)
        }
    }

    fun dismissBottomSheet() {
        viewModelState.update { it.copy(showBottomSheet = null) }
    }

    fun onScrollToTopComplete() {
        viewModelState.update { it.copy(scrollToTop = false) }
    }

    fun refresh() {
        viewModelScope.launch {
            withContext(ioDispatcher) { // TODO remove after adding a sync instead of a call database.clearAllTables()
                viewModelState.update { it.copy(isLoadingProductList = true) }
                coffeeShopFetcher.fetchAndPopulateDatabase() // TODO add error messages Handler
                viewModelState.update { it.copy(isLoadingProductList = false) }
            }
        }
    }

    fun onTopBarAction(action: TopAppBarAction) {
        when (action) {
            is TopAppBarAction.FavoriteFilterClick -> updateFavoriteFilterSelected(action.checked)
            TopAppBarAction.CartClick -> navigateTo(CoffeeShopNavigationEvent.Cart)
            TopAppBarAction.FiltersClick -> showBottomSheet(CoffeeShopBottomSheets.Filters)
            TopAppBarAction.SortingClick -> showBottomSheet(CoffeeShopBottomSheets.Sorting)
        }
    }

    private fun navigateTo(navigation: CoffeeShopNavigationEvent) {
        _navigationEvents.send(navigation)
    }

    private fun showBottomSheet(bottomSheet: CoffeeShopBottomSheets) {
        viewModelState.update { it.copy(showBottomSheet = bottomSheet) }
    }

    private fun updateFavoriteFilterSelected(selected: Boolean) {
        viewModelScope.launch {
            filterRepository.updateFavoriteFilterSelected(selected)
        }
    }

}

private data class CoffeeShopViewModelState(
    val searchFieldState: TextFieldState = TextFieldState(),
    val scrollToTop: Boolean = false,
    val isLoadingProductList: Boolean = false,
    val showBottomSheet: CoffeeShopBottomSheets? = null
) {
    @OptIn(FlowPreview::class)
    val searchFieldTextFlow = snapshotFlow { searchFieldState.text.trim() }
        .debounce(500)
}

private fun filteredCoffeeProductsFlow(
    coffeeShopRepository: CoffeeShopRepository,
    filterRepository: SelectedFiltersCoffeeProductRepository
): Flow<List<CoffeeProductShortWithCompany>> = combine(
    coffeeShopRepository.getAllCoffeeProductShortWithCompanyFlow(),
    filterRepository.getCoffeeProductIdsBySelectedFilters()
) { coffeeBeanProducts, productIdsBySelectedFilters ->
    val isAnyFilterSelected = filterRepository.isAnyFilterSelected()
    coffeeBeanProducts
        .filterIf(condition = { isAnyFilterSelected }) { coffeeProduct ->
            coffeeProduct.id in productIdsBySelectedFilters
        }
}.distinctUntilChanged()

private fun Flow<List<CoffeeProductShortWithCompany>>.filterBySearch(
    searchRepository: SearchCoffeeProductRepository,
    searchFieldTextFlow: Flow<CharSequence>
): Flow<List<CoffeeProductShortWithCompany>> =
    combine(searchFieldTextFlow) { coffeeBeanProducts, searchText ->
        if (searchText.length < ACTIVE_SEARCH_ON_TEXT_LENGTH) return@combine coffeeBeanProducts

        val searchCoffeeProductIds = searchRepository.searchCoffeeProductIds(
            useFilterIds = true,
            filterIds = coffeeBeanProducts.map { it.id }.toSet(),
            query = searchText.toString()
        ).toSet()
        coffeeBeanProducts.filter { coffeeProduct -> coffeeProduct.id in searchCoffeeProductIds }
    }.distinctUntilChanged()

private fun Flow<List<CoffeeProductShortWithCompany>>.sortedBy(
    sortingProductRepository: SortingCoffeeProductRepository
): Flow<List<CoffeeProductShortWithCompany>> =
    combine(sortingProductRepository.selectedSorting) { coffeeBeanProducts, selectedSorting ->
        coffeeBeanProducts.sortedBy(selectedSorting)
    }

private fun List<CoffeeProductShortWithCompany>.sortedBy(
    sortProduct: SortingCoffeeProduct
): List<CoffeeProductShortWithCompany> = when (sortProduct) {
    SortingCoffeeProduct.Cheap -> sortedBy { it.price.toInt() }
    SortingCoffeeProduct.Expensive -> sortedByDescending { it.price.toInt() }
    SortingCoffeeProduct.NewestArrivals -> sortedByDescending { it.createdAt }
}

private fun Flow<List<CoffeeProductShortWithCompany>>.asUiItems(
    favoriteCoffeeBeanProductIdsFlow: Flow<Set<String>>,
): Flow<List<Product>> = combine(
    this,
    favoriteCoffeeBeanProductIdsFlow
) { coffeeBeanProducts, favoriteProductIds ->
    coffeeBeanProducts.map { coffeeProduct ->
        coffeeProduct.asUiItem(isFavorite = coffeeProduct.id in favoriteProductIds)
    }
}.distinctUntilChanged()

private fun listState(
    filteredCoffeeBeanProducts: Flow<List<Product>>,
    filterRepository: SelectedFiltersCoffeeProductRepository,
    viewModelStateFlow: StateFlow<CoffeeShopViewModelState>,
    coffeeCompaniesFlow: Flow<List<CoffeeShopUiState.Company>>
): Flow<ListState> = combine(
    filteredCoffeeBeanProducts,
    viewModelStateFlow.value.searchFieldTextFlow
        .mapLatest { it.length >= ACTIVE_SEARCH_ON_TEXT_LENGTH }
        .distinctUntilChanged(),
    viewModelStateFlow.mapLatest { it.scrollToTop to it.isLoadingProductList }
        .distinctUntilChanged(),
) { coffeeBeanProducts, isSearchActive, (scrollToTop, isLoadingProductList) ->
    when {
        isLoadingProductList -> ListState.Loading
        coffeeBeanProducts.isEmpty() -> {
            val isAnyFilterSelected = filterRepository.isAnyFilterSelected()
            ListState.Empty(hasAppliedFiltersOrSearch = isAnyFilterSelected || isSearchActive)
        }

        else -> ListState.Success(
            coffeeBeanProducts = coffeeBeanProducts,
            scrollToTop = scrollToTop,
            companies = emptyList()
        )
    }
}.distinctUntilChanged()
    .combine(coffeeCompaniesFlow) { listState, coffeeCompanies ->
        if (listState is ListState.Success) {
            listState.copy(companies = coffeeCompanies)
        } else {
            listState
        }
    }
