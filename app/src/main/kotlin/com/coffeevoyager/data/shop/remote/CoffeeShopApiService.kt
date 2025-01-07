package com.coffeevoyager.data.shop.remote

import com.coffeevoyager.data.shop.dto.CoffeeBeanOptionDto
import com.coffeevoyager.data.shop.dto.CoffeeBeanProductDto
import com.coffeevoyager.data.shop.dto.CoffeeCompanyDto
import com.coffeevoyager.data.shop.dto.CoffeeProducerInfoDto
import com.coffeevoyager.data.shop.dto.CoffeeRecipeDto
import com.coffeevoyager.data.shop.dto.CoffeeTasteDto
import com.coffeevoyager.core.common.network.getResult
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

private const val ID_PARAMETER_NAME = "id"

class CoffeeShopApiService(private val client: HttpClient) {

    suspend fun getAllCoffeeBeans(): Result<List<CoffeeBeanProductDto>> =
        client.getResult(urlString = "shop-coffee-product")

    suspend fun getCoffeeCompanyByIds(ids: List<String>): Result<List<CoffeeCompanyDto>> =
        client.getResult(urlString = "shop-company") {
            ids.forEach { id ->
                parameter(ID_PARAMETER_NAME, id)
            }
        }

    suspend fun getCoffeeRecipeByIds(ids: List<String>): Result<List<CoffeeRecipeDto>> =
        client.getResult(urlString = "shop-coffee-recipe") {
            url {
                parameters.appendAll(ID_PARAMETER_NAME, ids)
            }
        }

    suspend fun getCoffeeOptionByIds(ids: List<String>): Result<List<CoffeeBeanOptionDto>> =
        client.getResult(urlString = "shop-coffee-options") {
            url {
                parameters.appendAll(ID_PARAMETER_NAME, ids)
            }
        }

    suspend fun getCoffeeProducerInfoByIds(ids: List<String>): Result<List<CoffeeProducerInfoDto>> =
        client.getResult(urlString = "shop-coffee-producer") {
            url {
                parameters.appendAll(ID_PARAMETER_NAME, ids)
            }
        }

    suspend fun getCoffeeTasteByIds(ids: List<String>): Result<List<CoffeeTasteDto>> =
        client.getResult(urlString = "shop-coffee-taste") {
            url {
                parameters.appendAll(ID_PARAMETER_NAME, ids)
            }
        }
}
