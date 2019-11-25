package xyz.mcmxciv.halauncher.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.ManualSetupFragmentBinding
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener

class ManualSetupFragment : Fragment() {
    private lateinit var binding: ManualSetupFragmentBinding
    private lateinit var listener: ServiceSelectedListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ManualSetupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.setupManualButton.setOnClickListener {
            val text = binding.setupHostText.text.toString()

            if (!text.isBlank()) {
                listener.onServiceSelected(text)
            }
        }
    }

    fun setServiceSelectedListener(callback: ServiceSelectedListener) {
        listener = callback
    }
}
