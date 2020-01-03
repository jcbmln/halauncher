package xyz.mcmxciv.halauncher.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.di.components.FragmentComponent

abstract class BaseFragment : Fragment() {
    protected val component: FragmentComponent =
        LauncherApplication.instance.component.fragmentBuilder().build()

    fun displayMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}