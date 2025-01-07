package com.coffeevoyager.data.shop.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductFullEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToOption
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToRecipe
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToTaste
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductWithCompany
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeBeanProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coffeeBeanProducts: List<CoffeeBeanProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCoffeeBeanProductToRecipe(coffeeBeanProducts: List<CoffeeBeanProductToRecipe>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCoffeeBeanProductToOption(coffeeBeanProducts: List<CoffeeBeanProductToOption>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCoffeeBeanProductToTaste(coffeeBeanProducts: List<CoffeeBeanProductToTaste>)

    @Query("SELECT * FROM coffee_bean_products")
    fun getAllCoffeeBeanProductsFlow(): Flow<List<CoffeeBeanProductEntity>>

    @Query("SELECT product_id FROM coffee_bean_products WHERE product_id in (:ids)")
    fun getCoffeeBeanProductsIdsFlow(ids: Set<String>): Flow<List<String>>

    @Query("SELECT * FROM coffee_bean_products")
    suspend fun getAllCoffeeBeanProducts(): List<CoffeeBeanProductEntity>

    @Transaction
    @Query("SELECT * FROM coffee_bean_products")
    fun getAllCoffeeBeanProductFullFlow(): Flow<List<CoffeeBeanProductFullEntity>>

    @Transaction
    @Query("SELECT * FROM coffee_bean_products")
    fun getAllCoffeeBeanProductWithCompanyFlow(): Flow<List<CoffeeBeanProductWithCompany>>

}
