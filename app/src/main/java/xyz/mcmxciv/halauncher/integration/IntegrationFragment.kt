package xyz.mcmxciv.halauncher.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import xyz.mcmxciv.halauncher.databinding.FragmentIntegrationBinding
import xyz.mcmxciv.halauncher.ui.BaseFragment
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.ui.fragmentViewModels
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class IntegrationFragment : BaseFragment() {
    private lateinit var binding: FragmentIntegrationBinding
    private val viewModel by fragmentViewModels { component.integrationViewModelProvider().get() }

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

        observe(viewModel.navigationEvent) { navigate(it) }
        observe(viewModel.errorEvent) { displayMessage(it) }
        observe(viewModel.buttonVisibility) { isVisible ->
            binding.retryButton.isVisible = isVisible
            binding.skipButton.isVisible = isVisible
        }
        observe(viewModel.progressVisibility) { binding.progressBar.isVisible = it }

        binding.retryButton.setOnClickListener {
            viewModel.registerDevice()
        }

        binding.skipButton.setOnClickListener {
            viewModel.skipIntegration()
        }
    }
}
