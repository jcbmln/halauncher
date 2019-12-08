package xyz.mcmxciv.halauncher.di

import dagger.Component
import xyz.mcmxciv.halauncher.fragments.home.HomeViewModel
import xyz.mcmxciv.halauncher.fragments.integration.IntegrationViewModel
import xyz.mcmxciv.halauncher.fragments.authentication.AuthenticationViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, DbModule::class])
interface AppComponent {
    fun inject(authenticationViewModel: AuthenticationViewModel)
    fun inject(integrationViewModel: IntegrationViewModel)
    fun inject(homeViewModel: HomeViewModel)
}