package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coffeevoyager.domain.shop.CoffeeCompany

@Entity(tableName = "companies")
data class CoffeeCompanyEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("image_url")
    val imageUrl: String,
    val name: String,
    val description: String
)

fun CoffeeCompanyEntity.asDomain() =
    CoffeeCompany(
        id = id,
        imageUrl = imageUrl,
        name = name,
        description = description
    )
