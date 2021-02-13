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
