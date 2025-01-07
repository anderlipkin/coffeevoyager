package com.coffeevoyager.data.shop.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.coffeevoyager.data.shop.database.entities.CoffeeProductFilterEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeProductFilterGroupEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeProductFilterGroupFullEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterCoffeeProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilterGroup(filterGroups: List<CoffeeProductFilterGroupEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilter(filters: List<CoffeeProductFilterEntity>)

    @Transaction
    @Query("SELECT * FROM coffee_product_filter_groups")
    suspend fun getAllFilterGroupFull(): List<CoffeeProductFilterGroupFullEntity>

    @Transaction
    @Query("SELECT * FROM coffee_product_filter_groups")
    fun getAllFilterGroupFullFlow(): Flow<List<CoffeeProductFilterGroupFullEntity>>

    @Query("SELECT * FROM coffee_product_filters WHERE id in (:ids)")
    suspend fun getFiltersById(ids: List<String>): List<CoffeeProductFilterEntity>

    @Query("SELECT * FROM coffee_product_filters WHERE id = :id")
    suspend fun getFilterById(id: String): CoffeeProductFilterEntity?

}
