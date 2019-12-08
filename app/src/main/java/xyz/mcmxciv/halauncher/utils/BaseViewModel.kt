package xyz.mcmxciv.halauncher.utils

import androidx.lifecycle.ViewModel
import xyz.mcmxciv.halauncher.fragments.home.HomeViewModel
import xyz.mcmxciv.halauncher.fragments.integration.IntegrationViewModel
import xyz.mcmxciv.halauncher.di.ApiModule
import xyz.mcmxciv.halauncher.di.DaggerAppComponent
import xyz.mcmxciv.halauncher.di.AppComponent
import xyz.mcmxciv.halauncher.di.DbModule
import xyz.mcmxciv.halauncher.fragments.authentication.AuthenticationViewModel

abstract class BaseViewModel : ViewModel() {
    private val component: AppComponent = DaggerAppComponent
        .builder()
        .apiModule(ApiModule())
        .dbModule(DbModule())
        .build()

    init {
        inject()
    }

    private fun inject() {
        when (this) {
            is AuthenticationViewModel -> component.inject(this)
            is IntegrationViewModel -> component.inject(this)
            is HomeViewModel -> component.inject(this)
        }
    }
}