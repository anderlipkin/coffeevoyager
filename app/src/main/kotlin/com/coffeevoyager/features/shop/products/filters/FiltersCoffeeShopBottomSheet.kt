package com.coffeevoyager.features.shop.products.filters

import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.TextButtonWithIconContentPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coffeevoyager.R
import com.coffeevoyager.core.common.extension.toFloatRange
import com.coffeevoyager.core.ui.ImmutableList
import com.coffeevoyager.core.ui.ScreenPreview
import com.coffeevoyager.core.ui.UiStringValue
import com.coffeevoyager.core.ui.extension.animateToDismiss
import com.coffeevoyager.core.ui.extension.clearFocusOnFirstDown
import com.coffeevoyager.core.ui.extension.notifyInput
import com.coffeevoyager.core.ui.extension.scrollOnFocus
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod.RangeWithTextFields.SliderState
import com.coffeevoyager.domain.shop.CoffeeProductFilter.SelectionMethod.RangeWithTextFields.TextState
import com.coffeevoyager.domain.shop.CoffeeProductFilterGroups
import com.coffeevoyager.domain.shop.Currency
import com.coffeevoyager.features.shop.products.filters.FilterUiData.Attribute
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersCoffeeShopBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FiltersCoffeeShopViewModel = koinViewModel()
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val animateToDismiss = {
        bottomSheetState.animateToDismiss(scope, onDismissRequest)
    }
    val filterGroups by viewModel.filterGroupsUiData.collectAsStateWithLifecycle()
    val resetButtonVisible by viewModel.resetButtonVisible.collectAsStateWithLifecycle()
    val userScrollEnabled by viewModel.userScrollEnabled.collectAsStateWithLifecycle()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        sheetGesturesEnabled = userScrollEnabled,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier.fillMaxSize()
    ) {
        FiltersCoffeeShopContent(
            filterGroups = filterGroups,
            resetButtonVisible = resetButtonVisible,
            userScrollEnabled = userScrollEnabled,
            onResetClick = viewModel::onResetClick,
            onDoneClick = animateToDismiss,
            onFilterGroupExpandedChange = viewModel::onFilterGroupExpandedChange,
            onFilterAction = viewModel::onFilterItemAction
        )
    }
}

@Composable
fun ColumnScope.FiltersCoffeeShopContent(
    filterGroups: ImmutableList<FilterGroupsUiData>,
    resetButtonVisible: Boolean,
    userScrollEnabled: Boolean,
    onResetClick: () -> Unit,
    onDoneClick: () -> Unit,
    onFilterGroupExpandedChange: (id: String, expanded: Boolean) -> Unit,
    onFilterAction: (FilterItemAction) -> Unit,
    modifier: Modifier = Modifier
) {
    FiltersTopAppBar(
        resetButtonVisible = resetButtonVisible,
        onResetClick = onResetClick,
        onDoneClick = onDoneClick,
        modifier = Modifier.clearFocusOnFirstDown()
    )
    LazyColumn(
        userScrollEnabled = userScrollEnabled,
        modifier = Modifier.fillMaxSize()
    ) {
        filterGroups.forEachIndexed { index, filterGroup ->
            val filterGroupId = filterGroup.id
            filterGroupHeaderItem(
                filterGroupId = filterGroupId,
                name = filterGroup.name,
                expanded = filterGroup.expanded,
                onExpandedChange = { expanded ->
                    onFilterGroupExpandedChange(filterGroupId, expanded)
                }
            )
            filterItems(
                filterGroupId = filterGroupId,
                filterGroupType = filterGroup.type,
                filters = filterGroup.filters,
                expanded = filterGroup.expanded,
                onFilterAction = onFilterAction
            )
            if (index != filterGroups.lastIndex) {
                filterGroupDividerItem()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersTopAppBar(
    resetButtonVisible: Boolean,
    onResetClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (resetButtonVisible) {
                TextButton(onClick = onResetClick) {
                    Text(stringResource(R.string.reset))
                }
            }
        },
        title = { Text(stringResource(R.string.filters)) },
        actions = {
            TextButton(onClick = onDoneClick) {
                Text(stringResource(R.string.done))
            }
        },
        modifier = modifier
    )
}

// TODO add FlowRow of selected filters
//@OptIn(ExperimentalLayoutApi::class)
//@Composable
//fun SelectedFiltersSection(modifier: Modifier = Modifier) {
//    FlowRow {
//        FilterChip(
//            selected = true,
//            onClick = { },
//            label = { Text(stringResource(R.string.favorite)) },
//            trailingIcon = if (true) {
//                {
//                    Icon(
//                        imageVector = Icons.Filled.Done,
//                        contentDescription = null,
//                        modifier = Modifier.size(FilterChipDefaults.IconSize)
//                    )
//                }
//            } else {
//                null
//            },
//            modifier = Modifier.padding(start = 16.dp)
//        )
//    }
//}

fun LazyListScope.filterGroupHeaderItem(
    filterGroupId: String,
    name: UiStringValue,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val contentType = "filterGroupHeaderItem"
    item(key = "$contentType-$filterGroupId", contentType = contentType) {
        CompositionLocalProvider(
            LocalRippleConfiguration provides null
        ) {
            TextButton(
                onClick = { onExpandedChange(!expanded) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = TextButtonWithIconContentPadding.calculateTopPadding(),
                    end = 16.dp,
                    bottom = TextButtonWithIconContentPadding.calculateBottomPadding()
                )
            ) {
                Text(
                    text = name.asString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = ButtonDefaults.IconSpacing)
                )
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 0f else -180f,
                    animationSpec = tween(500)
                )
                Icon(
                    imageVector = Icons.Default.ExpandLess,
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(rotation)
                        .sizeIn(maxHeight = ButtonDefaults.IconSize)
                )
            }
        }
    }
}

