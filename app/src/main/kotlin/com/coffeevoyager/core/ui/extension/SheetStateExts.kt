package com.coffeevoyager.core.ui.extension

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.animateToDismiss(scope: CoroutineScope, onCompleted: () -> Unit) {
    scope.launch { hide() }
        .invokeOnCompletion {
            if (!isVisible) {
                onCompleted()
            }
        }
}
