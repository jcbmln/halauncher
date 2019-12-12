package xyz.mcmxciv.halauncher.fragments.setup.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import xyz.mcmxciv.halauncher.databinding.ManualSetupFragmentBinding
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BaseFragment

class ManualSetupFragment : BaseFragment() {
    private lateinit var binding: ManualSetupFragmentBinding
    private lateinit var viewModel: ManualSetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ManualSetupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.manualSetupViewModel() }

        binding.discoveryModeButton.setOnClickListener { view ->
            val action =
                ManualSetupFragmentDirections.actionManualSetupFragmentToDiscoveryFragment()
            view.findNavController().navigate(action)
        }

        binding.setupManualButton.setOnClickListener { view ->
            val text = binding.setupHostText.text.toString()

            if (!text.isBlank()) {
                viewModel.setUrl(text)

                val action = ManualSetupFragmentDirections
                    .actionGlobalAuthenticationNavigationGraph()
                view.findNavController().navigate(action)
            }
        }
    }
}
