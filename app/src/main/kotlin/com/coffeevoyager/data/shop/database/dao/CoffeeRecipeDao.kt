package com.coffeevoyager.data.shop.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.coffeevoyager.data.shop.database.entities.CoffeeRecipeEntity

@Dao
interface CoffeeRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<CoffeeRecipeEntity>)
}
