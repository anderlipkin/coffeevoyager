package com.coffeevoyager.data.shop.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coffeevoyager.data.shop.database.entities.CoffeeTasteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeTasteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tastes: List<CoffeeTasteEntity>)

    @Query("SELECT * FROM tastes")
    fun getAllCoffeeTastesFlow(): Flow<List<CoffeeTasteEntity>>
}
