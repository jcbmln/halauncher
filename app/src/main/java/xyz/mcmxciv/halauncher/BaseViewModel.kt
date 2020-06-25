package xyz.mcmxciv.halauncher

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.hadilq.liveevent.LiveEvent

open class BaseViewModel : ViewModel() {
    protected val navigationEvent = LiveEvent<NavDirections>()
    val navigation: LiveData<NavDirections> = navigationEvent

    protected val errorEvent = LiveEvent<Int>()
    val error: LiveData<Int> = errorEvent
}
