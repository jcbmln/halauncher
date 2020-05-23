package xyz.mcmxciv.halauncher.ui.onboarding.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.databinding.FragmentDiscoveryBinding
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.fragmentViewModels
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class DiscoveryFragment : LauncherFragment() {
    private lateinit var binding: FragmentDiscoveryBinding
    private val viewModel by fragmentViewModels { component.discoveryViewModelProvider().get() }
    private val serviceAdapter = ServiceAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoveryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceAdapter.setOnServiceSelectedListener { url -> viewModel.serviceSelected(url) }

        binding.serviceList.layoutManager = LinearLayoutManager(context)
        binding.serviceList.adapter = serviceAdapter
        binding.serviceList.addItemDecoration(DividerItemDecoration(
            binding.serviceList.context, DividerItemDecoration.VERTICAL
        ))

        observe(viewModel.navigationEvent) { navigate(it) }
        observe(viewModel.services) { services -> serviceAdapter.data = services }
        observe(viewModel.showProgress) { binding.serviceListProgressBar.isVisible = it }
        observe(viewModel.showServices) { binding.serviceList.isVisible = it }
        observe(viewModel.headerText) { binding.discoveryText.text = it }

        binding.manualModeButton.setOnClickListener { viewModel.manualSetupModeButtonClicked() }
    }
}
