package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class CoffeeRecipeEntity(
    @PrimaryKey
    @ColumnInfo("recipe_id")
    val id: String,
    val recipe: String,
    @ColumnInfo("roast_type")
    val roastType: String
)
