package xyz.mcmxciv.halauncher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.data.interactors.SessionInteractor
import xyz.mcmxciv.halauncher.ui.HassTheme
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
