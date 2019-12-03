package xyz.mcmxciv.halauncher.activities.setup.manual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import xyz.mcmxciv.halauncher.LauncherApplication

import xyz.mcmxciv.halauncher.databinding.ManualSetupFragmentBinding
import xyz.mcmxciv.halauncher.utils.AppPreferences

class ManualSetupFragment : Fragment() {
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

        binding.discoveryModeButton.setOnClickListener {
            val action =
                ManualSetupFragmentDirections.actionManualSetupFragmentToDiscoveryFragment()
            it.findNavController().navigate(action)
        }

        binding.setupManualButton.setOnClickListener {
            val text = binding.setupHostText.text.toString()

            if (!text.isBlank()) {
                val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())
                prefs.url = text

                val action = ManualSetupFragmentDirections
                    .actionManualSetupFragmentToAuthenticationFragment()
                it.findNavController().navigate(action)
                //serviceSelectedListener.onServiceSelected(text)
            }
        }
    }
}
