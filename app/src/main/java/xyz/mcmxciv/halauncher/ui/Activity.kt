package xyz.mcmxciv.halauncher.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T: ViewModel> AppCompatActivity.createViewModel(
    crossinline factory: () -> T
): T = T::class.java.let { viewModel ->
    ViewModelProvider(this, object: ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass == viewModel) {
                @Suppress("UNCHECKED_CAST")
                return factory() as T
            }
            throw IllegalArgumentException("Unexpected argument: $modelClass")
        }
    }).get(viewModel)
}

inline fun <reified T : LiveData<V>, reified V: Any> AppCompatActivity.observe(
    obj: T, crossinline block: (V) -> Unit
) {
    obj.observe(this, Observer {
        block(it)
    })
}