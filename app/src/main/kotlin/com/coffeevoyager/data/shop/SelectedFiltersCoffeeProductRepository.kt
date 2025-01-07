package com.coffeevoyager.data.shop

import android.util.Log
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductDao
import com.coffeevoyager.data.shop.database.dao.FilterCoffeeProductDao
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductFullEntity
import com.coffeevoyager.data.shop.database.entities.asDomain
import com.coffeevoyager.data.user.datastore.UserPreferencesDataSource
import com.coffeevoyager.domain.shop.CoffeeProductFilter
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroup
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroups
import com.coffeevoyager.domain.shop.CustomCoffeeProductFilters
import com.coffeevoyager.domain.shop.SelectedCoffeeProductFilter
import com.coffeevoyager.domain.shop.asSelectedFilter
import com.coffeevoyager.domain.shop.isInteracting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update

private const val LOG_TAG = "SelectedFiltersCoffeeProductRepository"

class SelectedFiltersCoffeeProductRepository(
    private val filterCoffeeProductDao: FilterCoffeeProductDao,
    private val coffeeBeanProductDao: CoffeeBeanProductDao,
    private val userPreferencesDataSource: UserPreferencesDataSource
) {

    private val _filtersSnapshotFlow =
        MutableStateFlow<Map<String, CoffeeProductFilter>>(emptyMap())
    private val filtersSnapshotFlow: StateFlow<Map<String, CoffeeProductFilter>> =
        _filtersSnapshotFlow

    private val _selectedIdsToFilterMapFlow =
        MutableStateFlow<Map<String, SelectedCoffeeProductFilter>>(emptyMap())

    private val selectedFiltersFlow: Flow<List<SelectedCoffeeProductFilter>> =
        _selectedIdsToFilterMapFlow.mapLatest { it.values.toList() }

    fun isAnyFilterSelected(): Boolean = _selectedIdsToFilterMapFlow.value.isNotEmpty()

    fun getSelectedFiltersFlow(): Flow<List<SelectedCoffeeProductFilter>> = selectedFiltersFlow

    fun isAnyInteractingSelectedFiltersFlow(): Flow<Boolean> =
        selectedFiltersFlow.mapLatest { selectedFilters ->
            selectedFilters.isAnyInteracting()
        }.distinctUntilChanged()

    fun getSelectedCompanyFilterIdsFlow(): Flow<Set<String>> =
        selectedFiltersFlow.mapLatest { selectedFilters ->
            selectedFilters.filter { it.groupType == CoffeeProductFilterGroups.Company }
                .map { it.id }.toSet()
        }.distinctUntilChanged()

    fun isFavoriteFilterSelectedFlow(): Flow<Boolean> =
        selectedFiltersFlow.mapLatest { selectedFilters ->
            selectedFilters.any { it.groupType == CoffeeProductFilterGroups.Favorite }
        }.distinctUntilChanged()

    fun getFilterGroupsFlow(): Flow<List<CoffeeProductFilterGroup>> {
        _filtersSnapshotFlow.update { emptyMap() }
        val filterGroupsFullFlow = flow {
            val coffeeProducts = coffeeBeanProductDao.getAllCoffeeBeanProducts()
            val filterGroups = filterCoffeeProductDao.getAllFilterGroupFull()
                .map { filterGroupEntity -> filterGroupEntity.asDomain(coffeeProducts) }
            emit(filterGroups)
        }

        return combine(
            filterGroupsFullFlow,
            _selectedIdsToFilterMapFlow.mapLatest { it.values.groupBy { it.groupId } },
            coffeeBeanProductDao.getAllCoffeeBeanProductFullFlow().distinctUntilChanged(),
            getFavoriteProductIdsFlow()
        ) { filterGroups, selectedFilterByGroupMap, coffeeProducts, favoriteProductIds ->
            val isAnyFilterInteracting =
                selectedFilterByGroupMap.values.flatten().isAnyInteracting()
            filterGroups.map { filterGroup ->
                val selectedFiltersIdMap =
                    selectedFilterByGroupMap[filterGroup.id]?.associateBy { it.id }
                val updatedFilters = filterGroup.filters.map { filter ->
                    val selectedFilter = selectedFiltersIdMap?.get(filter.id)
                    val updatedFilter = filter.copy(
                        selectionMethod = selectedFilter?.selectionMethod ?: filter.selectionMethod
                    )
                    val availableProductCount = getFutureAvailableProductCount(
                        filter = updatedFilter,
                        isSelected = selectedFilter != null,
                        isAnyFilterInteracting = isAnyFilterInteracting,
                        selectedFilterByGroupMap = selectedFilterByGroupMap,
                        coffeeProducts = coffeeProducts,
                        favoriteProductIds = favoriteProductIds
                    )
                    updatedFilter.copy(
                        enabled = availableProductCount > 0 || selectedFilter != null,
                        availableProductCount = availableProductCount
                    )
                }
                filterGroup.copy(filters = updatedFilters)
            }.also { updatedFilterGroups ->
                _filtersSnapshotFlow.update {
                    updatedFilterGroups.map { it.filters }.flatten().associateBy { it.id }
                }
            }
        }
    }

    fun getCoffeeProductIdsBySelectedFilters(): Flow<Set<String>> =
        combine(
            coffeeBeanProductDao.getAllCoffeeBeanProductFullFlow().distinctUntilChanged(),
            selectedFiltersFlow,
            getFavoriteProductIdsFlow()
        ) { coffeeProducts, filters, favoriteProductIds ->
            coffeeProducts.filterBy(filters, favoriteProductIds)
                .map { it.product.id }
                .toSet()
        }.distinctUntilChanged()

    fun resetSelectedFilters(excludeFilterIds: Set<String> = emptySet()) {
        _selectedIdsToFilterMapFlow.update { selectedIdsToFilterMap ->
            if (excludeFilterIds.isEmpty()) {
                emptyMap()
            } else {
                selectedIdsToFilterMap.filterKeys { it in excludeFilterIds }
            }
        }
    }

    suspend fun updateCompanyFilterSelected(companyId: String, selected: Boolean) {
        updateSelectedFilterData(
            filterId = companyId,
            selectionMethod = SelectionMethod.Selectable(selected)
        )
    }

    suspend fun updateFavoriteFilterSelected(selected: Boolean) {
        updateSelectedFilterData(
            filterId = CustomCoffeeProductFilters.Favorite.id,
            selectionMethod = SelectionMethod.Selectable(selected)
        )
    }

    suspend fun updateSelectedFilterData(filterId: String, selectionMethod: SelectionMethod) {
        val filter = filterCoffeeProductDao.getFilterById(filterId) ?: run {
            Log.e(LOG_TAG, "Failed to updateSelectedFilterData. Not found filterId: $filterId")
            return
        }
        val filterGroupId = filter.filterGroupId
        val newSelectedFilter = SelectedCoffeeProductFilter(
            id = filterId,
            groupId = filterGroupId,
            groupType = CoffeeProductFilterGroups.getFilterGroup(filterGroupId)!!,
            selectionMethod = selectionMethod
        )
        _selectedIdsToFilterMapFlow.update { selectedFiltersMap ->
            val selectedFilters = selectedFiltersMap.toMutableMap()
            val newFilterId = newSelectedFilter.id
            when (selectionMethod) {
                is SelectionMethod.RangeWithTextFields -> {
                    if (selectionMethod.value == selectionMethod.valueRange && !selectionMethod.isInteracting()) {
                        selectedFilters.remove(newFilterId)
                    } else {
                        selectedFilters[newFilterId] = newSelectedFilter
                    }
                }

                is SelectionMethod.Selectable -> {
                    if (selectionMethod.selected) {
                        selectedFilters[newFilterId] = newSelectedFilter
                    } else {
                        selectedFilters.remove(newFilterId)
                    }
                }
            }
            selectedFilters
        }
    }

    private fun getFavoriteProductIdsFlow(): Flow<Set<String>> =
        isFavoriteFilterSelectedFlow()
            .flatMapLatest { isFavoriteFilterSelected ->
                val favoriteProductIdsFlow =
                    userPreferencesDataSource.favoriteCoffeeBeanProductIdsFlow
                if (isFavoriteFilterSelected) {
                    favoriteProductIdsFlow
                } else {
                    flowOf(favoriteProductIdsFlow.firstOrNull() ?: emptySet())
                }
            }

    private fun getFutureAvailableProductCount(
        filter: CoffeeProductFilter,
        isSelected: Boolean,
        isAnyFilterInteracting: Boolean,
        selectedFilterByGroupMap: Map<String, List<SelectedCoffeeProductFilter>>,
        coffeeProducts: List<CoffeeBeanProductFullEntity>,
        favoriteProductIds: Set<String>
    ): Int {
        val availableProductCount = when {
            isSelected || isAnyFilterInteracting ->
                filtersSnapshotFlow.value[filter.id]?.availableProductCount ?: 0

            else -> {
                val futureFilters =
                    selectedFilterByGroupMap.toMutableMap().apply {
                        remove(filter.groupId)
                    }.values.flatten() + filter.asSelectedFilter()
                coffeeProducts.filterBy(
                    selectedFilters = futureFilters,
                    favoriteProductIds = favoriteProductIds
                ).count()
            }
        }
        return availableProductCount
    }
}

