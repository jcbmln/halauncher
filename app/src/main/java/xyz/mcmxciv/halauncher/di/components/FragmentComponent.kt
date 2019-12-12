package xyz.mcmxciv.halauncher.di.components

import dagger.Subcomponent
import xyz.mcmxciv.halauncher.di.modules.ApiModule
import xyz.mcmxciv.halauncher.di.modules.DiscoveryModule
import xyz.mcmxciv.halauncher.di.scopes.FragmentScope
import xyz.mcmxciv.halauncher.fragments.authentication.AuthenticationViewModel
import xyz.mcmxciv.halauncher.fragments.home.HomeViewModel
import xyz.mcmxciv.halauncher.fragments.integration.IntegrationViewModel
import xyz.mcmxciv.halauncher.fragments.setup.discovery.DiscoveryViewModel
import xyz.mcmxciv.halauncher.fragments.setup.manual.ManualSetupViewModel

@FragmentScope
@Subcomponent(modules = [ApiModule::class, DiscoveryModule::class])
interface FragmentComponent {
    fun homeViewModel(): HomeViewModel
    fun authenticationViewModel(): AuthenticationViewModel
    fun integrationViewModel(): IntegrationViewModel
    fun discoveryViewModel(): DiscoveryViewModel
    fun manualSetupViewModel(): ManualSetupViewModel

    @Subcomponent.Builder
    interface Builder {
        fun apiModule(apiModule: ApiModule): Builder
        fun discoveryModule(discoveryModule: DiscoveryModule): Builder
        fun build(): FragmentComponent
    }
}