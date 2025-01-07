package com.coffeevoyager.features.shop.products.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.coffeevoyager.features.shop.products.CoffeeShopUiState.Company
import com.coffeevoyager.core.ui.placeholderIfPreview

@Composable
internal fun CompanyCard(
    company: Company,
    onSelectedChange: (id: String, selected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.primaryContainer
    val border = remember(company.selected) {
        BorderStroke(
            width = if (company.selected) (1).dp else Dp.Unspecified,
            color = borderColor
        )
    }
    OutlinedCard(
        onClick = { onSelectedChange(company.id, !company.selected) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        border = border,
        modifier = modifier
            .width(128.dp)
            .height(72.dp)
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val imagePainter = rememberAsyncImagePainter(
                    company.imageUrl,
                    error = placeholderIfPreview {
                        rememberVectorPainter(Icons.Filled.House)
                    }
                )
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    contentScale = ContentScale.Inside,
                    alignment = Alignment.BottomCenter
                )
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
