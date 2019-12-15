package xyz.mcmxciv.halauncher.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.json.JSONObject
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.databinding.HomeFragmentBinding
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.utils.AuthorizationException
import xyz.mcmxciv.halauncher.utils.BaseFragment
import java.io.BufferedReader

class HomeFragment : BaseFragment() {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = createViewModel { component.homeViewModel() }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            if (binding.appList.visibility == View.VISIBLE)
                binding.appList.visibility = View.INVISIBLE
        }

        if (viewModel.isSetupDone()) {
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
                        navigateToAuthenticationGraph()
                    })
                }
                else {
                    navigateToAuthenticationGraph()
                }
            })

            binding.appList.layoutManager = GridLayoutManager(context, 5)

            viewModel.appList.observe(this, Observer {
                binding.appList.adapter = AppListAdapter(it)
            })

            binding.allAppsButton.setOnClickListener {
                when (binding.appList.visibility) {
                    View.VISIBLE -> binding.appList.visibility = View.INVISIBLE
                    View.INVISIBLE -> binding.appList.visibility = View.VISIBLE
                }
            }
        }
        else {
            navigateToSetupGraph()
        }
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
                    try {
                        viewModel.getExternalAuth(JSONObject(result).get("callback") as String)
                    }
                    catch (exception: AuthorizationException) {
                        Toast.makeText(
                            context, "Failed to authenticate user.", Toast.LENGTH_LONG
                        ).show()
                        navigateToAuthenticationGraph()
                    }
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

                @JavascriptInterface
                fun externalBus(message: String) {
                    Log.d(TAG, "External bus $message")
                    binding.homeWebView.post {
                        when {
                            JSONObject(message).get("type") == "config/get" -> {
                                val script = "externalBus(" +
                                        "${JSONObject(
                                            mapOf(
                                                "id" to JSONObject(message).get("id"),
                                                "type" to "result",
                                                "success" to true,
                                                "result" to JSONObject(mapOf("hasSettingsScreen" to true))
                                            )
                                        )}" +
                                        ");"
                                Log.d(TAG, script)
                                binding.homeWebView.evaluateJavascript(script) {
                                    Log.d(TAG, "Callback $it")
                                }
                            }
                            JSONObject(message).get("type") == "config_screen/show" ->
                                navigateToSettingsActivity()
                        }
                    }
                }
            }, "externalApp")
        }

        binding.homeWebView.loadUrl(viewModel.buildUrl())
    }

    private fun injectJs() {
        try {
            val input = LauncherApplication.instance.assets.open("websocketBridge.js")
            input.bufferedReader().use(BufferedReader::readText)
        }
        catch (ex: Exception) {
            null
        }?.let {
            binding.homeWebView.loadUrl("javascript:(function() { $it })()")
        }
    }

    private fun navigateToSetupGraph() {
        val action = HomeFragmentDirections.actionHomeFragmentToSetupNavigationGraph()
        findNavController().navigate(action)
    }

    private fun navigateToAuthenticationGraph() {
        val action = HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph()
        findNavController().navigate(action)
    }

    private fun navigateToSettingsActivity() {
        val action = HomeFragmentDirections.actionHomeFragmentToSettingsNavigationGraph()
        findNavController().navigate(action)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
