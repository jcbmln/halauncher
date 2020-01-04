package xyz.mcmxciv.halauncher.di.components

import dagger.Subcomponent
import xyz.mcmxciv.halauncher.settings.SettingsViewModel
import xyz.mcmxciv.halauncher.di.modules.ApiModule
import xyz.mcmxciv.halauncher.di.modules.DiscoveryModule
import xyz.mcmxciv.halauncher.di.scopes.FragmentScope
import xyz.mcmxciv.halauncher.authentication.AuthenticationViewModel
import xyz.mcmxciv.halauncher.home.HomeViewModel
import xyz.mcmxciv.halauncher.integration.IntegrationViewModel
import xyz.mcmxciv.halauncher.setup.SetupViewModel

@FragmentScope
@Subcomponent(modules = [ApiModule::class, DiscoveryModule::class])
interface FragmentComponent {
    fun homeViewModel(): HomeViewModel
    fun authenticationViewModel(): AuthenticationViewModel
    fun integrationViewModel(): IntegrationViewModel
    fun setupViewModel(): SetupViewModel
    fun settingsViewModel(): SettingsViewModel

    @Subcomponent.Builder
    interface Builder {
        fun apiModule(apiModule: ApiModule): Builder
        fun discoveryModule(discoveryModule: DiscoveryModule): Builder
        fun build(): FragmentComponent
    }
}