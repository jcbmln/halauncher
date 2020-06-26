package xyz.mcmxciv.halauncher.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import xyz.mcmxciv.halauncher.BaseFragment
import xyz.mcmxciv.halauncher.databinding.FragmentLaunchBinding

@AndroidEntryPoint
class LaunchFragment : BaseFragment() {
    private lateinit var binding: FragmentLaunchBinding
    private val viewModel: LaunchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaunchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.navigation) { navigate(it) }
    }
}
