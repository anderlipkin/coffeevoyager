package com.coffeevoyager.data.shop.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producer_infos")
data class CoffeeProducerInfoEntity(
    @PrimaryKey
    val id: String,
    val country: String,
    val region: String?,
    val farm: String?
)
