package xyz.mcmxciv.halauncher.ui.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentIntegrationBinding
import xyz.mcmxciv.halauncher.models.IntegrationState
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.ui.main.MainActivityViewModel
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class IntegrationFragment : LauncherFragment() {
    private lateinit var binding: FragmentIntegrationBinding
    private lateinit var viewModel: IntegrationViewModel
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntegrationBinding.inflate(inflater)
        return binding.root
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

        binding.skipButton.setOnClickListener {
            viewModel.registerDevice()
        }

        binding.skipButton.setOnClickListener {
            finishIntegration()
        }
    }

    private fun finishIntegration() {
        activityViewModel.getConfig()
        navigate(IntegrationFragmentDirections.actionGlobalHomeFragment())
    }

    private fun showButtons() {
        binding.retryButton.isVisible = true
        binding.skipButton.isVisible = true
        binding.progressBar.isVisible = false
    }

    private fun hideButtons() {
        binding.retryButton.isVisible = false
        binding.skipButton.isVisible = false
        binding.progressBar.isVisible = true
    }
}
