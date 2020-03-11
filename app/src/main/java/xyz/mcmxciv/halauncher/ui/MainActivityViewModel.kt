package xyz.mcmxciv.halauncher.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.data.interactors.AppsInteractor
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val appsInteractor: AppsInteractor,
    private val sessionInteractor: SessionInteractor
) : ViewModel() {
    val appListItems = MutableLiveData<List<AppListItem>>().also {
        viewModelScope.launch {
            it.postValue(appsInteractor.getAppListItems())
        }
    }

    fun updateAppListItems() {
        viewModelScope.launch {
            appListItems.postValue(appsInteractor.getAppListItems())
        }
    }

    fun revokeSession() {
        viewModelScope.launch {
            sessionInteractor.revokeSession()
        }
    }
}