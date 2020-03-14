package xyz.mcmxciv.halauncher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.mcmxciv.halauncher.data.interactors.AppsInteractor
import xyz.mcmxciv.halauncher.data.interactors.IntegrationInteractor
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.data.models.Config
import xyz.mcmxciv.halauncher.models.apps.AppListItem
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val appsInteractor: AppsInteractor,
    private val sessionInteractor: SessionInteractor,
    private val integrationInteractor: IntegrationInteractor
) : ViewModel() {
    val appListItems = MutableLiveData<List<AppListItem>>().also { data ->
        viewModelScope.launch {
            data.postValue(appsInteractor.getAppListItems())
        }
    }

    private val configData = MutableLiveData<Config?>().also { data ->
        val exceptionHandler = CoroutineExceptionHandler { _, ex ->
            Timber.e(ex)
        }

        viewModelScope.launch(exceptionHandler) {
            data.postValue(integrationInteractor.getConfig())
        }
    }
    val config: LiveData<Config?> = configData

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