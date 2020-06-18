package xyz.mcmxciv.halauncher.di

import dagger.Subcomponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.mcmxciv.halauncher.authentication.AuthenticationViewModel
import xyz.mcmxciv.halauncher.discovery.DiscoveryViewModel
import xyz.mcmxciv.halauncher.home.HomeViewModel
import xyz.mcmxciv.halauncher.integration.IntegrationViewModel
import xyz.mcmxciv.halauncher.launch.LaunchViewModel
import xyz.mcmxciv.halauncher.manualsetup.ManualSetupViewModel

@ViewScope
@Subcomponent
interface ViewComponent {
    fun authenticationViewModel(): AuthenticationViewModel
    @ExperimentalCoroutinesApi
    fun discoveryViewModel(): DiscoveryViewModel
    fun homeViewModel(): HomeViewModel
    fun integrationViewModel(): IntegrationViewModel
    fun launchViewModel(): LaunchViewModel
    fun manualSetupViewModel(): ManualSetupViewModel

    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewComponent
    }
}
