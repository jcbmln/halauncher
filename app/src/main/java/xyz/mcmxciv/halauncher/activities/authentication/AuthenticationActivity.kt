package xyz.mcmxciv.halauncher.activities.authentication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.databinding.ActivityAuthenticationBinding
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
        setContentView(binding.root)

        binding.authenticationWebView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    return viewModel.shouldRedirect(url)
                }
            }
        }

        binding.authenticationWebView.loadUrl(AuthenticationRepository.authenticationUrl)

        viewModel.authenticationError.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        viewModel.authenticationSuccess.observe(this, Observer {

        })
    }
}
