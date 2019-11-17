package xyz.mcmxciv.halauncher.fragments

import android.net.nsd.NsdManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.AppModel
import xyz.mcmxciv.halauncher.HomeAssistantDiscoveryListener
import xyz.mcmxciv.halauncher.HomeAssistantResolveListener

import xyz.mcmxciv.halauncher.ServiceListAdapter
import xyz.mcmxciv.halauncher.databinding.DiscoveryFragmentBinding

class DiscoveryFragment : Fragment() {
    private lateinit var viewModel: DiscoveryViewModel
    private lateinit var binding: DiscoveryFragmentBinding
    private lateinit var listener: ServiceSelectedListener
    private lateinit var appModel: AppModel
    private lateinit var nsdManager: NsdManager
    private lateinit var discoveryListener: HomeAssistantDiscoveryListener

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
        appModel = AppModel.getInstance(context!!)
        nsdManager = appModel.nsdManager
        discoveryListener = HomeAssistantDiscoveryListener(nsdManager, this)

        binding.setupServiceList.layoutManager = LinearLayoutManager(context)
        val adapter = ServiceListAdapter(viewModel)
        binding.setupServiceList.adapter = adapter

        nsdManager.discoverServices(
            HomeAssistantDiscoveryListener.SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
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

    override fun onPause() {
        super.onPause()
        nsdManager.stopServiceDiscovery(discoveryListener)
    }

    fun setServiceSelectedListener(callback: ServiceSelectedListener) {
        listener = callback
    }

    private fun updateViewVisibility(serviceListIsEmpty: Boolean) {
        binding.setupLoadingLayout.visibility =
            if (serviceListIsEmpty) View.VISIBLE
            else View.GONE
        binding.setupSelectionLayout.visibility =
            if (serviceListIsEmpty) View.GONE
            else View.VISIBLE
    }

    interface ServiceSelectedListener {
        fun onServiceSelected(serviceUrl: String)
    }
}
