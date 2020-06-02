package xyz.mcmxciv.halauncher.ui.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.databinding.FragmentLaunchBinding
import xyz.mcmxciv.halauncher.ui.BaseFragment
import xyz.mcmxciv.halauncher.ui.fragmentViewModels

class LaunchFragment : BaseFragment() {
    private lateinit var binding: FragmentLaunchBinding
    private val viewModel by fragmentViewModels { component.launchViewModelProvider().get() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaunchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.navigation) { navigate(it) }
    }
}
