package com.coffeevoyager.domain.shop

import kotlinx.datetime.Instant

data class CoffeeProductShort(
    val id: String,
    val name: String,
    val companyId: String,
    val producerInfoId: String,
    val imageUrl: String,
    val price: String,
    val processingOfCoffee: String,
    val variety: String?,
    val views: Int,
    val createdAt: Instant
)

data class CoffeeProductShortWithCompany(
    val id: String,
    val name: String,
    val company: CoffeeCompany,
    val producerInfoId: String,
    val imageUrl: String,
    val price: String,
    val processingOfCoffee: String,
    val variety: String?,
    val views: Int,
    val createdAt: Instant
)
