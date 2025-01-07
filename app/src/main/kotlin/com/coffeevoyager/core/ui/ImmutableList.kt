package com.coffeevoyager.core.ui

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<T>(
    val wrapped: List<T> = listOf()
) : List<T> by wrapped

