package xyz.mcmxciv.halauncher.activities.authentication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import xyz.mcmxciv.halauncher.databinding.ActivityAuthenticationBinding
import xyz.mcmxciv.halauncher.repositories.AuthenticationRepository
import xyz.mcmxciv.halauncher.utils.AppPreferences

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
        setContentView(binding.root)

        binding.authenticationWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    return viewModel.authenticate(url)
                }
            }
        }

        binding.authenticationWebView.loadUrl(AuthenticationRepository.authenticationUrl)

        viewModel.authenticationError.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

//        viewModel.authenticationSuccess.observe(this, Observer {
//            AppPreferences.getInstance(this).isAuthenticated = it
//
//            if (it) {
//                startActivity(Intent(this, IntegrationActivity::class.java))
//                finish()
//            }
//        })
    }
}
