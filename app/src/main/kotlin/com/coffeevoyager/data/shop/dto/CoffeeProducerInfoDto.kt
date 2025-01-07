package com.coffeevoyager.data.shop.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoffeeProducerInfoDto(
    @SerialName("id")
    val id: String,
    @SerialName("country")
    val country: String,
    @SerialName("region")
    val region: String?,
    @SerialName("farm")
    val farm: String?
)
