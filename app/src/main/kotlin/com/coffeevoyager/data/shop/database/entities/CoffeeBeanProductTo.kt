package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index

@Entity(
    tableName = "coffee_bean_products_to_recipes",
    primaryKeys = ["product_id", "recipe_id"],
    foreignKeys = [
        ForeignKey(
            entity = CoffeeBeanProductEntity::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CoffeeRecipeEntity::class,
            parentColumns = ["recipe_id"],
            childColumns = ["recipe_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index("product_id"), Index("recipe_id")]
)
data class CoffeeBeanProductToRecipe(
    @ColumnInfo("product_id")
    val productId: String,
    @ColumnInfo("recipe_id")
    val recipeId: String
)

@Entity(
    tableName = "coffee_bean_products_to_options",
    primaryKeys = ["product_id", "option_id"],
    foreignKeys = [
        ForeignKey(
            entity = CoffeeBeanProductEntity::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CoffeeBeanOptionEntity::class,
            parentColumns = ["option_id"],
            childColumns = ["option_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index("product_id"), Index("option_id")]
)
data class CoffeeBeanProductToOption(
    @ColumnInfo("product_id")
    val productId: String,
    @ColumnInfo("option_id")
    val optionId: String
)

@Entity(
    tableName = "coffee_bean_products_to_tastes",
    primaryKeys = ["product_id", "taste_id"],
    foreignKeys = [
        ForeignKey(
            entity = CoffeeBeanProductEntity::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CoffeeTasteEntity::class,
            parentColumns = ["taste_id"],
            childColumns = ["taste_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index("product_id"), Index("taste_id")]
)
data class CoffeeBeanProductToTaste(
    @ColumnInfo("product_id")
    val productId: String,
    @ColumnInfo("taste_id")
    val tasteId: String
)
