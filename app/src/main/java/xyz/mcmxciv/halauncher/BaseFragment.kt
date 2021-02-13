package xyz.mcmxciv.halauncher

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

open class BaseFragment : Fragment() {
    fun displayMessage(@StringRes resId: Int) {
        val message = requireContext().getString(resId)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

fun <T : LiveData<V>, V : Any> Fragment.observe(obj: T, block: (V) -> Unit) {
    obj.observe(viewLifecycleOwner, Observer { block(it) })
}

fun Fragment.navigate(action: NavDirections) {
    val navController = findNavController()
    navController.navigate(action)
}

fun Fragment.requireHalauncherActivity(): HalauncherActivity {
    val activity = requireActivity()

    if (activity !is HalauncherActivity)
        throw IllegalStateException("Fragment $this is not attached to HalauncherActivity.")

    return activity
}
