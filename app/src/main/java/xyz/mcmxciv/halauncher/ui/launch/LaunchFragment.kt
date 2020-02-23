package xyz.mcmxciv.halauncher.ui.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.models.SessionState
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class LaunchFragment : LauncherFragment() {
    private lateinit var viewModel: LaunchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = createViewModel { component.launchViewModel() }

        observe(viewModel.sessionState) { state ->
            navigate {
                return@navigate when (state) {
                    SessionState.NEW_USER ->
                        LaunchFragmentDirections.actionLaunchFragmentToSetupNavigationGraph()
                    SessionState.UNAUTHENTICATED ->
                        LaunchFragmentDirections
                            .actionLaunchFragmentToAuthenticationNavigationGraph()
                    SessionState.AUTHENTICATED ->
                        LaunchFragmentDirections.actionLaunchFragmentToHomeFragment()
                }
            }
        }
    }
}
