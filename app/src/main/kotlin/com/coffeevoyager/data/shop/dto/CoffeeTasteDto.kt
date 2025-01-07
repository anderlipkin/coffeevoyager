package com.coffeevoyager.data.shop.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoffeeTasteDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String
)
