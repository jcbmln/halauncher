package xyz.mcmxciv.halauncher.di

import dagger.Subcomponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.mcmxciv.halauncher.ui.discovery.DiscoveryViewModel
import xyz.mcmxciv.halauncher.ui.launch.LaunchViewModel
import xyz.mcmxciv.halauncher.ui.manual.ManualSetupFragment
import xyz.mcmxciv.halauncher.ui.manual.ManualSetupViewModel
import javax.inject.Provider

@ViewScope
@Subcomponent(modules = [DataModule::class])
interface ViewComponent {
    @ExperimentalCoroutinesApi
    fun discoveryViewModelProvider(): Provider<DiscoveryViewModel>
    fun launchViewModelProvider(): Provider<LaunchViewModel>
    fun manualSetupViewModelProvider(): Provider<ManualSetupViewModel>

    @Subcomponent.Builder
    interface Builder {
        fun build(): ViewComponent
    }
}
