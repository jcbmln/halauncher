package xyz.mcmxciv.halauncher.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T: ViewModel> Fragment.createViewModel(
    crossinline factory: () -> T
): T = T::class.java.let { viewModel ->
    ViewModelProvider(this, object: ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass == viewModel) {
                @Suppress("UNCHECKED_CAST")
                return factory() as T
            }
            throw IllegalArgumentException("Unexpected argument: $modelClass")
        }
    }).get(viewModel)
}