fun LazyListScope.filterGroupDividerItem() {
    item(contentType = "filterGroupDividerItem") {
        HorizontalDivider()
    }
}

fun LazyListScope.filterItems(
    filterGroupId: String,
    filterGroupType: CoffeeProductFilterGroups,
    filters: ImmutableList<FilterUiData>,
    expanded: Boolean,
    onFilterAction: (FilterItemAction) -> Unit
) = items(
    items = filters,
    key = { filterGroupId + it.id },
    contentType = { it.selectionMethod }
) { filterData ->
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        when (val selectionMethod = filterData.selectionMethod) {
            is SelectionMethod.Selectable ->
                CheckboxFilterItem(
                    filterName = filterData.name,
                    availableCount = filterData.availableProductCount,
                    enabled = filterData.enabled,
                    selected = selectionMethod.selected,
                    onSelectedChange = { selected ->
                        onFilterAction(
                            FilterItemAction.SelectedChange(
                                filterId = filterData.id,
                                selected = selected
                            )
                        )
                    }
                )

            is SelectionMethod.RangeWithTextFields -> {
                when (filterGroupType) {
                    CoffeeProductFilterGroups.PriceRange -> {
                        PriceRangeFilterItem(
                            rangeData = selectionMethod,
                            currency = (filterData.attribute as Attribute.Currency).currency,
                            onSliderRangeChange = { rangeValue, state ->
                                onFilterAction(
                                    FilterItemAction.Slider(
                                        filterId = filterData.id,
                                        value = rangeValue,
                                        state = state,
                                        currentSelectionMethod = selectionMethod
                                    )
                                )
                            },
                            onFromTextRangeChange = { text, state ->
                                onFilterAction(
                                    FilterItemAction.TextFieldRangeChange(
                                        filterId = filterData.id,
                                        minValue = text,
                                        maxValue = selectionMethod.maxValueText,
                                        state = state,
                                        currentSelectionMethod = selectionMethod
                                    )
                                )
                            },
                            onToTextRangeChange = { text, state ->
                                onFilterAction(
                                    FilterItemAction.TextFieldRangeChange(
                                        filterId = filterData.id,
                                        minValue = selectionMethod.minValueText,
                                        maxValue = text,
                                        state = state,
                                        currentSelectionMethod = selectionMethod
                                    )
                                )
                            }
                        )
                    }

                    else -> error("Not supported")
                }
            }
        }
    }
}

