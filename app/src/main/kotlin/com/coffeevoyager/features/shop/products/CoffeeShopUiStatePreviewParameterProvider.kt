package com.coffeevoyager.features.shop.products

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.coffeevoyager.data.shop.dto.CoffeeBeanOptionDto
import com.coffeevoyager.domain.shop.Currency
import com.coffeevoyager.domain.shop.SortingCoffeeProduct
import com.coffeevoyager.features.shop.products.CoffeeShopPreviewParameterDataProvider.coffeeBeanList
import com.coffeevoyager.features.shop.products.CoffeeShopPreviewParameterDataProvider.companyList
import com.coffeevoyager.features.shop.products.sorting.getUiStringValue
import kotlin.random.Random

class CoffeeShopUiStatePreviewParameterProvider :
    PreviewParameterProvider<CoffeeShopPreviewParameterData> {
    override val values = sequenceOf(
        CoffeeShopPreviewParameterData(
            coffeeShopUiState = CoffeeShopUiState(
                topBarState = CoffeeShopUiState.TopBarState(
                    searchFieldState = TextFieldState(),
                    hasAppliedFilters = true,
                    favoriteFilterState = CoffeeShopUiState.FavoriteFilterState(true, false),
                    selectedSorting = SortingCoffeeProduct.NewestArrivals.getUiStringValue(),
                    countInCart = 4
                ),
                listState = CoffeeShopUiState.ListState.Success(
                    companies = companyList,
                    coffeeBeanProducts = coffeeBeanList,
                    scrollToTop = false,
                ),
                showBottomSheet = null
            )
        )
    )
}

data class CoffeeShopPreviewParameterData(
    val coffeeShopUiState: CoffeeShopUiState
)

private object CoffeeShopPreviewParameterDataProvider {
    val companyList = CoffeeCompany.entries.mapIndexed { index, coffeeCompany ->
        CoffeeShopUiState.Company(
            id = index.toString(),
            name = coffeeCompany.title,
            imageUrl = coffeeCompany.imageUrl,
            selected = false
        )
    }

    val coffeeBeanList = buildList {
        repeat(20) { index ->
            val coffeeCompany = CoffeeCompany.entries.random()
            add(
                CoffeeShopUiState.Product(
                    id = index.toString(),
                    name = LoremIpsum(2).values.first(),
                    companyName = coffeeCompany.name,
                    imageUrl = coffeeCompany.coffeeBeanImageUrls.random(),
                    companyLogoUrl = coffeeCompany.imageUrl,
                    price = Random.nextInt(10, 1000).toString(),
                    currency = Currency.UAH.name,
                    isFavorite = false,
                    addedToCart = false
                )
            )
        }
    }
}

private enum class CoffeeCompany(val title: String) {
    SVIT_KAVY("Svit Kavy"), FUNT("Funt Coffee"), MAD_HEADS("Mad Heads");

    val imageUrl: String
        get() = when (this) {
            SVIT_KAVY -> svitkavyImageUrls.first()
            FUNT -> funtCoffeeImageUrls.first()
            MAD_HEADS -> madHeadsCoffeeImageUrls.first()
        }

    val coffeeBeanImageUrls: List<String>
        get() = when (this) {
            SVIT_KAVY -> svitkavyImageUrls.drop(1)
            FUNT -> funtCoffeeImageUrls.drop(1)
            MAD_HEADS -> madHeadsCoffeeImageUrls.drop(1)
        }

    companion object {
        fun getCompanyByTitle(title: String): CoffeeCompany? =
            entries.find { it.title == title }
    }
}

private val madHeadsCoffeeImageUrls = listOf(
    "https://madheadscoffee.com/logo/dark.svg",
    "https://cdn.madheadscoffee.com/s/1/p/1706/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1700/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1707/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1682/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1701/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1681/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1713/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1676/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1714/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1708/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1679/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1709/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1684/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1685/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1675/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1705/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1715/0/medium.png",
    "https://cdn.madheadscoffee.com/s/1/p/1687/0/medium.png"
)

