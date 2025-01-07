package com.coffeevoyager.domain.shop

data class CoffeeProductFilterGroup(
    val id: String,
    val type: CoffeeProductFilterGroups,
    val filters: List<CoffeeProductFilter>
)

enum class CoffeeProductFilterGroups(
    val id: String,
    val selectionType: SelectionType,
    val sortOrder: Int
) {
    Favorite(
        id = "favorite",
        selectionType = SelectionType.Single,
        sortOrder = 0
    ),
    Company(
        id = "company",
        selectionType = SelectionType.Multiple,
        sortOrder = 1
    ),
    Country(
        id = "country",
        selectionType = SelectionType.Multiple,
        sortOrder = 2
    ),
    PriceRange(
        id = "price_range",
        selectionType = SelectionType.Range,
        sortOrder = 3
    ),
    Taste(
        id = "taste",
        selectionType = SelectionType.Multiple,
        sortOrder = 4
    );

    enum class SelectionType {
        Single, Multiple, Range
    }

    companion object {
        fun getFilterGroup(id: String) = entries.find { it.id == id }
    }
}
