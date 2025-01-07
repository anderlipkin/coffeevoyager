package com.coffeevoyager.core.common.extension

inline fun <T> List<T>.filterIf(
    condition: (List<T>) -> Boolean,
    predicate: (T) -> Boolean
): List<T> = if (condition(this)) filter(predicate) else this

fun <T> Sequence<T>.filterIf(
    condition: (Sequence<T>) -> Boolean,
    predicate: (T) -> Boolean
): Sequence<T> = if (condition(this)) filter(predicate) else this
