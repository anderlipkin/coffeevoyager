package com.coffeevoyager.data.user.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.coffeevoyager.UserPreferences
import com.coffeevoyager.copy
import com.coffeevoyager.domain.user.UserData
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.coroutines.coroutineContext

private const val LOG_TAG = "UserPreferencesDataSource"

class UserPreferencesDataSource(private val userPreferences: DataStore<UserPreferences>) {

    val userData = userPreferences.data
        .map {
            UserData(
                favoriteCoffeeBeanProductIds = it.favoriteCoffeeBeanProductIdsMap.keys
            )
        }.distinctUntilChanged()

    val favoriteCoffeeBeanProductIdsFlow =
        userData.map { it.favoriteCoffeeBeanProductIds }.distinctUntilChanged()

    suspend fun setFavoriteCoffeeProductId(id: String, favorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (favorite) {
                        favoriteCoffeeBeanProductIds.put(id, true)
                    } else {
                        favoriteCoffeeBeanProductIds.remove(id)
                    }
                }
            }
        } catch (exception: Exception) {
            coroutineContext.ensureActive()
            Log.e(LOG_TAG, "Failed to update user preferences", exception)
        }
    }

}
