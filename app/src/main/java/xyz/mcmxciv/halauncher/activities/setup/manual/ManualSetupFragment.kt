package xyz.mcmxciv.halauncher.activities.setup.manual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.activities.setup.SetupFragment

import xyz.mcmxciv.halauncher.databinding.ManualSetupFragmentBinding
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener

class ManualSetupFragment : SetupFragment() {
    private lateinit var binding: ManualSetupFragmentBinding
    //lateinit var serviceSelectedListener: ServiceSelectedListener

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
                serviceSelectedListener.onServiceSelected(text)
            }
        }
    }
}
