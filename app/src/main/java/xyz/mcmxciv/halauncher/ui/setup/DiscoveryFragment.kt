package xyz.mcmxciv.halauncher.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentDiscoveryBinding
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class DiscoveryFragment : LauncherFragment(), ServiceSelectedListener {
    private lateinit var binding: FragmentDiscoveryBinding
    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoveryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = createViewModel { component.setupViewModel() }

        binding.serviceList.layoutManager = LinearLayoutManager(context)
        val adapter = ServiceAdapter(this)
        binding.serviceList.adapter = adapter
        binding.serviceList.addItemDecoration(DividerItemDecoration(
            binding.serviceList.context, DividerItemDecoration.VERTICAL
        ))

        viewModel.startDiscovery()

        observe(viewModel.servicesData) { services ->
            updateViewVisibility(services.isEmpty())
            adapter.setData(services)
        }

        binding.manualModeButton.setOnClickListener {
            navigate(DiscoveryFragmentDirections.actionDiscoveryFragmentToManualSetupFragment())
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.stopDiscovery()
    }

    override fun onServiceSelected(url: String) {
        viewModel.stopDiscovery()
        viewModel.setUrl(url)
        navigate(DiscoveryFragmentDirections.actionGlobalAuthenticationNavigationGraph())
    }

    private fun updateViewVisibility(serviceListIsEmpty: Boolean) {
        binding.serviceListProgressBar.isVisible = serviceListIsEmpty
        binding.serviceList.isVisible = !serviceListIsEmpty

        binding.discoveryText.text =
            if (serviceListIsEmpty) getString(R.string.setup_discovery_text)
            else getString(R.string.setup_selection_text)
    }
}
