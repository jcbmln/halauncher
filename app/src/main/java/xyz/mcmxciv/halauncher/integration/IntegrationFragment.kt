package xyz.mcmxciv.halauncher.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.integration_fragment.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BaseFragment
import xyz.mcmxciv.halauncher.utils.Resource

class IntegrationFragment : BaseFragment() {
    private lateinit var viewModel: IntegrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.integration_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.integrationViewModel() }
        viewModel.registerDevice()

        viewModel.integrationState.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Error -> {
                    displayMessage(resource.message)
                    showButtons()
                }
                is Resource.Success -> finishIntegration()
            }
        })

        viewModel.integrationError.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        integrationRetryButton.setOnClickListener {
            viewModel.registerDevice()
            hideButtons()
        }

        integrationSkipButton.setOnClickListener {
            finishIntegration()
        }
    }

    private fun finishIntegration() {
        viewModel.finishSetup()
        val action = IntegrationFragmentDirections.actionGlobalHomeFragment()
        findNavController().navigate(action)
    }

    private fun showButtons() {
        integrationRetryButton.isVisible = true
        integrationSkipButton.isVisible = true
        integrationProgressBar.isVisible = true
    }

    private fun hideButtons() {
        integrationRetryButton.isVisible = false
        integrationSkipButton.isVisible = false
        integrationProgressBar.isVisible = false
    }
}
