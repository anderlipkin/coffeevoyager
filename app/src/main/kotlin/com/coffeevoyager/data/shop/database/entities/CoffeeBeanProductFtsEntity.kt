package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "coffee_bean_products_fts")
@Fts4
data class CoffeeBeanProductFtsEntity(
    @ColumnInfo("product_id")
    val id: String,
    val name: String
)
