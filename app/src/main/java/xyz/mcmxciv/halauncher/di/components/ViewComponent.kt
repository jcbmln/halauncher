package xyz.mcmxciv.halauncher.di.components

import dagger.Subcomponent
import xyz.mcmxciv.halauncher.di.modules.DiscoveryModule
import xyz.mcmxciv.halauncher.di.scopes.ViewScope
import xyz.mcmxciv.halauncher.ui.authentication.AuthenticationViewModel
import xyz.mcmxciv.halauncher.ui.home.HomeFragment
import xyz.mcmxciv.halauncher.ui.home.HomeViewModel
import xyz.mcmxciv.halauncher.ui.integration.IntegrationViewModel
import xyz.mcmxciv.halauncher.ui.launch.LaunchViewModel
import xyz.mcmxciv.halauncher.ui.settings.SettingsViewModel
import xyz.mcmxciv.halauncher.ui.setup.SetupViewModel

@ViewScope
@Subcomponent(modules = [DiscoveryModule::class])
interface ViewComponent {
    fun authenticationViewModel(): AuthenticationViewModel
    fun homeViewModel(): HomeViewModel
    fun integrationViewModel(): IntegrationViewModel
    fun launchViewModel(): LaunchViewModel
    fun setupViewModel(): SetupViewModel
    fun settingsViewModel(): SettingsViewModel

    fun inject(homeFragment: HomeFragment)

    @Subcomponent.Builder
    interface Builder {
        fun discoveryModule(discoveryModule: DiscoveryModule): Builder
        fun build(): ViewComponent
    }
}
