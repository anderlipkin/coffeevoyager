package com.coffeevoyager.domain.shop

import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod.RangeWithTextFields.SliderState

data class CoffeeProductFilter(
    val id: String,
    val name: String,
    val groupId: String,
    val enabled: Boolean,
    val availableProductCount: Int,
    val selectionMethod: SelectionMethod
) {
    val groupType: CoffeeProductFilterGroups? =
        CoffeeProductFilterGroups.getFilterGroup(groupId)

    sealed class SelectionMethod {
        data class Selectable(val selected: Boolean = false) : SelectionMethod()

        data class RangeWithTextFields(
            val value: IntRange,
            val valueRange: IntRange,
            val minValueText: String = value.first.toString(),
            val maxValueText: String = value.last.toString(),
            val textState: TextState = TextState.Idle,
            val sliderState: SliderState = SliderState.Idle
        ) : SelectionMethod() {
            val steps: Int
                get() = valueRange.last - valueRange.first - 1

            enum class TextState {
                Idle, Focused
            }

            enum class SliderState {
                Idle, Interacting
            }
        }
    }
}

fun SelectionMethod.isInteracting(): Boolean =
    when (this) {
        is SelectionMethod.RangeWithTextFields -> sliderState == SliderState.Interacting
        is SelectionMethod.Selectable -> false
    }

fun CoffeeProductFilter.asSelectedFilter() =
    SelectedCoffeeProductFilter(
        id = id,
        groupId = groupId,
        groupType = groupType!!,
        selectionMethod = selectionMethod
    )
