package xyz.mcmxciv.halauncher.fragments.setup.discovery

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
import kotlinx.android.synthetic.main.discovery_fragment.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ServiceListAdapter
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener
import xyz.mcmxciv.halauncher.utils.BaseFragment

class DiscoveryFragment : BaseFragment(), ServiceSelectedListener {
    private lateinit var viewModel: DiscoveryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.discovery_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.discoveryViewModel() }

        serviceList.layoutManager = LinearLayoutManager(context)
        val adapter = ServiceListAdapter(this)
        serviceList.adapter = adapter
        serviceList.addItemDecoration(DividerItemDecoration(
            serviceList.context, DividerItemDecoration.VERTICAL
        ))

        viewModel.startDiscovery()

        viewModel.services.observe(viewLifecycleOwner, Observer {
            updateViewVisibility(it.isEmpty())
            adapter.setData(it)
        })
        viewModel.resolvedUrl.observe(viewLifecycleOwner, Observer {
            viewModel.setUrl(it)

            val action =
                DiscoveryFragmentDirections.actionGlobalAuthenticationNavigationGraph()
            findNavController().navigate(action)
        })

        manualModeButton.setOnClickListener {
            viewModel.clearServices()
            val action = DiscoveryFragmentDirections.actionDiscoveryFragmentToManualSetupFragment()
            findNavController().navigate(action)
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

    private fun updateViewVisibility(serviceListIsEmpty: Boolean) {
        serviceListProgress.isVisible = serviceListIsEmpty
        serviceList.isVisible = !serviceListIsEmpty
    }
}
