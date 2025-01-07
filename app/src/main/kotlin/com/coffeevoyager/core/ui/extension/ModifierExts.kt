package com.coffeevoyager.core.ui.extension

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

@Composable
fun Modifier.clearFocusOnFirstDown(): Modifier {
    val focusManager = LocalFocusManager.current
    return pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            focusManager.clearFocus()
        }
    }
}

/**
 * A [Modifier] that tracks all input, and calls [block] every time input is received.
 */
fun Modifier.notifyInput(block: () -> Unit): Modifier =
    composed {
        val blockState = rememberUpdatedState(block)
        pointerInput(Unit) {
            while (currentCoroutineContext().isActive) {
                awaitPointerEventScope {
                    awaitPointerEvent(PointerEventPass.Initial)
                    blockState.value()
                }
            }
        }
    }

@OptIn(ExperimentalLayoutApi::class)
fun Modifier.scrollOnFocus(bringIntoViewRequester: BringIntoViewRequester): Modifier =
    composed(
        inspectorInfo = {
            name = "scrollOnFocus"
            properties["bringIntoViewRequester"] = bringIntoViewRequester
        }
    ) {
        var hasFocus by remember { mutableStateOf(false) }
        val isKeyboardVisible = WindowInsets.isImeVisible
        val imeAnimationSource = WindowInsets.imeAnimationSource
        val imeAnimationTarget = WindowInsets.imeAnimationTarget
        val isKeyboardAnimationInProgress by remember {
            derivedStateOf {
                imeAnimationTarget != imeAnimationSource
            }
        }
        val isIdleKeyboardVisible = !isKeyboardAnimationInProgress && isKeyboardVisible
        val shouldBringIntoView = hasFocus && isIdleKeyboardVisible
        LaunchedEffect(isIdleKeyboardVisible) {
            if (shouldBringIntoView) {
                bringIntoViewRequester.bringIntoView()
            }
        }
        onFocusChanged { focusState ->
            hasFocus = focusState.hasFocus
        }
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Modifier.clearFocusOnHiddenIme(): Modifier =
    composed(inspectorInfo = { name = "clearFocusOnHiddenIme" }) {
        val focusManager = LocalFocusManager.current
        var isFocused by remember { mutableStateOf(false) }
        val isKeyboardVisible = WindowInsets.isImeVisible
        val imeAnimationSource = WindowInsets.imeAnimationSource
        val imeAnimationTarget = WindowInsets.imeAnimationTarget
        val isKeyboardAnimationInProgress by remember {
            derivedStateOf {
                imeAnimationTarget != imeAnimationSource
            }
        }
        val isIdleKeyboardVisible = !isKeyboardAnimationInProgress && isKeyboardVisible
        val shouldClearFocus = isFocused && !isIdleKeyboardVisible
        LaunchedEffect(isIdleKeyboardVisible) {
            if (shouldClearFocus) {
                focusManager.clearFocus()
            }
        }
        onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        }
    }
