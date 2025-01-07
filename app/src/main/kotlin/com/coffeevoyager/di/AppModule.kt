package com.coffeevoyager.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.room.Room
import coil3.ImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import com.coffeevoyager.BuildConfig
import com.coffeevoyager.UserPreferences
import com.coffeevoyager.data.shop.CoffeeShopFetcher
import com.coffeevoyager.data.shop.CoffeeShopRepository
import com.coffeevoyager.data.shop.FilterCoffeeProductRepository
import com.coffeevoyager.data.shop.SearchCoffeeProductRepository
import com.coffeevoyager.data.shop.SelectedFiltersCoffeeProductRepository
import com.coffeevoyager.data.shop.SortingCoffeeProductRepository
import com.coffeevoyager.data.shop.database.CoffeeShopDatabase
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanOptionDao
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductDao
import com.coffeevoyager.data.shop.database.dao.CoffeeBeanProductFtsDao
import com.coffeevoyager.data.shop.database.dao.CoffeeCompanyDao
import com.coffeevoyager.data.shop.database.dao.CoffeeProducerInfoDao
import com.coffeevoyager.data.shop.database.dao.CoffeeRecipeDao
import com.coffeevoyager.data.shop.database.dao.CoffeeTasteDao
import com.coffeevoyager.data.shop.database.dao.FilterCoffeeProductDao
import com.coffeevoyager.data.shop.remote.CoffeeShopApiService
import com.coffeevoyager.data.user.datastore.UserPreferencesDataSource
import com.coffeevoyager.data.user.datastore.UserPreferencesSerializer
import com.coffeevoyager.features.shop.products.CoffeeShopViewModel
import com.coffeevoyager.features.shop.products.filters.FiltersCoffeeShopViewModel
import com.coffeevoyager.features.shop.products.sorting.ChooseSortCoffeeShopViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class CoroutineScopeQualifiers {
    ApplicationScope
}

enum class DispatchersQualifiers {
    Default, IO
}

val coreCommonModule = module {
    single<CoroutineDispatcher>(named(DispatchersQualifiers.IO)) {
        Dispatchers.IO
    }
    single<CoroutineDispatcher>(named(DispatchersQualifiers.Default)) {
        Dispatchers.Default
    }
    single<CoroutineScope>(named(CoroutineScopeQualifiers.ApplicationScope)) {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named(DispatchersQualifiers.Default)))
    }
}

val networkModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
        }
    }
    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }
            defaultRequest {
                url(BuildConfig.SERVER_URL)
            }
        }
    }
    singleOf(::CoffeeShopApiService)
}

val databaseModule = module {
    single<CoffeeShopDatabase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = CoffeeShopDatabase::class.java,
            name = "coffee-shop-database",
        ).fallbackToDestructiveMigration().build()
    }

    singleOf(::UserPreferencesSerializer) {
        bind<Serializer<UserPreferences>>()
    }
    single<DataStore<UserPreferences>> {
        val scope = get<CoroutineScope>(named(CoroutineScopeQualifiers.ApplicationScope))
        val ioDispatcher = get<CoroutineDispatcher>(named(DispatchersQualifiers.Default))
        DataStoreFactory.create(
            serializer = get<Serializer<UserPreferences>>(),
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher)
        ) {
            androidContext().dataStoreFile("user_preferences.pb")
        }
    }
    singleOf(::UserPreferencesDataSource)
}

val shopDaoModule = module {
    single<CoffeeBeanProductDao> {
        get<CoffeeShopDatabase>().coffeeBeanProductDao()
    }
    single<CoffeeRecipeDao> {
        get<CoffeeShopDatabase>().coffeeRecipeDao()
    }
    single<CoffeeCompanyDao> {
        get<CoffeeShopDatabase>().coffeeCompanyDao()
    }
    single<CoffeeBeanOptionDao> {
        get<CoffeeShopDatabase>().coffeeBeanOptionDao()
    }
    single<CoffeeProducerInfoDao> {
        get<CoffeeShopDatabase>().coffeeProducerInfoDao()
    }
    single<CoffeeTasteDao> {
        get<CoffeeShopDatabase>().coffeeTasteDao()
    }
    single<FilterCoffeeProductDao> {
        get<CoffeeShopDatabase>().coffeeProductFilterDao()
    }
    single<CoffeeBeanProductFtsDao> {
        get<CoffeeShopDatabase>().coffeeBeanProductFtsDao()
    }
}

val dataModule = module {
    includes(databaseModule, shopDaoModule, networkModule, coreCommonModule)

    singleOf(::CoffeeShopFetcher)
    singleOf(::CoffeeShopRepository)
    singleOf(::FilterCoffeeProductRepository)
    singleOf(::SearchCoffeeProductRepository)
    singleOf(::SelectedFiltersCoffeeProductRepository)
    singleOf(::SortingCoffeeProductRepository)
}

val coreUiModule = module {
    single<ImageLoader> {
        ImageLoader.Builder(context = androidContext())
            .components {
                add(KtorNetworkFetcherFactory(httpClient = get<HttpClient>()))
                add(SvgDecoder.Factory())
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}

val shopFeatureModule = module {
    includes(dataModule, coreUiModule)

    viewModel<CoffeeShopViewModel> {
        CoffeeShopViewModel(
            coffeeShopFetcher = get(),
            filterRepository = get(),
            userPreferencesDataSource = get(),
            searchRepository = get(),
            coffeeShopRepository = get(),
            sortingProductRepository = get(),
            ioDispatcher = get<CoroutineDispatcher>(named(DispatchersQualifiers.IO)),
        )
    }
    viewModelOf(::FiltersCoffeeShopViewModel)
    viewModelOf(::ChooseSortCoffeeShopViewModel)
}

val appModule = module {
    includes(shopFeatureModule)
}
