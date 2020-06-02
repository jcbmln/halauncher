package xyz.mcmxciv.halauncher.ui.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.databinding.FragmentManualSetupBinding
import xyz.mcmxciv.halauncher.ui.BaseFragment
import xyz.mcmxciv.halauncher.ui.fragmentViewModels
import xyz.mcmxciv.halauncher.utils.value

class ManualSetupFragment : BaseFragment() {
    private lateinit var binding: FragmentManualSetupBinding
    private val viewModel by fragmentViewModels { component.manualSetupViewModelProvider().get() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManualSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.navigation) { navigate(it) }

        binding.discoveryModeButton.setOnClickListener { viewModel.onDiscoveryModeButtonClicked() }
        binding.openUrlButton.setOnClickListener {
            viewModel.onUrlSubmitted(binding.hostText.value)
        }
        binding.hostText.setOnEditorActionListener { v, actionId, _ ->
            viewModel.onEditorAction(actionId, v.value)
        }
    }
}
