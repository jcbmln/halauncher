package xyz.mcmxciv.halauncher.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import xyz.mcmxciv.halauncher.BaseFragment
import xyz.mcmxciv.halauncher.databinding.FragmentDiscoveryBinding
import xyz.mcmxciv.halauncher.fragmentViewModels

class DiscoveryFragment : BaseFragment() {
    private lateinit var binding: FragmentDiscoveryBinding
    @ExperimentalCoroutinesApi
    private val viewModel by fragmentViewModels { component.discoveryViewModel() }
    private val discoveryInstanceAdapter = DiscoveryInstanceAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        discoveryInstanceAdapter.setOnInstanceSelectedListener { instance ->
            viewModel.instanceSelected(instance)
        }

        binding.instanceList.adapter = discoveryInstanceAdapter
        binding.instanceList.layoutManager = LinearLayoutManager(context)
        binding.instanceList.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

        observe(viewModel.navigation) { navigate(it) }
        observe(viewModel.instances) { instances ->
            discoveryInstanceAdapter.setInstances(instances)
            binding.discoveryProgressBar.isVisible = instances.isEmpty()
            binding.instanceList.isVisible = instances.isNotEmpty()
        }
    }
}
