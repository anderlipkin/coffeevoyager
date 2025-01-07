package com.coffeevoyager.data.shop.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coffeevoyager.data.shop.database.entities.CoffeeProducerInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeProducerInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(producerInfos: List<CoffeeProducerInfoEntity>)

    @Query("SELECT * FROM producer_infos")
    fun getAllCoffeeProducerInfosFlow(): Flow<List<CoffeeProducerInfoEntity>>
}
