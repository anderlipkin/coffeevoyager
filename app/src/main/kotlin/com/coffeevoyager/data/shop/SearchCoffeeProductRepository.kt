package com.coffeevoyager.data.shop

import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductDao
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductFtsDao
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductFtsEntity
import kotlinx.coroutines.flow.first

class SearchCoffeeProductRepository(
    private val coffeeBeanProductDao: CoffeeBeanProductDao,
    private val coffeeBeanProductFtsDao: CoffeeBeanProductFtsDao
) {

    suspend fun populateFtsDatabase() {
        coffeeBeanProductFtsDao.insertAll(
            coffeeBeanProducts = coffeeBeanProductDao.getAllCoffeeBeanProductsFlow().first()
                .map { coffeeProduct ->
                    CoffeeBeanProductFtsEntity(
                        id = coffeeProduct.id,
                        name = coffeeProduct.name
                    )
                }
        )
    }

    suspend fun searchCoffeeProductIds(
        useFilterIds: Boolean = false,
        filterIds: Set<String> = emptySet(),
        query: String
    ) = coffeeBeanProductFtsDao.searchAllCoffeeProductIds(useFilterIds, filterIds, "*$query*")
}