private val svitkavyImageUrls = listOf(
    "https://svitkavy.com/static/svitkavy_shuup_theme/images/logo.5d6e371b5c49.png",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/32/9e/329e9cb9-80ec-4fed-94e1-8a5e129f989f/kolumbiia_mirador_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/65/8c/658cb3f3-5317-484b-b337-28d43a8a8432/meksika_mazateka_bez_kofeyinu_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/7f/45/7f452312-12fe-4afe-b703-5c56e36707d0/boa_vista_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/3a/d8/3ad8e775-ccce-485f-8227-cba86f028cfc/kolumbiia_uyila_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/13/5e/135e9f2c-674d-4b30-aa2d-02fa7c94a375/gonduras_kavaleras_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/1d/82/1d826282-729f-4378-8338-fa423ee53446/kolumbiia_bez_kofeyinu_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/fa/8e/fa8eb257-f764-4ccf-bf83-61ceda825c84/niagishiru_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/a1/1f/a11f141d-10df-4567-b28c-f405cc387527/espreso_sumish_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/56/79/56794bd8-9c9e-4097-8bc4-63171c84decb/eiopiia_uraga_sait__1.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/1c/b8/1cb8c593-7cc1-43fa-b774-040a7ad624a3/burundi_mugoyi_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/61/40/614016d0-6634-4c59-a088-0e0dbdfbbad5/livivska_kava_mokap_sait__1.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/c4/3c/c43cb095-9c63-4561-a6df-4a5786eddc50/kolumbiia_milagro_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/47/b2/47b2ab52-7636-4f47-a8c3-5b8a5d04543f/gonduras_santiago_mokap_sait_.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/de/d0/ded0585b-83f5-4b2c-ad32-61c097842c1f/dripi_sait_1_3jpg__0x640_q85_subsampling-2.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/8f/a1/8fa1b728-b0cf-4829-b583-9526c7da0b26/dripi_sait_1_3.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/34/3a/343a3433-b450-412a-bfbb-f2076f157453/dripi_sait_1_3.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/55/10/551024b8-a2a5-4d46-93e2-c8733662778b/dripi_sait_1_2.jpg__300x300_q85_subsampling-2.jpg",
    "https://svitkavy.com/media/filer_public_thumbnails/filer_public/a7/43/a743f333-446d-4a18-93b0-3f3e68e00bdd/dripi_sait_1_2.jpg__300x300_q85_subsampling-2.jpg"
)

private val funtCoffeeImageUrls = listOf(
    "https://funt.coffee/images/header/logo.svg",
    "https://admin.funt.coffee/assets/6aeb78a8-6a6c-4856-87dc-33a164722ac0?format=webp",
    "https://admin.funt.coffee/assets/690d6c28-9259-411f-b9a9-cd6a67461cf1?format=webp",
    "https://admin.funt.coffee/assets/170d8236-4b9a-4a6c-92b1-eb654bde9115?format=webp",
    "https://admin.funt.coffee/assets/f498db69-3411-43a9-b608-b52d95be3185?format=webp",
    "https://admin.funt.coffee/assets/b01ed756-c0b9-4eef-8335-12347a58f6e2?format=webp",
    "https://admin.funt.coffee/assets/b39f04bf-f917-4d31-aece-4216e7e6ecf3?format=webp",
    "https://admin.funt.coffee/assets/ab0a640b-d2c5-463e-b758-e1adc5798869?format=webp",
    "https://admin.funt.coffee/assets/cd85c4f3-cdad-49ff-9c37-36c78d3ce904?format=webp",
    "https://admin.funt.coffee/assets/819f050f-afaf-4fa4-a4e4-c7af8852dc6d?format=webp",
    "https://admin.funt.coffee/assets/325b4cb2-4c68-47bb-a6a5-4146ed5ec2bf?format=webp",
    "https://admin.funt.coffee/assets/3bf8f5bd-cb7b-4b9d-b147-4940adbb2d60?format=webp",
    "https://admin.funt.coffee/assets/375493fe-28f3-4aa2-b32f-d414130363c1?format=webp",
    "https://admin.funt.coffee/assets/37c013c3-4b46-4667-bd78-964288f9f01a?format=webp",
    "https://admin.funt.coffee/assets/f74726b9-58dc-4f7f-9f24-78d6f5fe14a8?format=webp",
    "https://admin.funt.coffee/assets/657ca475-9514-4c54-af53-604e97b0e08f?format=webp",
    "https://admin.funt.coffee/assets/be42dc0e-a46b-4d1d-9fdb-1b34a7b57c81?format=webp",
    "https://admin.funt.coffee/assets/710b1db2-9146-49da-9485-566ea2e6cc87?format=webp",
    "https://admin.funt.coffee/assets/0271ec2d-e46f-4779-a6f2-71ffedc8e06e?format=webp"
)

private enum class ProcessingCoffeeBean {
    Washed, Honey, Natural
}

private val options = listOf(
    CoffeeBeanOptionDto(
        id = "1",
        name = "roastType",
        values = listOf("Espresso", "Filter"),
    ),
    CoffeeBeanOptionDto(
        id = "2",
        name = "packaging",
        values = listOf("0.25 kg", "1 kg")
    ),
    CoffeeBeanOptionDto(
        id = "3",
        name = "grindType",
        values = listOf(
            "Beans",
            "V-60",
            "Aeropress",
            "Moka pot",
            "Cezve",
            "Espresso",
            "Chemex",
            "French Press",
            "Filter",
            "Cup",
            "Home espresso coffee machine"
        )
    )
)
