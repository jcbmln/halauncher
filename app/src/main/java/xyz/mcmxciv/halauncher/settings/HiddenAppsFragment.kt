package xyz.mcmxciv.halauncher.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.mcmxciv.halauncher.*
import xyz.mcmxciv.halauncher.apps.HideShowAppsAdapter
import xyz.mcmxciv.halauncher.databinding.FragmentHiddenAppsBinding
import javax.inject.Inject

@AndroidEntryPoint
class HiddenAppsFragment : BaseFragment() {
    private lateinit var binding: FragmentHiddenAppsBinding
    private val activityViewModel: HalauncherViewModel by activityViewModels()

    @Inject
    lateinit var hideShowAppsAdapter: HideShowAppsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHiddenAppsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(activityViewModel.allApps) { hideShowAppsAdapter.submitList(it) }

        binding.toolbar.title = getString(R.string.hidden_apps_title)
        requireHalauncherActivity().apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        hideShowAppsAdapter.setOnAppVisibilityToggledListener {
            activityViewModel.onToggleAppVisibility(it)
        }
        binding.appListView.layoutManager = LinearLayoutManager(context)
        binding.appListView.adapter = hideShowAppsAdapter
    }
}
