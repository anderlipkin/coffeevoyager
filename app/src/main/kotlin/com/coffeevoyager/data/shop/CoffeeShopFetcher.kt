package com.coffeevoyager.data.shop

import androidx.room.withTransaction
import com.coffeevoyager.data.shop.database.CoffeeShopDatabase
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanOptionDao
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductDao
import com.coffeevoyager.data.shop.database.dao.CoffeeCompanyDao
import com.coffeevoyager.data.shop.database.dao.CoffeeProducerInfoDao
import com.coffeevoyager.data.shop.database.dao.CoffeeRecipeDao
import com.coffeevoyager.data.shop.database.dao.CoffeeTasteDao
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanOptionEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToOption
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToRecipe
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToTaste
import com.coffeevoyager.data.shop.database.entities.CoffeeCompanyEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeProducerInfoEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeRecipeEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeTasteEntity
import com.coffeevoyager.data.shop.database.entities.asEntity
import com.coffeevoyager.data.shop.dto.CoffeeBeanOptionDto
import com.coffeevoyager.data.shop.dto.CoffeeBeanProductDto
import com.coffeevoyager.data.shop.dto.CoffeeCompanyDto
import com.coffeevoyager.data.shop.dto.CoffeeProducerInfoDto
import com.coffeevoyager.data.shop.dto.CoffeeRecipeDto
import com.coffeevoyager.data.shop.dto.CoffeeTasteDto
import com.coffeevoyager.data.shop.remote.CoffeeShopApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CoffeeShopFetcher internal constructor(
    private val apiService: CoffeeShopApiService,
    private val coffeeBeanProductDao: CoffeeBeanProductDao,
    private val coffeeRecipeDao: CoffeeRecipeDao,
    private val coffeeCompanyDao: CoffeeCompanyDao,
    private val coffeeBeanOptionDao: CoffeeBeanOptionDao,
    private val coffeeProducerInfoDao: CoffeeProducerInfoDao,
    private val coffeeTasteDao: CoffeeTasteDao,
    private val filterCoffeeProductRepository: FilterCoffeeProductRepository,
    private val searchCoffeeProductRepository: SearchCoffeeProductRepository,
    private val coffeeShopDatabase: CoffeeShopDatabase
) {

    suspend fun fetchAndPopulateDatabase(): Result<Unit> {
        return coroutineScope {
            coffeeShopDatabase.clearAllTables() // TODO add sync (delete, update)
            val remoteCoffeeBeans = apiService.getAllCoffeeBeans()
                .getOrElse { return@coroutineScope Result.failure(it) }
            val companyIds = remoteCoffeeBeans.map { it.companyId }.distinct()
            val coffeeRecipeIds =
                remoteCoffeeBeans.map { it.coffeeRecipeIds }.flatten().distinct()
            val coffeeOptionIds = remoteCoffeeBeans.map { it.optionIds }.flatten().distinct()
            val coffeeProducerIds = remoteCoffeeBeans.map { it.producerInfoId }.distinct()
            val coffeeTasteIds = remoteCoffeeBeans.map { it.tasteIds }.flatten().distinct()

            val remoteCompanies = async { apiService.getCoffeeCompanyByIds(companyIds) }
            val remoteRecipes = async { apiService.getCoffeeRecipeByIds(coffeeRecipeIds) }
            saveDataWithTransaction(
                remoteCoffeeBeans = remoteCoffeeBeans,
                remoteCompanies = remoteCompanies.await()
                    .getOrElse { return@coroutineScope Result.failure(it) },
                remoteRecipes = remoteRecipes.await()
                    .getOrElse { return@coroutineScope Result.failure(it) },
                remoteCoffeeOptions = apiService.getCoffeeOptionByIds(coffeeOptionIds)
                    .getOrElse { return@coroutineScope Result.failure(it) },
                remoteCoffeeProducers = apiService.getCoffeeProducerInfoByIds(coffeeProducerIds)
                    .getOrElse { return@coroutineScope Result.failure(it) },
                remoteCoffeeTastes = apiService.getCoffeeTasteByIds(coffeeTasteIds)
                    .getOrElse { return@coroutineScope Result.failure(it) }
            )
            launch {
                filterCoffeeProductRepository.populateFiltersDatabase()
            }
            launch {
                searchCoffeeProductRepository.populateFtsDatabase()
            }
            Result.success(Unit)
        }
    }

    private suspend fun saveDataWithTransaction(
        remoteCoffeeBeans: List<CoffeeBeanProductDto>,
        remoteCompanies: List<CoffeeCompanyDto>,
        remoteRecipes: List<CoffeeRecipeDto>,
        remoteCoffeeOptions: List<CoffeeBeanOptionDto>,
        remoteCoffeeProducers: List<CoffeeProducerInfoDto>,
        remoteCoffeeTastes: List<CoffeeTasteDto>
    ) {
        return coffeeShopDatabase.withTransaction {
            insertAllCoffeeCompanies(remoteCompanies)
            insertAllRecipes(remoteRecipes)
            insertAllCoffeeOptions(remoteCoffeeOptions)
            insertAllCoffeeProducers(remoteCoffeeProducers)
            insertAllCoffeeTastes(remoteCoffeeTastes)
            insertAllCoffeeBeanProducts(remoteCoffeeBeans)
        }
    }

    private suspend fun insertAllCoffeeBeanProducts(coffeeBeanProducts: List<CoffeeBeanProductDto>) {
        coffeeBeanProductDao.insertAll(coffeeBeanProducts.map(CoffeeBeanProductDto::asEntity))
        coffeeBeanProducts.map { coffeeBeanProduct ->
            val productId = coffeeBeanProduct.id
            insertCoffeeBeanProductToRecipe(productId, coffeeBeanProduct.coffeeRecipeIds)
            insertCoffeeBeanProductToOption(productId, coffeeBeanProduct.optionIds)
            insertCoffeeBeanProductToTaste(productId, coffeeBeanProduct.tasteIds)
        }
    }

    private suspend fun insertCoffeeBeanProductToRecipe(
        productId: String,
        recipeIds: List<String>
    ) {
        coffeeBeanProductDao.insertAllCoffeeBeanProductToRecipe(
            recipeIds.map { recipeId ->
                CoffeeBeanProductToRecipe(productId = productId, recipeId = recipeId)
            }
        )
    }

    private suspend fun insertCoffeeBeanProductToOption(
        productId: String,
        optionIds: List<String>
    ) {
        coffeeBeanProductDao.insertAllCoffeeBeanProductToOption(
            optionIds.map { optionId ->
                CoffeeBeanProductToOption(productId = productId, optionId = optionId)
            }
        )
    }

    private suspend fun insertCoffeeBeanProductToTaste(
        productId: String,
        tasteIds: List<String>
    ) {
        coffeeBeanProductDao.insertAllCoffeeBeanProductToTaste(
            tasteIds.map { tasteId ->
                CoffeeBeanProductToTaste(productId = productId, tasteId = tasteId)
            }
        )
    }

    private suspend fun insertAllCoffeeCompanies(companies: List<CoffeeCompanyDto>) {
        coffeeCompanyDao.insertAll(
            companies.map { company ->
                CoffeeCompanyEntity(
                    id = company.id,
                    imageUrl = company.imageUrl,
                    name = company.name,
                    description = company.description
                )
            }
        )
    }

    private suspend fun insertAllRecipes(recipes: List<CoffeeRecipeDto>) {
        coffeeRecipeDao.insertAll(
            recipes.map { recipe ->
                CoffeeRecipeEntity(
                    id = recipe.id,
                    recipe = recipe.recipe,
                    roastType = recipe.roastType
                )
            }
        )
    }

    private suspend fun insertAllCoffeeOptions(options: List<CoffeeBeanOptionDto>) {
        coffeeBeanOptionDao.insertAll(
            options.map { option ->
                CoffeeBeanOptionEntity(
                    id = option.id,
                    name = option.name,
                    values = option.values
                )
            }
        )
    }

    private suspend fun insertAllCoffeeProducers(producers: List<CoffeeProducerInfoDto>) {
        coffeeProducerInfoDao.insertAll(
            producers.map { producer ->
                CoffeeProducerInfoEntity(
                    id = producer.id,
                    country = producer.country,
                    region = producer.region,
                    farm = producer.farm
                )
            }
        )

    }

    private suspend fun insertAllCoffeeTastes(tastes: List<CoffeeTasteDto>) {
        coffeeTasteDao.insertAll(
            tastes.map { taste ->
                CoffeeTasteEntity(id = taste.id, name = taste.name)
            }
        )
    }

}
