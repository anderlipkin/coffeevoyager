package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.coffeevoyager.domain.shop.CoffeeProductFilter
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroups

@Entity(
    tableName = "coffee_product_filters",
    foreignKeys = [
        ForeignKey(
            entity = CoffeeProductFilterGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["filter_group_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("filter_group_id")]
)
data class CoffeeProductFilterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "filter_group_id")
    val filterGroupId: String
)

fun CoffeeProductFilterEntity.asDomain(
    coffeeProducts: List<CoffeeBeanProductEntity>,
    filterGroupType: CoffeeProductFilterGroups
): CoffeeProductFilter? {
    val selectionMethod = defaultSelectionMethod(filterGroupType, coffeeProducts) ?: return null
    return CoffeeProductFilter(
        id = id,
        name = name,
        groupId = filterGroupId,
        selectionMethod = selectionMethod,
        enabled = true,
        availableProductCount = 0
    )
}

private fun defaultSelectionMethod(
    filterGroup: CoffeeProductFilterGroups,
    coffeeProducts: List<CoffeeBeanProductEntity>
): SelectionMethod? {
    return when (filterGroup.selectionType) {
        CoffeeProductFilterGroups.SelectionType.Single,
        CoffeeProductFilterGroups.SelectionType.Multiple -> SelectionMethod.Selectable()

        CoffeeProductFilterGroups.SelectionType.Range -> if (filterGroup == CoffeeProductFilterGroups.PriceRange) {
            if (coffeeProducts.isEmpty()) return null
            val minPriceCoffeeProduct = coffeeProducts.minOf { it.price.toIntOrNull() ?: 1 }
            val maxPriceCoffeeProduct = coffeeProducts.maxOf { it.price.toIntOrNull() ?: 1 }
            val range = minPriceCoffeeProduct..maxPriceCoffeeProduct
            SelectionMethod.RangeWithTextFields(value = range, valueRange = range)
        } else {
            error("Not supported type: $filterGroup")
        }
    }
}
