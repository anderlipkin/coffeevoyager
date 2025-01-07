package com.coffeevoyager.core.ui

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val exception: Throwable) : UiState<Nothing>
    data object Loading : UiState<Nothing>
}
