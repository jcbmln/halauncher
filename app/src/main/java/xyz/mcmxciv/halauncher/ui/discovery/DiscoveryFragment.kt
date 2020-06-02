package xyz.mcmxciv.halauncher.ui.discovery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentDiscoveryBinding
import xyz.mcmxciv.halauncher.discovery.DiscoveryInstanceAdapter
import xyz.mcmxciv.halauncher.ui.BaseFragment
import xyz.mcmxciv.halauncher.ui.fragmentViewModels

class DiscoveryFragment : BaseFragment() {
    private lateinit var binding: FragmentDiscoveryBinding
    @ExperimentalCoroutinesApi
    private val viewModel by fragmentViewModels { component.discoveryViewModelProvider().get() }
    private val discoveryInstanceAdapter = DiscoveryInstanceAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoveryBinding.inflate(inflater)
        return binding.root
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        discoveryInstanceAdapter.setOnInstanceSelectedListener { instance ->
            viewModel.onInstanceSelected(instance)
        }

        binding.instanceList.adapter = discoveryInstanceAdapter
        binding.instanceList.layoutManager = LinearLayoutManager(context)
        binding.instanceList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

        observe(viewModel.instances) { instances ->
            discoveryInstanceAdapter.setInstances(instances)
            binding.discoveryProgressBar.isVisible = instances.isEmpty()
            binding.instanceList.isVisible = instances.isNotEmpty()
        }
    }
}
