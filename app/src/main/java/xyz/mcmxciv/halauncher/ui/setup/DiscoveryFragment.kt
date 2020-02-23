package xyz.mcmxciv.halauncher.ui.setup

import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_discovery.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.utils.Resource

class DiscoveryFragment : LauncherFragment(),
    ServiceSelectedListener {
    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.setupViewModel() }

        serviceList.layoutManager = LinearLayoutManager(context)
        val adapter = ServiceAdapter(this)
        serviceList.adapter = adapter
        serviceList.addItemDecoration(DividerItemDecoration(
            serviceList.context, DividerItemDecoration.VERTICAL
        ))

        viewModel.startDiscovery()

        viewModel.servicesData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Error -> displayMessage(resource.message)
                is Resource.Success -> {
                    val services = resource.data
                    updateViewVisibility(services.isEmpty())
                    adapter.setData(services)
                }
            }
        })

        viewModel.resolvedUrl.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Error -> displayMessage(resource.message)
                is Resource.Success -> {
                    viewModel.setUrl(resource.data)
                    navigateToAuthenticationGraph()
                }
            }
        })

        manualModeButton.setOnClickListener {
            navigationToManualSetup()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopDiscovery()
    }

    override fun onServiceSelected(serviceInfo: NsdServiceInfo) {
        viewModel.stopDiscovery()
        viewModel.resolveService(serviceInfo)
    }

    private fun navigateToAuthenticationGraph() {
        val action =
            DiscoveryFragmentDirections.actionGlobalAuthenticationNavigationGraph()
        findNavController().navigate(action)
    }

    private fun navigationToManualSetup() {
        val action =
            DiscoveryFragmentDirections.actionDiscoveryFragmentToManualSetupFragment()
        findNavController().navigate(action)
    }

    private fun updateViewVisibility(serviceListIsEmpty: Boolean) {
        serviceListProgress.isVisible = serviceListIsEmpty
        serviceList.isVisible = !serviceListIsEmpty
    }
}
