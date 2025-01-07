package com.coffeevoyager.features.shop.products.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.coffeevoyager.R
import com.coffeevoyager.core.designsystem.component.FlexibleTopBar
import com.coffeevoyager.core.designsystem.component.FlexibleTopBarDefaults
import com.coffeevoyager.core.ui.UiStringValue
import com.coffeevoyager.core.ui.extension.clearFocusOnFirstDown
import com.coffeevoyager.core.ui.extension.clearFocusOnHiddenIme
import com.coffeevoyager.features.shop.products.CoffeeShopUiState
import com.coffeevoyager.features.shop.products.TopAppBarAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeShopTopAppBar(
    topBarData: CoffeeShopUiState.TopBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    onAction: (TopAppBarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    FlexibleTopBar(
        colors = FlexibleTopBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    ) {
        Column(modifier = Modifier) {
            SearchTopAppBar(
                countInCart = topBarData.countInCart,
                textFieldState = topBarData.searchFieldState,
                hasAppliedFilters = topBarData.hasAppliedFilters,
                onCartClick = { onAction(TopAppBarAction.CartClick) },
                onFiltersClick = { onAction(TopAppBarAction.FiltersClick) },
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 8.dp)
                    .clearFocusOnFirstDown()
            ) {
                SortingButton(topBarData.selectedSorting, onAction)
                val favoriteFilterState = topBarData.favoriteFilterState
                if (favoriteFilterState.visible) {
                    FavoriteFilterChip(
                        selected = favoriteFilterState.selected,
                        onCheckedChange = { onAction(TopAppBarAction.FavoriteFilterClick(it)) },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    countInCart: Int,
    textFieldState: TextFieldState,
    hasAppliedFilters: Boolean,
    onCartClick: () -> Unit,
    onFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            SearchTextField(textFieldState)
        },
        navigationIcon = {
            ShoppingCartButton(countInCart, onCartClick)
        },
        actions = {
            FiltersButton(hasAppliedFilters, onFiltersClick)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun FiltersButton(
    hasAppliedFilters: Boolean,
    onFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BadgedBox(
        badge = {
            if (hasAppliedFilters) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .size(8.dp)
                        .offset((-2).dp, (1).dp)
                )
            }
        },
        modifier = modifier.size(IconButtonDefaults.smallContainerSize())
    ) {
        IconButton(onClick = onFiltersClick) {
            Icon(
                imageVector = Icons.Filled.FilterAlt,
                contentDescription = null
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun ShoppingCartButton(
    countInCart: Int,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BadgedBox(
        badge = {
            if (countInCart > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.offset((-6).dp, 2.dp)
                ) {
                    Text(text = "$countInCart", modifier = Modifier.offset((-1).dp))
                }
            }
        },
        modifier = modifier.size(IconButtonDefaults.smallContainerSize())
    ) {
        IconButton(onClick = onCartClick) {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = null
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun SearchTextField(
    fieldState: TextFieldState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        state = fieldState,
        modifier = modifier
//            .padding(start = 8.dp)
            .fillMaxWidth()
            .height(36.dp)
            .clearFocusOnHiddenIme(),
        placeholder = { Text(text = stringResource(R.string.search)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (fieldState.text.isNotEmpty()) {
                IconButton(
                    onClick = { fieldState.clearText() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
//                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        onKeyboardAction = { focusManager.clearFocus() },
        lineLimits = TextFieldLineLimits.SingleLine,
        shape = SearchBarDefaults.inputFieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground
        ),
        contentPadding = OutlinedTextFieldDefaults.contentPadding(
            start = 0.dp,
            top = 0.dp,
            bottom = 0.dp
        )
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun SortingButton(
    selectedSorting: UiStringValue,
    onAction: (TopAppBarAction) -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = { onAction(TopAppBarAction.SortingClick) },
        modifier = modifier.heightIn(max = ButtonDefaults.XSmallContainerHeight),
        contentPadding = ButtonDefaults.XSmallContentPadding
    ) {
        Text(selectedSorting.asString())
        Spacer(modifier = Modifier.size(ButtonDefaults.XSmallIconSpacing))
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
    }
}

@Composable
fun FavoriteFilterChip(
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = { onCheckedChange(!selected) },
        label = { Text(stringResource(R.string.favorite)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
            iconColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        shape = CircleShape,
        border = null,
        modifier = modifier.heightIn(max = FilterChipDefaults.Height)
    )
}
