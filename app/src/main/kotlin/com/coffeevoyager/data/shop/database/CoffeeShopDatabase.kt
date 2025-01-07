package com.coffeevoyager.data.shop.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanOptionDao
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductDao
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductFtsDao
import com.coffeevoyager.data.shop.database.dao.CoffeeCompanyDao
import com.coffeevoyager.data.shop.database.dao.CoffeeProducerInfoDao
import com.coffeevoyager.data.shop.database.dao.FilterCoffeeProductDao
import com.coffeevoyager.data.shop.database.dao.CoffeeRecipeDao
import com.coffeevoyager.data.shop.database.dao.CoffeeTasteDao
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanOptionEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductFtsEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToOption
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToRecipe
import com.coffeevoyager.data.shop.database.entities.CoffeeBeanProductToTaste
import com.coffeevoyager.data.shop.database.entities.CoffeeCompanyEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeProducerInfoEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeProductFilterEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeProductFilterGroupEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeRecipeEntity
import com.coffeevoyager.data.shop.database.entities.CoffeeTasteEntity

@Database(
    entities = [
        CoffeeBeanProductEntity::class,
        CoffeeCompanyEntity::class,
        CoffeeRecipeEntity::class,
        CoffeeBeanOptionEntity::class,
        CoffeeProducerInfoEntity::class,
        CoffeeTasteEntity::class,
        CoffeeBeanProductToRecipe::class,
        CoffeeBeanProductToOption::class,
        CoffeeBeanProductToTaste::class,
        CoffeeProductFilterGroupEntity::class,
        CoffeeProductFilterEntity::class,
        CoffeeBeanProductFtsEntity::class
    ],
    version = 1
)
@TypeConverters(CoffeeShopTypeConverters::class)
internal abstract class CoffeeShopDatabase : RoomDatabase() {
    abstract fun coffeeBeanProductDao(): CoffeeBeanProductDao
    abstract fun coffeeRecipeDao(): CoffeeRecipeDao
    abstract fun coffeeCompanyDao(): CoffeeCompanyDao
    abstract fun coffeeBeanOptionDao(): CoffeeBeanOptionDao
    abstract fun coffeeProducerInfoDao(): CoffeeProducerInfoDao
    abstract fun coffeeTasteDao(): CoffeeTasteDao
    abstract fun coffeeProductFilterDao(): FilterCoffeeProductDao
    abstract fun coffeeBeanProductFtsDao(): CoffeeBeanProductFtsDao
}
