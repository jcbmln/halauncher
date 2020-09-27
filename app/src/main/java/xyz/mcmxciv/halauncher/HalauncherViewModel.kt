package xyz.mcmxciv.halauncher

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.apps.AppListItem
import xyz.mcmxciv.halauncher.apps.AppUseCase

class HalauncherViewModel @ViewModelInject constructor(
    private val appUseCase: AppUseCase
) : BaseViewModel() {
    private val _appDrawerItems = MutableLiveData<List<AppListItem>>()
    val appListItems: LiveData<List<AppListItem>> = _appDrawerItems

    init {
        populateAppList()
    }

    fun onHideApp(activityName: String) {
        viewModelScope.launch {
            appUseCase.hideApp(activityName)
            populateAppList()
        }
    }

    fun onToggleAppVisibility(activityName: String) {
        viewModelScope.launch {
            appUseCase.toggleAppVisibility(activityName)
            populateAppList()
        }
    }

    private fun populateAppList() {
        viewModelScope.launch {
            _appDrawerItems.postValue(appUseCase.getAllAppListItems())
        }
    }
}
