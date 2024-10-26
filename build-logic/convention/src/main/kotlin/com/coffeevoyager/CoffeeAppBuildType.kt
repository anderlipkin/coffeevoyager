package com.coffeevoyager

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class CoffeeAppBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}
