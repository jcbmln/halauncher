package xyz.mcmxciv.halauncher.activities.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONObject
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.activities.authentication.AuthenticationActivity
import xyz.mcmxciv.halauncher.databinding.ActivityHomeBinding
import xyz.mcmxciv.halauncher.utils.AppPreferences
import java.io.BufferedReader
import java.lang.Exception

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var prefs: AppPreferences
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        prefs = AppPreferences.getInstance(this)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        setContentView(binding.root)

        viewModel.validateSession()
        viewModel.sessionValidated.observe(this, Observer { valid ->
            if (valid) {
                initializeWebView()

                viewModel.externalAuthCallback.observe(this, Observer {
                    binding.homeWebView.evaluateJavascript(
                        "${it.first}(true, ${it.second});",
                        null
                    )
                })

                viewModel.externalAuthRevokeCallback.observe(this, Observer {
                    binding.homeWebView.evaluateJavascript("$it(true);", null)
                    prefs.isAuthenticated = false
                })
            }
            else {
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
        })



//        binding.homeAppBar.appList.layoutManager = LinearLayoutManager(this)

//        viewModel.appList.observe(this, Observer {
//            binding.homeAppBar.appList.adapter = AppListAdapter(it)
//        })
    }

    private fun initializeWebView() {
        WebView.setWebContentsDebuggingEnabled(true)
        binding.homeWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    injectJs()
                    super.onPageFinished(view, url)

                }
            }

            addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun getExternalAuth(result: String) {
                    viewModel.getExternalAuth(JSONObject(result).get("callback") as String)
                }

                @JavascriptInterface
                fun revokeExternalAuth(result: String) {
                    viewModel.revokeExternalAuth(JSONObject(result).get("callback") as String)
                }

                @JavascriptInterface
                fun themesUpdated(result: String) {
                    val name = JSONObject(result).get("name") as String
                    Log.d(TAG, name)
                }

//                @JavascriptInterface
//                fun externalBus(message: String) {
//                    Log.d(TAG, "External bus $message")
//                    binding.homeWebView.post {
//                        when {
//                            JSONObject(message).get("type") == "config/get" -> {
//                                val script = "externalBus(" +
//                                        "${JSONObject(
//                                            mapOf(
//                                                "id" to JSONObject(message).get("id"),
//                                                "type" to "result",
//                                                "success" to true,
//                                                "result" to JSONObject(mapOf("hasSettingsScreen" to true))
//                                            )
//                                        )}" +
//                                        ");"
//                                Log.d(TAG, script)
//                                binding.homeWebView.evaluateJavascript(script) {
//                                    Log.d(TAG, "Callback $it")
//                                }
//                            }
////                            JSONObject(message).get("type") == "config_screen/show" ->
////                                startActivity(SettingsActivity.newInstance(this@WebViewActivity))
//                        }
//                    }
//                }
            }, "externalApp")
        }

        binding.homeWebView.loadUrl(viewModel.buildUrl(prefs.url))
    }

    private fun injectJs() {
        try {
            val input = assets.open("websocketBridge.js")
            input.bufferedReader().use(BufferedReader::readText)
        }
        catch (ex: Exception) {
            null
        }?.let {
            binding.homeWebView.loadUrl("javascript:(function() { $it })()")
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}
