package xyz.mcmxciv.halauncher.ui.setup.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import xyz.mcmxciv.halauncher.databinding.FragmentManualSetupBinding
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.setup.SetupViewModel
import xyz.mcmxciv.halauncher.utils.textString

class ManualSetupFragment : LauncherFragment() {
    private lateinit var binding: FragmentManualSetupBinding
    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManualSetupBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.setupViewModel() }

        binding.discoveryModeButton.setOnClickListener {
            navigate(ManualSetupFragmentDirections.actionManualSetupFragmentToDiscoveryFragment())
        }

        binding.openUrlButton.setOnClickListener { finishManualSetup() }

        binding.hostText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                finishManualSetup()
                true
            } else false
        }
    }

    private fun finishManualSetup() {
        val text = binding.hostText.textString

        if (!text.isBlank()) {
            viewModel.setUrl(text)
            navigate(ManualSetupFragmentDirections.actionGlobalAuthenticationNavigationGraph())
        }
    }
}
