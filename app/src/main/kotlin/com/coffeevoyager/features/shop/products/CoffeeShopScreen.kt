package com.coffeevoyager.features.shop.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coffeevoyager.R
import com.coffeevoyager.core.ui.ScreenPreview
import com.coffeevoyager.core.ui.effect.ScrollToTopEffect
import com.coffeevoyager.core.ui.effect.UiEventEffect
import com.coffeevoyager.core.ui.extension.clearFocusOnFirstDown
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.Company
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.ListState
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.Product
import com.coffeevoyager.features.shop.products.component.CoffeeProductCard
import com.coffeevoyager.features.shop.products.component.CoffeeShopTopAppBar
import com.coffeevoyager.features.shop.products.component.CompanyCard
import com.coffeevoyager.features.shop.products.filters.FiltersCoffeeShopBottomSheet
import com.coffeevoyager.features.shop.products.sorting.ChooseSortingCoffeeShopBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CoffeeShopScreen(
    onNavigationEvent: (CoffeeShopNavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoffeeShopViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CoffeeShopContent(
        uiState = uiState,
        onCompanyFilterSelectedChange = viewModel::updateFilterCompanySelected,
        onCoffeeProductItemAction = viewModel::onCoffeeProductItemAction,
        onTopBarAction = viewModel::onTopBarAction,
        onRefreshClick = viewModel::refresh,
        onScrollToTopComplete = viewModel::onScrollToTopComplete,
        modifier = modifier
    )

    uiState.showBottomSheet?.let {
        when (it) {
            CoffeeShopBottomSheets.Sorting ->
                ChooseSortingCoffeeShopBottomSheet(onDismissRequest = viewModel::dismissBottomSheet)

            CoffeeShopBottomSheets.Filters ->
                FiltersCoffeeShopBottomSheet(onDismissRequest = viewModel::dismissBottomSheet)
        }
    }

    UiEventEffect(viewModel.navigationEvents, onNavigationEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CoffeeShopContent(
    uiState: CoffeeShopUiState,
    onCompanyFilterSelectedChange: (id: String, selected: Boolean) -> Unit,
    onCoffeeProductItemAction: (CoffeeProductItemAction) -> Unit,
    onTopBarAction: (TopAppBarAction) -> Unit,
    onRefreshClick: () -> Unit,
    onScrollToTopComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = if (uiState.listState is ListState.Success) {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    } else {
        TopAppBarDefaults.pinnedScrollBehavior()
    }
    Scaffold(
        topBar = {
            CoffeeShopTopAppBar(
                topBarData = uiState.topBarState,
                scrollBehavior = scrollBehavior,
                onAction = onTopBarAction
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        when (uiState.listState) {
            is ListState.Success ->
                CompaniesAndProductsGrid(
                    state = uiState.listState,
                    onCompanyFilterSelectedChange = onCompanyFilterSelectedChange,
                    onCoffeeProductItemAction = onCoffeeProductItemAction,
                    onScrollToTopComplete = onScrollToTopComplete,
                    modifier = Modifier
                        .clearFocusOnFirstDown()
                        .consumeWindowInsets(innerPadding),
                    contentPadding = innerPadding
                )

            is ListState.Empty -> {
                EmptyProductListState(
                    state = uiState.listState,
                    onRefreshClick = onRefreshClick,
                    modifier = Modifier
                        .clearFocusOnFirstDown()
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                )
            }

            ListState.Loading ->
                LoadingProductListState(
                    modifier = Modifier
                        .clearFocusOnFirstDown()
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                )
        }
    }
}

@Composable
private fun CompaniesAndProductsGrid(
    state: ListState.Success,
    onCompanyFilterSelectedChange: (id: String, selected: Boolean) -> Unit,
    onCoffeeProductItemAction: (CoffeeProductItemAction) -> Unit,
    onScrollToTopComplete: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val verticalGridState = rememberLazyGridState()
    ScrollToTopEffect(
        verticalGridState = verticalGridState,
        scrollToTop = state.scrollToTop,
        onScrollToTopComplete = onScrollToTopComplete
    )
    LazyVerticalGrid(
        state = verticalGridState,
        columns = GridCells.Adaptive(180.dp),
        horizontalArrangement = Arrangement.spacedBy((-16).dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        companiesRow(
            companies = state.companies,
            onSelectedChange = onCompanyFilterSelectedChange
        )
        coffeeProductItems(
            coffeeProducts = state.coffeeBeanProducts,
            onItemAction = onCoffeeProductItemAction
        )
    }
}

private fun LazyGridScope.companiesRow(
    companies: List<Company>,
    onSelectedChange: (id: String, selected: Boolean) -> Unit
) {
    item(span = { GridItemSpan(maxLineSpan) }, contentType = "companiesRow") {
        LazyRow(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = companies,
                key = { it.id },
                contentType = { "companyItem" }
            ) { company ->
                CompanyCard(company, onSelectedChange)
            }
        }
    }
}

private fun LazyGridScope.coffeeProductItems(
    coffeeProducts: List<Product>,
    onItemAction: (CoffeeProductItemAction) -> Unit,
) {
    items(
        items = coffeeProducts,
        key = { it.id },
        contentType = { "coffeeProductItem" }
    ) { coffeeProduct ->
        CoffeeProductCard(
            coffeeProduct = coffeeProduct,
            onItemAction = onItemAction,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmptyProductListState(
    state: ListState.Empty,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasAppliedFilters = state.hasAppliedFiltersOrSearch
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = if (hasAppliedFilters) Icons.Filled.SearchOff else Icons.Filled.Refresh,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = if (hasAppliedFilters) {
                stringResource(R.string.coffee_shop_empty_list_on_search_title)
            } else {
                stringResource(R.string.coffee_shop_empty_list_title)
            },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (hasAppliedFilters) {
            Text(
                text = stringResource(R.string.coffee_shop_empty_list_on_search_description),
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            Button(
                onClick = onRefreshClick,
                contentPadding = ButtonDefaults.XSmallContentPadding,
                modifier = Modifier.heightIn(min = ButtonDefaults.XSmallContainerHeight)
            ) {
                Text(stringResource(R.string.coffee_shop_empty_list_description))
            }
        }
    }
}

@Composable
private fun LoadingProductListState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
private fun CoffeeShopContentPreview(
    @PreviewParameter(CoffeeShopUiStatePreviewParameterProvider::class)
    previewData: CoffeeShopPreviewParameterData
) {
    ScreenPreview {
        CoffeeShopContent(
            uiState = previewData.coffeeShopUiState,
            onCompanyFilterSelectedChange = { _, _ -> },
            onCoffeeProductItemAction = { _ -> },
            onTopBarAction = {},
            onRefreshClick = {},
            onScrollToTopComplete = {}
        )
    }
}

@Preview
@Composable
private fun LoadingCoffeeShopContentPreview(
    @PreviewParameter(CoffeeShopUiStatePreviewParameterProvider::class)
    previewData: CoffeeShopPreviewParameterData
) {
    ScreenPreview {
        CoffeeShopContent(
            uiState = previewData.coffeeShopUiState.copy(
                listState = ListState.Loading
            ),
            onCompanyFilterSelectedChange = { _, _ -> },
            onCoffeeProductItemAction = { _ -> },
            onTopBarAction = {},
            onRefreshClick = {},
            onScrollToTopComplete = {}
        )
    }
}

@Preview
@Composable
private fun EmptyCoffeeShopContentPreview(
    @PreviewParameter(CoffeeShopUiStatePreviewParameterProvider::class)
    previewData: CoffeeShopPreviewParameterData
) {
    ScreenPreview {
        CoffeeShopContent(
            uiState = previewData.coffeeShopUiState.copy(
                listState = ListState.Empty(false)
            ),
            onCompanyFilterSelectedChange = { _, _ -> },
            onCoffeeProductItemAction = { _ -> },
            onTopBarAction = {},
            onRefreshClick = {},
            onScrollToTopComplete = {}
        )
    }
}
