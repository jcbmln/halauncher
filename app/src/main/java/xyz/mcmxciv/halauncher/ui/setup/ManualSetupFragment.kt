package xyz.mcmxciv.halauncher.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_manual_setup.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel

class ManualSetupFragment : LauncherFragment() {
    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manual_setup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.setupViewModel() }

        discoveryModeButton.setOnClickListener {
            navigateToDiscoveryFragment()
        }

        setupManualButton.setOnClickListener { finishManualSetup() }

        setupHostText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_GO) {
                finishManualSetup()
                true
            } else false
        }
    }

    private fun finishManualSetup() {
        val text = setupHostText.text.toString()

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
