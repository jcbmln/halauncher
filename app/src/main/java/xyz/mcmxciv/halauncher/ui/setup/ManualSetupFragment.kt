package xyz.mcmxciv.halauncher.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.manual_setup_fragment.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BaseFragment

class ManualSetupFragment : BaseFragment() {
    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.manual_setup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.setupViewModel() }

        discoveryModeButton.setOnClickListener {
            navigateToDiscoveryFragment()
        }

        setupManualButton.setOnClickListener {
            val text = setupHostText.text.toString()

            if (!text.isBlank()) {
                viewModel.setUrl(text)
                navigateToAuthenticationGraph()
            }
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
