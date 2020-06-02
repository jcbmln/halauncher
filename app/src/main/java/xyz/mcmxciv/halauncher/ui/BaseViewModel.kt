package xyz.mcmxciv.halauncher.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent

open class BaseViewModel : ViewModel() {
    protected val navigationEvent = LiveEvent<NavDirections>()
    val navigation: LiveData<NavDirections> = navigationEvent
}

