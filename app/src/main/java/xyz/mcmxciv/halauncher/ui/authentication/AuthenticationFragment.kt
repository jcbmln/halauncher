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
import kotlinx.android.synthetic.main.fragment_authentication.*
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.*

class AuthenticationFragment : LauncherFragment() {
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.authenticationViewModel() }

        authenticationWebView.apply {
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
