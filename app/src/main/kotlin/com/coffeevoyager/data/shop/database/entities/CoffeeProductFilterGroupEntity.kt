package com.coffeevoyager.data.shop.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroup
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroups

@Entity("coffee_product_filter_groups")
data class CoffeeProductFilterGroupEntity(
    @PrimaryKey
    val id: String
)

data class CoffeeProductFilterGroupFullEntity(
    @Embedded
    val filterGroup: CoffeeProductFilterGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "filter_group_id"
    )
    val filters: List<CoffeeProductFilterEntity>
)

fun CoffeeProductFilterGroupFullEntity.asDomain(
    coffeeProducts: List<CoffeeBeanProductEntity>
): CoffeeProductFilterGroup {
    val filterGroupType =
        CoffeeProductFilterGroups.getFilterGroup(filterGroup.id)!!
    return CoffeeProductFilterGroup(
        id = filterGroup.id,
        type = filterGroupType,
        filters = filters.mapNotNull { filter ->
            filter.asDomain(coffeeProducts, filterGroupType)
        }
    )
}
