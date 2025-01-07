package com.coffeevoyager.core.ui.extension

import androidx.annotation.IntRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.layout.LazyLayoutScrollScope

suspend fun LazyGridState.customScroll(block: suspend LazyLayoutScrollScope.() -> Unit) =
    scroll {
        block.invoke(LazyLayoutScrollScope(this@customScroll, this))
    }

suspend fun LazyGridState.customAnimateScrollToItem(
    @IntRange(from = 0) index: Int,
    animationSpec: AnimationSpec<Float> = spring()
) = customScroll {
    val distance = calculateDistanceTo(index).toFloat()
    var previousValue = 0f
    animate(0f, targetValue = distance, animationSpec = animationSpec) { currentValue, _ ->
        previousValue += scrollBy(currentValue - previousValue)
    }
}

suspend fun LazyGridState.animateScrollToTop(animationSpec: AnimationSpec<Float> = spring()) =
    customAnimateScrollToItem(index = 0, animationSpec = animationSpec)
