package xyz.mcmxciv.halauncher.fragments.setup.discovery

import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.AppModel
import xyz.mcmxciv.halauncher.LauncherApplication

import xyz.mcmxciv.halauncher.ServiceListAdapter
import xyz.mcmxciv.halauncher.databinding.DiscoveryFragmentBinding
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener
import xyz.mcmxciv.halauncher.utils.AppPreferences

class DiscoveryFragment : Fragment(), ServiceSelectedListener {
    private lateinit var viewModel: DiscoveryViewModel
    private lateinit var binding: DiscoveryFragmentBinding
    private lateinit var appModel: AppModel

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
        viewModel.start(appModel.nsdManager)

        binding.serviceList.layoutManager = LinearLayoutManager(context)
        val adapter = ServiceListAdapter(this)
        binding.serviceList.adapter = adapter
        binding.serviceList.addItemDecoration(DividerItemDecoration(
            binding.serviceList.context, DividerItemDecoration.VERTICAL
        ))

        viewModel.startDiscovery()

        viewModel.services.observe(this, Observer {
            updateViewVisibility(it.isEmpty())
            adapter.setData(it)
        })
        viewModel.resolvedUrl.observe(this, Observer {
            val prefs = AppPreferences.getInstance(LauncherApplication.getAppContext())
            prefs.url = it

            val action =
                DiscoveryFragmentDirections.actionGlobalAuthenticationNavigationGraph()
            binding.root.findNavController().navigate(action)
        })

        binding.manualModeButton.setOnClickListener {
            val action = DiscoveryFragmentDirections.actionDiscoveryFragmentToManualSetupFragment()
            it.findNavController().navigate(action)
        }
    }

    override fun onServiceSelected(serviceInfo: NsdServiceInfo) {
        viewModel.stopDiscovery()
        viewModel.resolveService(serviceInfo)
    }

    private fun updateViewVisibility(serviceListIsEmpty: Boolean) {
        binding.serviceListProgress.visibility =
            if (serviceListIsEmpty) View.VISIBLE
            else View.GONE
        binding.serviceList.visibility =
            if (serviceListIsEmpty) View.GONE
            else View.VISIBLE
    }
}
