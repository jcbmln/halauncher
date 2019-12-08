package xyz.mcmxciv.halauncher.fragments.integration

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import xyz.mcmxciv.halauncher.LauncherApplication

import xyz.mcmxciv.halauncher.databinding.IntegrationFragmentBinding
import xyz.mcmxciv.halauncher.utils.AppPreferences
import java.lang.Exception

class IntegrationFragment : Fragment() {
    private lateinit var binding: IntegrationFragmentBinding
    private lateinit var viewModel: IntegrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IntegrationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(IntegrationViewModel::class.java)

        viewModel.registerDevice()

        viewModel.integrationState.observe(this, Observer {
            when (it) {
                IntegrationViewModel.IntegrationState.SUCCESS -> finishIntegration()
                IntegrationViewModel.IntegrationState.FAILED -> showButtons()
                else -> throw Exception("Unexpected integration state.")
            }
        })

        viewModel.integrationError.observe(this, Observer {
            Toast.makeText(LauncherApplication.getAppContext(), it, Toast.LENGTH_LONG).show()
        })

        binding.integrationRetryButton.setOnClickListener {
            viewModel.registerDevice()
            hideButtons()
        }

        binding.integrationSkipButton.setOnClickListener {
            finishIntegration()
        }
    }

    private fun finishIntegration() {
        AppPreferences.getInstance(LauncherApplication.getAppContext()).setupDone = true
        val action = IntegrationFragmentDirections.actionGlobalHomeFragment()
        binding.root.findNavController().navigate(action)
    }

    private fun showButtons() {
        binding.integrationRetryButton.visibility = View.VISIBLE
        binding.integrationSkipButton.visibility = View.VISIBLE
        binding.integrationProgressBar.visibility = View.INVISIBLE
    }

    private fun hideButtons() {
        binding.integrationRetryButton.visibility = View.INVISIBLE
        binding.integrationSkipButton.visibility = View.INVISIBLE
        binding.integrationProgressBar.visibility = View.VISIBLE
    }
}
