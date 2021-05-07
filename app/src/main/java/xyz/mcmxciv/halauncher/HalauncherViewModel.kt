package xyz.mcmxciv.halauncher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import xyz.mcmxciv.halauncher.apps.App
import xyz.mcmxciv.halauncher.apps.AppManager
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HalauncherViewModel @Inject constructor(
    private val appManager: AppManager
) : BaseViewModel() {
    @FlowPreview
    val allApps: LiveData<List<App>> = appManager.apps.asLiveData()

    @FlowPreview
    val visibleApps: LiveData<List<App>> = appManager.apps.asLiveData()

    fun onToggleAppVisibility(activityName: String) {
        appManager.toggleAppVisibility(activityName)
    }
}
