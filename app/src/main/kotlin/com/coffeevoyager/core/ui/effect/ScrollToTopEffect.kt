package com.coffeevoyager.core.ui.effect

import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.coffeevoyager.core.ui.extension.animateScrollToTop

@Composable
fun ScrollToTopEffect(
    verticalGridState: LazyGridState,
    scrollToTop: Boolean,
    onScrollToTopComplete: () -> Unit
) {
    val currentOnScrollToTopComplete by rememberUpdatedState(onScrollToTopComplete)
    LaunchedEffect(scrollToTop) {
        if (scrollToTop) {
            verticalGridState.animateScrollToTop(animationSpec = tween(500))
            currentOnScrollToTopComplete()
        }
    }
}
