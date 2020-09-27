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
    private val _allAppListItems = MutableLiveData<List<AppListItem>>()
    val allAppListItems: LiveData<List<AppListItem>> = _allAppListItems

    private val _visibleAppListItems = MutableLiveData<List<AppListItem>>()
    val visibleAppListItems: LiveData<List<AppListItem>> = _visibleAppListItems

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
            val appListItems = appUseCase.getAllAppListItems()
            _allAppListItems.postValue(appListItems)
            _visibleAppListItems.postValue(appListItems.filterNot { item -> item.app.isHidden })
        }
    }
}
