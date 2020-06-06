package xyz.mcmxciv.halauncher

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
import xyz.mcmxciv.halauncher.di.ViewComponent

open class BaseFragment : Fragment() {
    protected val component: ViewComponent = HalauncherApplication.instance.component
        .viewComponentBuilder()
        .build()

    fun <T: LiveData<V>, V: Any> observe(obj: T, block: (V) -> Unit) {
        obj.observe(viewLifecycleOwner, Observer { block(it) })
    }

    fun navigate(action: NavDirections) {
        val navController = findNavController()
        navController.navigate(action)
    }

    fun displayMessage(@StringRes resId: Int) {
        val message = requireContext().getString(resId)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> BaseFragment.fragmentViewModels(
    crossinline creator: () -> T
): Lazy<T> = createViewModelLazy(T::class, storeProducer = {
    viewModelStore
}, factoryProducer = {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            creator.invoke() as T
    }
})
