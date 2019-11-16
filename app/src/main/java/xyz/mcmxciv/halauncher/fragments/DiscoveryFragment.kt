package xyz.mcmxciv.halauncher.fragments

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.HomeAssistantDiscoveryListener
import xyz.mcmxciv.halauncher.HomeAssistantResolveListener

import xyz.mcmxciv.halauncher.ServiceListAdapter
import xyz.mcmxciv.halauncher.interfaces.DiscoveryServiceSelectedListener
import xyz.mcmxciv.halauncher.databinding.DiscoveryFragmentBinding

class DiscoveryFragment(private val listener: DiscoveryServiceSelectedListener) : Fragment() {
    private lateinit var viewModel: DiscoveryViewModel
    private lateinit var binding: DiscoveryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DiscoveryFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DiscoveryViewModel::class.java)


        binding.setupServiceList.layoutManager = LinearLayoutManager(context)
        val adapter = ServiceListAdapter(viewModel)
        binding.setupServiceList.adapter = adapter

        val nsdManager = context?.let { getSystemService(it, NsdManager::class.java) } as NsdManager
        nsdManager.discoverServices(
            HomeAssistantDiscoveryListener.SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            HomeAssistantDiscoveryListener(nsdManager, this)
        )

        viewModel.services.observe(this, Observer {
            updateViewVisibility(it.isEmpty())
            adapter.setData(it)
        })
        viewModel.selectedService.observe(this, Observer {
            nsdManager.resolveService(it, HomeAssistantResolveListener(this))
        })
        viewModel.resolvedUrl.observe(this, Observer {
            listener.onServiceSelected(it)
        })
    }

    private fun updateViewVisibility(serviceListIsEmpty: Boolean) {
        binding.setupLoadingLayout.visibility =
            if (serviceListIsEmpty) View.VISIBLE
            else View.GONE
        binding.setupSelectionLayout.visibility =
            if (serviceListIsEmpty) View.GONE
            else View.VISIBLE
    }
}
