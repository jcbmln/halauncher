package xyz.mcmxciv.halauncher.activities.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.mcmxciv.halauncher.repositories.ApplicationRepository
import xyz.mcmxciv.halauncher.models.AppInfo

class HomeViewModel : ViewModel() {
    val appList: MutableLiveData<List<AppInfo>> by lazy {
        MutableLiveData<List<AppInfo>>().also {
            viewModelScope.launch {
                val apps = ApplicationRepository().getAppList()
                appList.postValue(apps)
            }
        }
    }
}