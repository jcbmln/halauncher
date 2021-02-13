package xyz.mcmxciv.halauncher

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.apps.App
import xyz.mcmxciv.halauncher.apps.AppManager

@ExperimentalCoroutinesApi
class HalauncherViewModel @ViewModelInject constructor(
    private val appManager: AppManager
) : BaseViewModel() {
    @FlowPreview
    val allAppListItems: LiveData<List<App>> = appManager.apps.asLiveData()

    private val _visibleAppListItems = MutableLiveData<List<App>>()
    val visibleAppListItems: LiveData<List<App>> = _visibleAppListItems

    fun onHideApp(activityName: String) {
//        viewModelScope.launch {
//            appManager.hideApp(activityName)
//            populateAppList()
//        }
    }

    fun onToggleAppVisibility(activityName: String) {
//        viewModelScope.launch {
//            appManager.toggleAppVisibility(activityName)
//            populateAppList()
//        }
    }
}
