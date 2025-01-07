package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "options")
data class CoffeeBeanOptionEntity(
    @PrimaryKey
    @ColumnInfo("option_id")
    val id: String,
    val name: String,
    val values: List<String>
)
