package xyz.mcmxciv.halauncher.ui.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentAuthenticationBinding
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class AuthenticationFragment : LauncherFragment() {
    private lateinit var binding: FragmentAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthenticationBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.authenticationViewModelProvider().get() }

        binding.authenticationWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    return viewModel.authenticate(url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    returnToSetup(getString(R.string.error_connection_failed_message))
                }
            }
            loadUrl(viewModel.authenticationUrl)
        }

        observe(viewModel.authenticationState) {
            when (it) {
                AuthenticationState.LOADING -> {}
                AuthenticationState.AUTHENTICATED -> {
                    navigate {
                        return@navigate if (viewModel.isSetupDone)
                            AuthenticationFragmentDirections.actionGlobalHomeFragment()
                        else
                            AuthenticationFragmentDirections
                                .actionAuthenticationFragmentToIntegrationFragment()
                    }
                }
                AuthenticationState.ERROR -> {
                    returnToSetup(getString(R.string.error_authentication_failed_message))
                }
            }
        }
    }

    private fun returnToSetup(message: String) {
        displayMessage(message)
        findNavController().popBackStack()
    }
}
