package xyz.mcmxciv.halauncher.ui.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import xyz.mcmxciv.halauncher.databinding.FragmentLaunchBinding
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class LaunchFragment : LauncherFragment() {
    private lateinit var binding: FragmentLaunchBinding
    private lateinit var viewModel: LaunchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaunchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = createViewModel { component.launchViewModelProvider().get() }
        observe(viewModel.navigationEvent) { navigate(it) }
    }
}
