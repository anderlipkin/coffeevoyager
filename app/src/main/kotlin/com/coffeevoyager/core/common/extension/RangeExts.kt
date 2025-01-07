package com.coffeevoyager.core.common.extension


fun IntRange.toFloatRange() = first.toFloat()..last.toFloat()

fun ClosedFloatingPointRange<Float>.toIntRange() =
    start.toInt()..endInclusive.toInt()
