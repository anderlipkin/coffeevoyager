package com.coffeevoyager.data.shop.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.coffeevoyager.data.shop.dto.CoffeeBeanProductDto
import com.coffeevoyager.domain.shop.CoffeeProductShort
import com.coffeevoyager.domain.shop.CoffeeProductShortWithCompany
import kotlinx.datetime.Instant

@Entity(
    tableName = "coffee_bean_products",
    foreignKeys = [
        ForeignKey(
            entity = CoffeeCompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["company_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CoffeeProducerInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["producer_info_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index("company_id"), Index("producer_info_id")]
)
data class CoffeeBeanProductEntity(
    @PrimaryKey
    @ColumnInfo("product_id")
    val id: String,
    val name: String,
    @ColumnInfo("company_id")
    val companyId: String,
    @ColumnInfo("producer_info_id")
    val producerInfoId: String,
    @ColumnInfo("image_url")
    val imageUrl: String,
    val price: String,
    @ColumnInfo("processing_of_coffee")
    val processingOfCoffee: String,
    val variety: String?,
    val views: Int,
    @ColumnInfo("created_at")
    val createdAt: Instant
)

data class CoffeeBeanProductFullEntity(
    @Embedded
    val product: CoffeeBeanProductEntity,
    @Relation(
        parentColumn = "company_id",
        entityColumn = "id"
    )
    val company: CoffeeCompanyEntity,
    @Relation(
        parentColumn = "producer_info_id",
        entityColumn = "id"
    )
    val producerInfo: CoffeeProducerInfoEntity,
    @Relation(
        parentColumn = "product_id",
        entityColumn = "recipe_id",
        associateBy = Junction(CoffeeBeanProductToRecipe::class)
    )
    val recipes: List<CoffeeRecipeEntity>,
    @Relation(
        parentColumn = "product_id",
        entityColumn = "option_id",
        associateBy = Junction(CoffeeBeanProductToOption::class)
    )
    val options: List<CoffeeBeanOptionEntity>,
    @Relation(
        parentColumn = "product_id",
        entityColumn = "taste_id",
        associateBy = Junction(CoffeeBeanProductToTaste::class)
    )
    val tastes: List<CoffeeTasteEntity>
)

data class CoffeeBeanProductWithCompany(
    @Embedded
    val product: CoffeeBeanProductEntity,
    @Relation(
        parentColumn = "company_id",
        entityColumn = "id"
    )
    val company: CoffeeCompanyEntity
)

fun CoffeeBeanProductDto.asEntity() =
    CoffeeBeanProductEntity(
        id = id,
        name = name,
        companyId = companyId,
        producerInfoId = producerInfoId,
        imageUrl = imageUrl,
        price = price,
        processingOfCoffee = processingOfCoffee,
        variety = variety,
        views = views,
        createdAt = createdAt
    )

fun CoffeeBeanProductEntity.asDomain() =
    CoffeeProductShort(
        id = id,
        name = name,
        companyId = companyId,
        producerInfoId = producerInfoId,
        imageUrl = imageUrl,
        price = price,
        processingOfCoffee = processingOfCoffee,
        variety = variety,
        views = views,
        createdAt = createdAt
    )

fun CoffeeBeanProductWithCompany.asDomain() =
    CoffeeProductShortWithCompany(
        id = product.id,
        name = product.name,
        company = company.asDomain(),
        producerInfoId = product.producerInfoId,
        imageUrl = product.imageUrl,
        price = product.price,
        processingOfCoffee = product.processingOfCoffee,
        variety = product.variety,
        views = product.views,
        createdAt = product.createdAt
    )
