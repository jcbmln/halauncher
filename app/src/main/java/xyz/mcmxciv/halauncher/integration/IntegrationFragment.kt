package xyz.mcmxciv.halauncher.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import xyz.mcmxciv.halauncher.BaseFragment
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentIntegrationBinding
import xyz.mcmxciv.halauncher.navigate
import xyz.mcmxciv.halauncher.observe

@AndroidEntryPoint
class IntegrationFragment : BaseFragment() {
    private lateinit var binding: FragmentIntegrationBinding
    private val viewModel: IntegrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntegrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.navigation) { navigate(it) }
        observe(viewModel.error) { displayMessage(it) }
        observe(viewModel.progressVisibility) { binding.registrationProgressBar.isVisible = it }
        observe(viewModel.buttonVisibility) { isVisible ->
            binding.retryButton.isVisible = isVisible
            binding.skipButton.isVisible = isVisible
        }

        binding.retryButton.setOnClickListener { viewModel.retry() }
        binding.skipButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.skip_message)
                .setPositiveButton(R.string.skip) { _, _ -> viewModel.skip() }
                .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
                .show()
        }
    }
}
