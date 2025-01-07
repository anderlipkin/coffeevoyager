package com.coffeevoyager.features.shop.products.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeevoyager.R
import com.coffeevoyager.core.common.extension.toIntRange
import com.coffeevoyager.core.ui.ImmutableList
import com.coffeevoyager.core.ui.UiStringValue
import com.coffeevoyager.data.shop.SelectedFiltersCoffeeProductRepository
import com.coffeevoyager.domain.shop.CoffeeProductFilter
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod.RangeWithTextFields.SliderState
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod.RangeWithTextFields.TextState
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroup
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroups
import com.coffeevoyager.domain.shop.Currency
import com.coffeevoyager.domain.shop.CustomCoffeeProductFilters
import com.coffeevoyager.features.shop.products.filters.FilterUiData.Attribute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

private const val DEFAULT_FILTER_GROUP_COLLAPSED_THRESHOLD = 10

class FiltersCoffeeShopViewModel(
    private val filterRepository: SelectedFiltersCoffeeProductRepository
) : ViewModel() {

    private val filterGroupsMapFlow = filterRepository.getFilterGroupsFlow()
        .mapLatest { it.associateBy { it.id } }
        .stateIn(viewModelScope, WhileSubscribed(5000), emptyMap())

    private val expandedFilterGroupIdsFlow = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val resetButtonVisible = filterRepository.getSelectedFiltersFlow()
        .mapLatest { it.any { it.groupId != CoffeeProductFilterGroups.Favorite.id } }
        .stateIn(viewModelScope, WhileSubscribed(5000), false)

    val filterGroupsUiData = combine(
        filterGroupsMapFlow.mapLatest { filterGroupsMap ->
            filterGroupsMap.filterKeys { it != CoffeeProductFilterGroups.Favorite.id }
                .values
                .sortedBy { it.type.sortOrder }
        },
        expandedFilterGroupIdsFlow
    ) { filterGroups, expandedFilterGroupIds ->
        ImmutableList(
            filterGroups
                .map { filterGroup ->
                    val isExpandedGroup = expandedFilterGroupIds[filterGroup.id]
                        ?: (filterGroup.filters.size < DEFAULT_FILTER_GROUP_COLLAPSED_THRESHOLD)
                    filterGroup.asUiData(expanded = isExpandedGroup)
                }
        )
    }.stateIn(viewModelScope, WhileSubscribed(5000), ImmutableList())

    private val _userScrollEnabled = MutableStateFlow(true)
    val userScrollEnabled: StateFlow<Boolean> = _userScrollEnabled

    private fun setUserScrollEnabled(enabled: Boolean) {
        _userScrollEnabled.value = enabled
    }

    fun onResetClick() {
        filterRepository.resetSelectedFilters(excludeFilterIds = setOf(CustomCoffeeProductFilters.Favorite.id))
    }

    fun onFilterGroupExpandedChange(filterGroupId: String, expanded: Boolean) {
        expandedFilterGroupIdsFlow.update { expandedFilterGroups ->
            expandedFilterGroups.toMutableMap().apply {
                put(filterGroupId, expanded)
            }
        }
    }

    fun onFilterItemAction(filterAction: FilterItemAction) {
        when (filterAction) {
            is FilterItemAction.SelectedChange ->
                updateSelectableFilterData(filterAction)

            is FilterItemAction.Slider ->
                updateSliderRangeFilterData(filterAction)

            is FilterItemAction.TextFieldRangeChange ->
                updateTextRangeFilterData(filterAction)
        }
    }

    private fun updateSelectableFilterData(
        selectedChangeData: FilterItemAction.SelectedChange
    ) {
        viewModelScope.launch {
            filterRepository.updateSelectedFilterData(
                filterId = selectedChangeData.filterId,
                selectionMethod = SelectionMethod.Selectable(selected = selectedChangeData.selected)
            )
        }
    }

    private fun updateSliderRangeFilterData(
        rangeChangeData: FilterItemAction.Slider
    ) {
        setUserScrollEnabled(rangeChangeData.state == SliderState.Idle)
        val newRange = rangeChangeData.value.toIntRange()
        val currentSelectionMethod = rangeChangeData.currentSelectionMethod
        viewModelScope.launch {
            filterRepository.updateSelectedFilterData(
                filterId = rangeChangeData.filterId,
                selectionMethod = SelectionMethod.RangeWithTextFields(
                    value = newRange,
                    valueRange = currentSelectionMethod.valueRange,
                    minValueText = newRange.first.toString(),
                    maxValueText = newRange.last.toString(),
                    sliderState = rangeChangeData.state,
                    textState = currentSelectionMethod.textState
                )
            )
        }
    }

    private fun updateTextRangeFilterData(
        rangeChangeData: FilterItemAction.TextFieldRangeChange
    ) {
        val oldRangeData = rangeChangeData.currentSelectionMethod
        val oldValueRange = oldRangeData.valueRange
        val (minValue, maxValue) = when (rangeChangeData.state) {
            TextState.Focused -> rangeChangeData.minValue to rangeChangeData.maxValue
            TextState.Idle -> {
                val minValueInt =
                    (rangeChangeData.minValue.toIntOrNull() ?: oldValueRange.first)
                        .coerceIn(oldValueRange)
                val maxValueInt =
                    (rangeChangeData.maxValue.toIntOrNull() ?: oldValueRange.last)
                        .coerceIn(oldValueRange)
                minValueInt.toString() to maxValueInt.toString()
            }
        }
        val (minValueInt: Int?, maxValueInt: Int?) =
            swapMinMaxIfNeed(rangeChangeData, minValue, maxValue)
        val rangeValue = when (rangeChangeData.state) {
            TextState.Focused -> {
                if (minValueInt != null && maxValueInt != null && minValueInt <= maxValueInt) {
                    minValueInt..maxValueInt
                } else {
                    oldRangeData.value
                }
            }

            TextState.Idle -> minValueInt!!..maxValueInt!!
        }
        viewModelScope.launch {
            filterRepository.updateSelectedFilterData(
                filterId = rangeChangeData.filterId,
                selectionMethod = SelectionMethod.RangeWithTextFields(
                    value = rangeValue,
                    valueRange = oldValueRange,
                    minValueText = minValueInt?.toString() ?: minValue,
                    maxValueText = maxValueInt?.toString() ?: maxValue,
                    textState = rangeChangeData.state,
                    sliderState = oldRangeData.sliderState
                )
            )
        }
    }

    private fun swapMinMaxIfNeed(
        rangeChangeData: FilterItemAction.TextFieldRangeChange,
        minValue: String,
        maxValue: String
    ): Pair<Int?, Int?> = when (rangeChangeData.state) {
        TextState.Focused -> minValue.toIntOrNull() to maxValue.toIntOrNull()
        TextState.Idle -> {
            val minValueInt = minValue.toIntOrNull()
            val maxValueInt = maxValue.toIntOrNull()
            if (minValueInt != null && maxValueInt != null) {
                min(minValueInt, maxValueInt) to max(minValueInt, maxValueInt)
            } else {
                minValueInt to maxValueInt
            }
        }
    }
}

