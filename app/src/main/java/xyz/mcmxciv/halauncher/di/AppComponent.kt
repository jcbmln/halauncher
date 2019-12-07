package xyz.mcmxciv.halauncher.di

import dagger.Component
import xyz.mcmxciv.halauncher.activities.home.HomeViewModel
import xyz.mcmxciv.halauncher.activities.launch.LaunchViewModel
import xyz.mcmxciv.halauncher.activities.setup.integration.IntegrationViewModel
import xyz.mcmxciv.halauncher.fragments.AuthenticationViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, DbModule::class])
interface AppComponent {
    fun inject(launchViewModel: LaunchViewModel)
    fun inject(authenticationViewModel: AuthenticationViewModel)
    fun inject(integrationViewModel: IntegrationViewModel)
    fun inject(homeViewModel: HomeViewModel)
}