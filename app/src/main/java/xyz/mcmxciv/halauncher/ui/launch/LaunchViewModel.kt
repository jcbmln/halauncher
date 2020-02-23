package xyz.mcmxciv.halauncher.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import xyz.mcmxciv.halauncher.data.repositories.LocalStorageRepository
import xyz.mcmxciv.halauncher.models.SessionState
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository
) : ViewModel() {
    private val sessionStateEvent by lazy {
        val sessionState = when {
            !localStorageRepository.hasHomeAssistantInstance -> SessionState.NEW_USER
            !localStorageRepository.isAuthenticated -> SessionState.UNAUTHENTICATED
            else -> SessionState.AUTHENTICATED
        }

        val liveEvent = LiveEvent<SessionState>()
        liveEvent.postValue(sessionState)
        return@lazy liveEvent
    }
    val sessionState: LiveData<SessionState> = sessionStateEvent
}