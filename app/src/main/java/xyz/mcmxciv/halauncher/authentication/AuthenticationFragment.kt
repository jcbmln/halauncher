package xyz.mcmxciv.halauncher.authentication

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import xyz.mcmxciv.halauncher.BaseFragment
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentAuthenticationBinding

@AndroidEntryPoint
class AuthenticationFragment : BaseFragment() {
    private lateinit var binding: FragmentAuthenticationBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.authenticationUrl) { binding.authenticationWebView.loadUrl(it) }
        observe(viewModel.error) { id ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.unable_to_connect)
                .setMessage(id)
                .setPositiveButton(R.string.retry) { _, _ ->
                    binding.authenticationWebView.reload()
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    findNavController().popBackStack()
                }
        }

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
                    viewModel.showWebError()
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    super.onReceivedSslError(view, handler, error)
                    viewModel.showSslError()
                }
            }
        }
    }
}
