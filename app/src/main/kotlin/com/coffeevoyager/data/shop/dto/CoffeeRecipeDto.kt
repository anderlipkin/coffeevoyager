package com.coffeevoyager.data.shop.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoffeeRecipeDto(
    @SerialName("id")
    val id: String,
    @SerialName("recipe")
    val recipe: String,
    @SerialName("roastType")
    val roastType: String
)
