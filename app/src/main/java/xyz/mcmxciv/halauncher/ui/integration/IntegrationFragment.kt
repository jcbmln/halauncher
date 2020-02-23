package xyz.mcmxciv.halauncher.ui.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_integration.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.models.IntegrationState
import xyz.mcmxciv.halauncher.ui.*
import xyz.mcmxciv.halauncher.utils.Resource

class IntegrationFragment : LauncherFragment() {
    private lateinit var viewModel: IntegrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_integration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = createViewModel { component.integrationViewModel() }

        observe(viewModel.integrationState) { state ->
            when (state) {
                IntegrationState.LOADING -> { hideButtons() }
                IntegrationState.ERROR -> {
                    displayMessage(getString(R.string.integration_status_failed))
                    showButtons()
                }
                IntegrationState.SUCCESS -> finishIntegration()
            }
        }

        integrationRetryButton.setOnClickListener {
            viewModel.registerDevice()
        }

        integrationSkipButton.setOnClickListener {
            finishIntegration(true)
        }
    }

    private fun finishIntegration(integrationSkipped: Boolean = false) {
        viewModel.finishSetup(integrationSkipped)
        navigate { IntegrationFragmentDirections.actionGlobalHomeFragment() }
    }

    private fun showButtons() {
        integrationRetryButton.isVisible = true
        integrationSkipButton.isVisible = true
        integrationProgressBar.isVisible = false
    }

    private fun hideButtons() {
        integrationRetryButton.isVisible = false
        integrationSkipButton.isVisible = false
        integrationProgressBar.isVisible = true
    }
}
