package xyz.mcmxciv.halauncher.ui.onboarding.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.databinding.FragmentManualSetupBinding
import xyz.mcmxciv.halauncher.ui.BaseFragment
import xyz.mcmxciv.halauncher.ui.fragmentViewModels
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe
import xyz.mcmxciv.halauncher.utils.value

class ManualSetupFragment : BaseFragment() {
    private lateinit var binding: FragmentManualSetupBinding
    private val viewModel by fragmentViewModels { component.manualSetupViewModel().get() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManualSetupBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.navigationEvent) { navigate(it) }

        binding.discoveryModeButton.setOnClickListener { viewModel.discoveryModeButtonClicked() }

        binding.openUrlButton.setOnClickListener {
            viewModel.urlSubmitted(binding.hostText.value)
        }

        binding.hostText.setOnEditorActionListener { v, actionId, _ ->
            viewModel.urlSubmitted(v.value, actionId)
        }
    }
}
