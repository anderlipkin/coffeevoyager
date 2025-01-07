package com.coffeevoyager.data.shop

import androidx.room.withTransaction
import com.coffeevoyager.data.shop.database.CoffeeShopDatabase
import com.coffeevoyager.data.shop.database.dao.CoffeeCompanyDao
import com.coffeevoyager.data.shop.database.dao.CoffeeProducerInfoDao
import com.coffeevoyager.data.shop.database.dao.CoffeeTasteDao
import com.coffeevoyager.data.shop.database.dao.FilterCoffeeProductDao
import com.coffeevoyager.data.shop.database.entities.CoffeeProductFilterEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeProductFilterGroupEntity
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroups
import com.coffeevoyager.domain.shop.CustomCoffeeProductFilters
import kotlinx.coroutines.flow.first

class FilterCoffeeProductRepository internal constructor(
    private val filterCoffeeProductDao: FilterCoffeeProductDao,
    private val coffeeCompanyDao: CoffeeCompanyDao,
    private val coffeeProducerInfoDao: CoffeeProducerInfoDao,
    private val coffeeTasteDao: CoffeeTasteDao,
    private val coffeeShopDatabase: CoffeeShopDatabase
) {

    suspend fun populateFiltersDatabase() {
        val filtersByCompany = coffeeCompanyDao.getAllCompaniesFlow().first().map { company ->
            CoffeeProductFilterEntity(
                id = company.id,
                name = company.name,
                filterGroupId = CoffeeProductFilterGroups.Company.id
            )
        }
        val filtersByProducerInfo = coffeeProducerInfoDao.getAllCoffeeProducerInfosFlow().first()
            .map { producer ->
                CoffeeProductFilterEntity(
                    id = producer.id,
                    name = producer.country,
                    filterGroupId = CoffeeProductFilterGroups.Country.id
                )
            }
        val filtersByTaste = coffeeTasteDao.getAllCoffeeTastesFlow().first().map { taste ->
            CoffeeProductFilterEntity(
                id = taste.id,
                name = taste.name,
                filterGroupId = CoffeeProductFilterGroups.Taste.id
            )
        }
        val customFilters = CustomCoffeeProductFilters.entries.map { filter ->
            CoffeeProductFilterEntity(
                id = filter.id,
                name = filter.id,
                filterGroupId = filter.group.id
            )
        }
        val allFilters = filtersByCompany + filtersByProducerInfo + filtersByTaste + customFilters
        coffeeShopDatabase.withTransaction {
            filterCoffeeProductDao.insertAllFilterGroup(
                filterGroups = CoffeeProductFilterGroups.entries.map { filterGroup ->
                    CoffeeProductFilterGroupEntity(id = filterGroup.id)
                }
            )
            filterCoffeeProductDao.insertAllFilter(allFilters)
        }
    }

}
