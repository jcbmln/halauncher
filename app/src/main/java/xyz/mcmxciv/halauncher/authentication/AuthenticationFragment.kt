package xyz.mcmxciv.halauncher.authentication

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
import xyz.mcmxciv.halauncher.databinding.FragmentAuthenticationBinding
import xyz.mcmxciv.halauncher.ui.BaseFragment
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.ui.fragmentViewModels
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe

class AuthenticationFragment : BaseFragment() {
    private lateinit var binding: FragmentAuthenticationBinding
    private val viewModel by fragmentViewModels {
        component.authenticationViewModelProvider().get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthenticationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    viewModel.webviewError()
                    findNavController().popBackStack()
                }
            }
            loadUrl(viewModel.authenticationUrl)
        }

        observe(viewModel.errorEvent) { displayMessage(it) }
        observe(viewModel.navigationEvent) { navigate(it) }
    }
}