private fun List<CoffeeBeanProductFullEntity>.filterBy(
    selectedFilters: List<SelectedCoffeeProductFilter>,
    favoriteProductIds: Set<String>
): List<CoffeeBeanProductFullEntity> {
    if (selectedFilters.isEmpty()) return this
    var result = this.asSequence()
    selectedFilters.groupBy { it.groupType }.forEach { (filterGroupType, filters) ->
        val filterIds = filters.map { it.id }.toSet()
        when (filterGroupType) {
            CoffeeProductFilterGroups.Company -> {
                result = result.filter { coffeeProduct ->
                    coffeeProduct.company.id in filterIds
                }
            }

            CoffeeProductFilterGroups.Country -> {
                result = result.filter { coffeeProduct ->
                    coffeeProduct.producerInfo.id in filterIds
                }
            }

            CoffeeProductFilterGroups.Taste -> {
                result = result.filter { coffeeProduct ->
                    val coffeeProductTastes = coffeeProduct.tastes.map { it.id }.toSet()
                    filterIds.any { id -> id in coffeeProductTastes }
                }
            }

            CoffeeProductFilterGroups.PriceRange -> {
                val selectionMethod =
                    filters.first().selectionMethod as SelectionMethod.RangeWithTextFields
                if (!selectionMethod.isInteracting()) {
                    result = result.filter { coffeeProduct ->
                        coffeeProduct.product.price.toInt() in selectionMethod.value
                    }
                }
            }

            CoffeeProductFilterGroups.Favorite -> {
                result = result.filter { coffeeProduct ->
                    coffeeProduct.product.id in favoriteProductIds
                }
            }
        }
    }
    return result.toList()
}

private fun List<SelectedCoffeeProductFilter>.isAnyInteracting() =
    any { filter -> filter.selectionMethod.isInteracting() }
