package com.coffeevoyager.data.shop

import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductDao
import com.coffeevoyager.data.shop.database.dao.CoffeeCompanyDao
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductWithCompany
import com.coffeevoyager.data.shop.database.entities.CoffeeCompanyEntity
import com.coffeevoyager.data.shop.database.entities.asDomain
import com.coffeevoyager.data.user.datastore.UserPreferencesDataSource
import com.coffeevoyager.domain.shop.CoffeeCompany
import com.coffeevoyager.domain.shop.CoffeeProductShortWithCompany
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

class CoffeeShopRepository(
    private val coffeeBeanProductDao: CoffeeBeanProductDao,
    private val coffeeCompanyDao: CoffeeCompanyDao,
    private val userPreferencesDataSource: UserPreferencesDataSource
) {

    fun getAllCoffeeProductShortWithCompanyFlow(): Flow<List<CoffeeProductShortWithCompany>> =
        coffeeBeanProductDao.getAllCoffeeBeanProductWithCompanyFlow()
            .mapLatest { it.map(CoffeeBeanProductWithCompany::asDomain) }
            .distinctUntilChanged()

    fun getFavoriteProductIdsFlow(): Flow<List<String>> =
        userPreferencesDataSource.favoriteCoffeeBeanProductIdsFlow.flatMapLatest {
            coffeeBeanProductDao.getCoffeeBeanProductsIdsFlow(it)
        }

    fun getAllCompaniesFlow(): Flow<List<CoffeeCompany>> =
        coffeeCompanyDao.getAllCompaniesFlow()
            .mapLatest { it.map(CoffeeCompanyEntity::asDomain) }
            .distinctUntilChanged()

    suspend fun getCompaniesById(ids: Set<String>): List<CoffeeCompany> =
        coffeeCompanyDao.getCompaniesById(ids).map(CoffeeCompanyEntity::asDomain)

}
