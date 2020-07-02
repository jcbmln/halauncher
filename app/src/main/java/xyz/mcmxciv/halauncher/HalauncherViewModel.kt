package xyz.mcmxciv.halauncher

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.apps.AppDrawerItem
import xyz.mcmxciv.halauncher.apps.AppUseCase

class HalauncherViewModel @ViewModelInject constructor(
    private val appUseCase: AppUseCase
) : BaseViewModel() {
    private val _appDrawerItems = MutableLiveData<List<AppDrawerItem>>().also {
        viewModelScope.launch {
            it.postValue(appUseCase.getAppDrawerItems())
        }
    }

    val appDrawerItems: LiveData<List<AppDrawerItem>> = _appDrawerItems

    fun onHideApp(activityName: String) {
        viewModelScope.launch {
            appUseCase.hideApp(activityName)
        }
    }
}
