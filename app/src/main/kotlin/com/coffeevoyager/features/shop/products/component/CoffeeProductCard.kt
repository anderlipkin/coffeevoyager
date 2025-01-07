package com.coffeevoyager.features.shop.products.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.coffeevoyager.core.ui.placeholderIfPreview
import com.coffeevoyager.features.shop.products.CoffeeProductItemAction
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.Product

@Composable
internal fun CoffeeProductCard(
    coffeeProduct: Product,
    onItemAction: (CoffeeProductItemAction) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = {
            onItemAction(CoffeeProductItemAction.ItemClick(coffeeProduct.id))
        },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = modifier.size(160.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(Modifier.fillMaxWidth()) {
                CoffeeProductImage(
                    imageUrl = coffeeProduct.imageUrl,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
                FavoriteButton(
                    isFavorite = coffeeProduct.isFavorite,
                    onCheckedChange = { isFavorite ->
                        onItemAction(
                            CoffeeProductItemAction.FavoriteClick(
                                coffeeProduct.id,
                                isFavorite
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 2.dp)
                )
            }
            Text(
                text = coffeeProduct.name,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = coffeeProduct.companyName,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${coffeeProduct.price} ${coffeeProduct.currency}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                )
                AddToCartButton(
                    onClick = {
                        onItemAction(CoffeeProductItemAction.AddToCartClick(coffeeProduct.id))
                    }
                )
            }
        }
    }
}

@Composable
private fun CoffeeProductImage(imageUrl: String, modifier: Modifier = Modifier) {
    val imagePainter = rememberAsyncImagePainter(
        model = imageUrl,
        error = placeholderIfPreview {
            rememberVectorPainter(Icons.Filled.Coffee)
        }
    )
    Image(
        painter = imagePainter,
        contentDescription = null,
        contentScale = ContentScale.Inside,
        modifier = modifier.size(72.dp)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddToCartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier.size(IconButtonDefaults.xSmallIconSize)
        )
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onCheckedChange(!isFavorite) },
        modifier = modifier.size(28.dp)
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavorite) MaterialTheme.colorScheme.errorContainer else LocalContentColor.current,
            modifier = Modifier.size(16.dp)
        )
    }
}
