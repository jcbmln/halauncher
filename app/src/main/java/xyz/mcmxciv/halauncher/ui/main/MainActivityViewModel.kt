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
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.utils.ResourceProvider
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val sessionInteractor: SessionInteractor
) : ViewModel() {
    private val themeData = MutableLiveData<HassTheme>()
    val theme: LiveData<HassTheme> = themeData

    fun revokeSession() {
        viewModelScope.launch {
            sessionInteractor.revokeSession()
        }
    }
}