data class FilterGroupsUiData(
    val id: String,
    val type: CoffeeProductFilterGroups,
    val name: UiStringValue,
    val expanded: Boolean,
    val filters: ImmutableList<FilterUiData>
)

data class FilterUiData(
    val id: String,
    val name: String,
    val enabled: Boolean,
    val availableProductCount: Int,
    val selectionMethod: SelectionMethod,
    val attribute: Attribute? = null
) {
    sealed class Attribute {
        data class Currency(val currency: String) : Attribute()
    }
}

sealed class FilterItemAction {
    abstract val filterId: String

    data class SelectedChange(
        override val filterId: String,
        val selected: Boolean
    ) : FilterItemAction()

    data class Slider(
        override val filterId: String,
        val value: ClosedFloatingPointRange<Float>,
        val state: SliderState,
        val currentSelectionMethod: SelectionMethod.RangeWithTextFields
    ) : FilterItemAction()

    data class TextFieldRangeChange(
        override val filterId: String,
        val minValue: String,
        val maxValue: String,
        val state: TextState,
        val currentSelectionMethod: SelectionMethod.RangeWithTextFields
    ) : FilterItemAction()
}

fun CoffeeProductFilterGroup.asUiData(expanded: Boolean): FilterGroupsUiData {
    val currency = Currency.UAH.symbol
    return FilterGroupsUiData(
        id = id,
        type = type,
        name = type.getUiTitle(currency),
        expanded = expanded,
        filters = ImmutableList(
            filters
                .filter { filter -> filter.enabled }
                .map { filter ->
                    FilterUiData(
                        id = filter.id,
                        name = filter.name,
                        selectionMethod = filter.selectionMethod,
                        attribute = filter.getUiFilterAttribute(currency),
                        enabled = filter.enabled,
                        availableProductCount = filter.availableProductCount
                    )
                }
        )
    )
}

fun CoffeeProductFilter.getUiFilterAttribute(currency: String): Attribute? =
    when (groupType) {
        CoffeeProductFilterGroups.PriceRange -> {
            Attribute.Currency(currency)
        }

        else -> null
    }

fun CoffeeProductFilterGroups.getUiTitle(currency: String? = null): UiStringValue =
    when (this) {
        CoffeeProductFilterGroups.Favorite -> UiStringValue.StringResource(R.string.favorite)
        CoffeeProductFilterGroups.Company -> UiStringValue.StringResource(R.string.company)
        CoffeeProductFilterGroups.Country -> UiStringValue.StringResource(R.string.country)
        CoffeeProductFilterGroups.Taste -> UiStringValue.StringResource(R.string.taste)
        CoffeeProductFilterGroups.PriceRange ->
            UiStringValue.StringResource(R.string.price_with_currency, currency!!)
    }
