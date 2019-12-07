package xyz.mcmxciv.halauncher.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.AuthenticationFragmentBinding
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.utils.AppPreferences

class AuthenticationFragment : Fragment() {
    private lateinit var binding: AuthenticationFragmentBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthenticationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)

        binding.authenticationWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    return viewModel.authenticate(url)
                }
            }
            loadUrl(AuthenticationRepository.authenticationUrl)
        }

        viewModel.authenticationErrorMessage.observe(this, Observer {
            Toast.makeText(LauncherApplication.getAppContext(), it, Toast.LENGTH_LONG).show()
        })

        viewModel.authenticationSuccess.observe(this, Observer {
            AppPreferences.getInstance(LauncherApplication.getAppContext()).isAuthenticated = it

            if (it) {
                val action = AuthenticationFragmentDirections
                    .actionAuthenticationFragmentToIntegrationFragment()
                binding.root.findNavController().navigate(action)
            }
        })
    }

}
