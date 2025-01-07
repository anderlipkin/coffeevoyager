package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tastes")
data class CoffeeTasteEntity(
    @PrimaryKey
    @ColumnInfo("taste_id")
    val id: String,
    val name: String
)
