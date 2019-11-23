package xyz.mcmxciv.halauncher.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener

class ManualSetupFragment : Fragment() {
    private lateinit var listener: ServiceSelectedListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.manual_setup_fragment, container, false)
    }

    fun setServiceSelectedListener(callback: ServiceSelectedListener) {
        listener = callback
    }
}
