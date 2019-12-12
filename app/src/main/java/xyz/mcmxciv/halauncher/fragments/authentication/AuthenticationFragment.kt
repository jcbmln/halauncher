package xyz.mcmxciv.halauncher.fragments.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import xyz.mcmxciv.halauncher.databinding.AuthenticationFragmentBinding
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BaseFragment

class AuthenticationFragment : BaseFragment() {
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
        viewModel = createViewModel { component.authenticationViewModel() }

        binding.authenticationWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    return viewModel.authenticate(url)
                }
            }
            loadUrl(viewModel.getAuthenticationUrl())
        }

        viewModel.authenticationErrorMessage.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        viewModel.authenticationSuccess.observe(this, Observer { authenticated ->
            if (authenticated && viewModel.isSetupDone()) {
                val action =
                    AuthenticationFragmentDirections.actionGlobalHomeFragment()
                binding.root.findNavController().navigate(action)
            }
            else if (authenticated) {
                val action =
                    AuthenticationFragmentDirections.actionAuthenticationFragmentToIntegrationFragment()
                binding.root.findNavController().navigate(action)
            }
        })
    }

}
