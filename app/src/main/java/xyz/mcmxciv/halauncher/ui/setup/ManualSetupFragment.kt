package xyz.mcmxciv.halauncher.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import xyz.mcmxciv.halauncher.databinding.FragmentManualSetupBinding
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.utils.textString

class ManualSetupFragment : LauncherFragment() {
    private lateinit var binding: FragmentManualSetupBinding
    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManualSetupBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.setupViewModel() }

        binding.discoveryModeButton.setOnClickListener {
            navigateToDiscoveryFragment()
        }

        binding.openUrlButton.setOnClickListener { finishManualSetup() }

        binding.hostText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_GO) {
                finishManualSetup()
                true
            } else false
        }
    }

    private fun finishManualSetup() {
        val text = binding.hostText.textString

        if (!text.isBlank()) {
            viewModel.setUrl(text)
            navigateToAuthenticationGraph()
        }
    }

    private fun navigateToDiscoveryFragment() {
        val action =
            ManualSetupFragmentDirections.actionManualSetupFragmentToDiscoveryFragment()
        findNavController().navigate(action)
    }

    private fun navigateToAuthenticationGraph() {
        val action =
            ManualSetupFragmentDirections.actionGlobalAuthenticationNavigationGraph()
        findNavController().navigate(action)
    }
}
