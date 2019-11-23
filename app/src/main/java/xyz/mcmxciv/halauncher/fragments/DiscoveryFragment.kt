package xyz.mcmxciv.halauncher.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mcmxciv.halauncher.AppModel

import xyz.mcmxciv.halauncher.ServiceListAdapter
import xyz.mcmxciv.halauncher.databinding.DiscoveryFragmentBinding
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener

class DiscoveryFragment : Fragment() {
    private lateinit var viewModel: DiscoveryViewModel
    private lateinit var binding: DiscoveryFragmentBinding
    private lateinit var listener: ServiceSelectedListener
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

        binding.setupServiceList.layoutManager = LinearLayoutManager(context)
        val adapter = ServiceListAdapter(viewModel)
        binding.setupServiceList.adapter = adapter
        binding.setupServiceList.addItemDecoration(DividerItemDecoration(
            binding.setupServiceList.context, DividerItemDecoration.VERTICAL
        ))

        viewModel.startDiscovery()

        viewModel.services.observe(this, Observer {
            updateViewVisibility(it.isEmpty())
            adapter.setData(it)
        })
        viewModel.selectedService.observe(this, Observer {
            viewModel.stopDiscovery()
            viewModel.resolveService()
        })
        viewModel.resolvedUrl.observe(this, Observer {
            listener.onServiceSelected(it)
        })
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
}
