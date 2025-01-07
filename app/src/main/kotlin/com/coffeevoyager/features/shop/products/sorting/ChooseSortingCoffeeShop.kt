package com.coffeevoyager.features.shop.products.sorting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coffeevoyager.core.ui.ImmutableList
import com.coffeevoyager.core.ui.ScreenPreview
import com.coffeevoyager.core.ui.extension.animateToDismiss
import com.coffeevoyager.domain.shop.SortingCoffeeProduct
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSortingCoffeeShopBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooseSortCoffeeShopViewModel = koinViewModel()
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sortItems by viewModel.sortItems.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val animateToDismiss = {
        bottomSheetState.animateToDismiss(scope, onDismissRequest)
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        ChooseSortingCoffeeShopContent(
            sortItems = sortItems,
            onSortItemClick = {
                viewModel.onSortItemClick(it)
                animateToDismiss()
            }
        )
    }
}

@Composable
fun ChooseSortingCoffeeShopContent(
    sortItems: ImmutableList<SortingCoffeeShopUiItem>,
    onSortItemClick: (SortingCoffeeProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    sortItems.forEach { sortItem ->
        SortItem(item = sortItem, onClick = { onSortItemClick(sortItem.sorting) })
        HorizontalDivider()
    }
}

@Composable
fun SortItem(item: SortingCoffeeShopUiItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(
                text = item.name.asString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
            )
        },
        modifier = modifier.clickable(enabled = true, onClick = onClick),
    )
}

@Preview
@Composable
private fun SortCoffeeShopContentPreview() {
    val sortItems = ImmutableList(SortingCoffeeProduct.entries.map(SortingCoffeeProduct::asUiItem))
    ScreenPreview {
        Column { ChooseSortingCoffeeShopContent(sortItems = sortItems, onSortItemClick = {}) }
    }
}