@Composable
fun PriceRangeFilterItem(
    rangeData: SelectionMethod.RangeWithTextFields,
    currency: String,
    onSliderRangeChange: (ClosedFloatingPointRange<Float>, SliderState) -> Unit,
    onFromTextRangeChange: (String, TextState) -> Unit,
    onToTextRangeChange: (String, TextState) -> Unit,
    modifier: Modifier = Modifier
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var isSliderInteractingRaw by remember(rangeData.sliderState) {
        mutableStateOf(rangeData.sliderState == SliderState.Interacting)
    }
    val shouldSyncWithText by remember(rangeData.minValueText, rangeData.maxValueText) {
        mutableIntStateOf(if (!isSliderInteractingRaw) rangeData.hashCode() else -1)
    }
    var sliderValueRaw by remember(shouldSyncWithText) {
        mutableStateOf(rangeData.value.toFloatRange())
    }
    PriceRangeFilter(
        sliderValue = if (isSliderInteractingRaw) sliderValueRaw else rangeData.value.toFloatRange(),
        sliderValueRange = rangeData.valueRange.toFloatRange(),
        sliderSteps = rangeData.steps,
        onValueChange = { value ->
            sliderValueRaw = value
            isSliderInteractingRaw = true
            onSliderRangeChange(value, SliderState.Interacting)
        },
        onValueChangeFinished = {
            isSliderInteractingRaw = false
            onSliderRangeChange(sliderValueRaw, SliderState.Idle)
        },
        onInteractWithSlider = {
            isSliderInteractingRaw = true
            onSliderRangeChange(sliderValueRaw, SliderState.Interacting)
        },
        fromPriceTextField = {
            PriceOutlinedTextFieldWithState(
                labelRes = R.string.from,
                text = rangeData.minValueText,
                currency = currency,
                onChange = onFromTextRangeChange
            )
        },
        toPriceTextField = {
            PriceOutlinedTextFieldWithState(
                labelRes = R.string.to,
                text = rangeData.maxValueText,
                currency = currency,
                onChange = onToTextRangeChange
            )
        },
        modifier = modifier
            .padding(horizontal = 16.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
            .scrollOnFocus(bringIntoViewRequester)
            .focusGroup()
    )
}

@Composable
fun PriceOutlinedTextFieldWithState(
    @StringRes labelRes: Int,
    text: String,
    currency: String,
    onChange: (String, TextState) -> Unit,
    modifier: Modifier = Modifier
) {
    var textRaw by remember(text) { mutableStateOf(text) }
    val interaction = remember { MutableInteractionSource() }
    val isFocused by interaction.collectIsFocusedAsState()
    PriceOutlinedTextField(
        text = textRaw,
        labelRes = labelRes,
        onValueChange = {
            textRaw = it
            val textState = if (isFocused) TextState.Focused else TextState.Idle
            onChange(it, textState)
        },
        onFocusChange = { focused ->
            val textState = if (focused) TextState.Focused else TextState.Idle
            onChange(textRaw, textState)
        },
        trailingIcon = { Text(text = currency) },
        interactionSource = interaction,
        modifier = modifier.widthIn(min = 100.dp, max = 200.dp)
    )
}

@Composable
fun CheckboxFilterItem(
    filterName: String,
    availableCount: Int,
    enabled: Boolean,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(LocalMinimumInteractiveComponentSize.current)
            .toggleable(
                value = selected,
                enabled = enabled,
                onValueChange = onSelectedChange,
                role = Role.Checkbox
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val text = buildAnnotatedString {
            append(filterName)
            if (availableCount > 0 && !selected) {
                append(" ")
                withStyle(SpanStyle(color = LocalContentColor.current.copy(alpha = 0.38f))) {
                    append("($availableCount)")
                }
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Checkbox(checked = selected, onCheckedChange = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeFilter(
    sliderValue: ClosedFloatingPointRange<Float>,
    sliderValueRange: ClosedFloatingPointRange<Float>,
    @IntRange(from = 0) sliderSteps: Int,
    onValueChange: (value: ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: () -> Unit,
    onInteractWithSlider: () -> Unit,
    fromPriceTextField: @Composable RowScope.() -> Unit,
    toPriceTextField: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnInteractWithSlider by rememberUpdatedState(onInteractWithSlider)
    val startInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource = remember { MutableInteractionSource() }
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            fromPriceTextField()
            toPriceTextField()
        }

        val colors = colors(
            activeTrackColor = MaterialTheme.colorScheme.primary,
            activeTickColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            inactiveTickColor = MaterialTheme.colorScheme.secondaryContainer,
        )
        RangeSlider(
            value = sliderValue,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = sliderValueRange,
            steps = sliderSteps,
            startInteractionSource = startInteractionSource,
            endInteractionSource = endInteractionSource,
            startThumb = {
                CircleThumb(
                    interactionSource = startInteractionSource,
                    size = 24.dp,
                    colors = colors
                )
            },
            endThumb = {
                CircleThumb(
                    interactionSource = startInteractionSource,
                    size = 24.dp,
                    colors = colors
                )
            },
            colors = colors,
            track = { rangeSliderState ->
                SliderDefaults.Track(
                    rangeSliderState = rangeSliderState,
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    modifier = Modifier.height(4.dp),
                    colors = colors
                )
            },
            modifier = Modifier
                .notifyInput(currentOnInteractWithSlider)
                .systemGestureExclusion()
                .clearFocusOnFirstDown(),
        )
    }
}

@Composable
fun CircleThumb(
    size: Dp,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    colors: SliderColors = colors(),
    enabled: Boolean = true,
) {
    Spacer(
        modifier
            .size(size)
            .hoverable(interactionSource = interactionSource)
            .background(
                color = if (enabled) colors.thumbColor else colors.disabledThumbColor,
                shape = CircleShape
            )
    )
}

@Composable
fun PriceOutlinedTextField(
    @StringRes labelRes: Int,
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChange: (Boolean) -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
        },
        singleLine = true,
        interactionSource = interactionSource,
        modifier = modifier.onFocusChanged { focusState ->
            onFocusChange(focusState.isFocused)
        }
    )
}

@Preview
@Composable
private fun FiltersCoffeeShopContentPreview() {
    val currency = Currency.UAH.symbol
    val filterGroups = CoffeeProductFilterGroups.entries
        .filter { it != CoffeeProductFilterGroups.Favorite }
        .map { filterGroup ->
            val filters = when (filterGroup) {
                CoffeeProductFilterGroups.PriceRange -> {
                    listOf(
                        FilterUiData(
                            id = "${filterGroup.id}-0",
                            name = LoremIpsum(3).values.first(),
                            selectionMethod = SelectionMethod.RangeWithTextFields(
                                value = 1..2000,
                                valueRange = 1..2000,
                                textState = TextState.Idle,
                                sliderState = SliderState.Idle
                            ),
                            attribute = Attribute.Currency(currency),
                            enabled = true,
                            availableProductCount = 0
                        )
                    )
                }

                else -> {
                    List(2) { index: Int ->
                        FilterUiData(
                            id = "${filterGroup.id}-$index",
                            name = LoremIpsum(3).values.first(),
                            selectionMethod = SelectionMethod.Selectable(false),
                            attribute = null,
                            enabled = true,
                            availableProductCount = 2
                        )
                    }
                }
            }
            FilterGroupsUiData(
                id = filterGroup.id,
                type = filterGroup,
                name = filterGroup.getUiTitle(currency),
                expanded = true,
                filters = ImmutableList(filters)
            )
        }
    ScreenPreview {
        Column {
            FiltersCoffeeShopContent(
                filterGroups = ImmutableList(filterGroups),
                userScrollEnabled = true,
                onDoneClick = {},
                onFilterGroupExpandedChange = { _, _ -> },
                onFilterAction = {},
                resetButtonVisible = false,
                onResetClick = {}
            )
        }
    }
}
