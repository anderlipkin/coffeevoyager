package com.coffeevoyager.data.shop.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductFtsEntity

@Dao
interface CoffeeBeanProductFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coffeeBeanProducts: List<CoffeeBeanProductFtsEntity>)

    @Transaction
    @Query(
        value = """
            SELECT product_id FROM coffee_bean_products_fts
            WHERE 
                CASE WHEN :useFilterIds
                    THEN product_id IN (:filterIds)
                    ELSE 1
                END
             AND product_id IN (SELECT product_id FROM coffee_bean_products_fts WHERE coffee_bean_products_fts MATCH :query)
    """
    )
    suspend fun searchAllCoffeeProductIds(
        useFilterIds: Boolean = false,
        filterIds: Set<String> = emptySet(),
        query: String
    ): List<String>
}
