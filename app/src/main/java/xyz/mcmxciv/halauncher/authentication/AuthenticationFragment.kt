package xyz.mcmxciv.halauncher.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.BaseFragment
import kotlinx.android.synthetic.main.authentication_fragment.*

class AuthenticationFragment : BaseFragment() {
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.authentication_fragment, container, false)
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
            }
            loadUrl(viewModel.getAuthenticationUrl())
        }

        viewModel.authenticationErrorMessage.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        viewModel.authenticationSuccess.observe(this, Observer { authenticated ->
            if (authenticated) {
                val action = if (viewModel.isSetupDone())
                        AuthenticationFragmentDirections.actionGlobalHomeFragment()
                    else
                        AuthenticationFragmentDirections
                            .actionAuthenticationFragmentToIntegrationFragment()

                findNavController().navigate(action)
            }
        })
    }

}
