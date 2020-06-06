package xyz.mcmxciv.halauncher.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.BaseFragment
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentHomeBinding
import xyz.mcmxciv.halauncher.fragmentViewModels

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by fragmentViewModels { component.homeViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
}
