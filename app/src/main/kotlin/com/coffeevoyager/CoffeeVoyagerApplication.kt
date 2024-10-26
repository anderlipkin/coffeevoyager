package com.coffeevoyager

import android.app.Application
import com.coffeevoyager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class CoffeeVoyagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@CoffeeVoyagerApplication)
            modules(appModule)
        }
    }
}