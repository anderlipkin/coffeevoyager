package com.coffeevoyager.data.shop.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coffeevoyager.data.shop.database.entities.CoffeeCompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeCompanyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(companies: List<CoffeeCompanyEntity>)

    @Query("SELECT * FROM companies")
    fun getAllCompaniesFlow(): Flow<List<CoffeeCompanyEntity>>

    @Query("SELECT * FROM companies WHERE id in (:ids)")
    suspend fun getCompaniesById(ids: Set<String>): List<CoffeeCompanyEntity>

}
