package com.coffeevoyager.data.shop.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoffeeBeanProductDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("coffeeRecipeIds")
    val coffeeRecipeIds: List<String>,
    @SerialName("companyId")
    val companyId: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("optionIds")
    val optionIds: List<String>,
    @SerialName("price")
    val price: String,
    @SerialName("processingOfCoffee")
    val processingOfCoffee: String,
    @SerialName("producerInfoId")
    val producerInfoId: String,
    @SerialName("tasteIds")
    val tasteIds: List<String>,
    @SerialName("variety")
    val variety: String?,
    @SerialName("views")
    val views: Int,
    @SerialName("createdAt")
    val createdAt: Instant
)

