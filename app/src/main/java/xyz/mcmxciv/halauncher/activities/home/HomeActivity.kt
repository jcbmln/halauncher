package xyz.mcmxciv.halauncher.activities.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONArray
import org.json.JSONObject
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.databinding.ActivityHomeBinding
import xyz.mcmxciv.halauncher.utils.AppPreferences

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
        initializeWebView()

        binding.homeAppBar.appList.layoutManager = LinearLayoutManager(this)

        binding.homeParentLayout.slidableView = binding.homeSlidableView
        binding.homeParentLayout.revealableView = binding.homeAppBar.appList

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

        viewModel.appList.observe(this, Observer {
            binding.homeAppBar.appList.adapter = AppListAdapter(it)
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun initializeWebView() {
        binding.homeWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()

            addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun getExternalAuth(result: String) {
                    viewModel.getExternalAuth(JSONObject(result).get("callback") as String)
                }

                @JavascriptInterface
                fun revokeExternalAuth(result: String) {
                    viewModel.revokeExternalAuth(JSONObject(result).get("callback") as String)
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

    private fun hideSystemUI() {
        // Enables sticky immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}
