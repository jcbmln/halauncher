package xyz.mcmxciv.halauncher.ui

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

inline fun <reified T : ViewModel> Fragment.createViewModel(
    crossinline factory: () -> T
): T = T::class.java.let { viewModel ->
    ViewModelProvider(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass == viewModel) {
                @Suppress("UNCHECKED_CAST")
                return factory() as T
            }
            throw IllegalArgumentException("Unexpected argument: $modelClass")
        }
    }).get(viewModel)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.fragmentViewModels(
    crossinline creator: () -> T
): Lazy<T> = createViewModelLazy(T::class, storeProducer = {
    viewModelStore
}, factoryProducer = {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            creator.invoke() as T
    }
})

fun Fragment.displayMessage(@StringRes resId: Int) {
    val message = requireContext().getString(resId)
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun Fragment.navigate(action: NavDirections) {
    val navController = findNavController()
    navController.navigate(action)
}

inline fun <reified T : LiveData<V>, reified V : Any> Fragment.observe(
    obj: T,
    crossinline block: (V) -> Unit
) {
    obj.observe(viewLifecycleOwner, Observer {
        block(it)
    })
}
