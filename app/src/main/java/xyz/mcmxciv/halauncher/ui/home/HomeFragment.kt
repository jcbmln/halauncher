package xyz.mcmxciv.halauncher.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.json.JSONObject
import timber.log.Timber
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentHomeBinding
import xyz.mcmxciv.halauncher.models.DeviceProfile
import xyz.mcmxciv.halauncher.models.ErrorState
import xyz.mcmxciv.halauncher.models.WebCallback
import xyz.mcmxciv.halauncher.ui.HassTheme
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.ui.main.MainActivityViewModel
import xyz.mcmxciv.halauncher.ui.main.applist.AppListAdapter
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe
import xyz.mcmxciv.halauncher.utils.AppLauncher
import java.io.BufferedReader
import javax.inject.Inject

class HomeFragment : LauncherFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var appListAdapter: AppListAdapter

    @Inject
    lateinit var deviceProfile: DeviceProfile

    @Inject
    lateinit var appLauncher: AppLauncher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)
        viewModel = createViewModel { component.homeViewModel() }

        observe(viewModel.error) { error ->
            if (error == ErrorState.AUTHENTICATION) {
                displayMessage(getString(R.string.error_no_session_message))
                navigate(
                    HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph()
                )
            } else {
                displayMessage(getString(R.string.error_webview_message))
            }
        }

        appListAdapter = AppListAdapter(deviceProfile, appLauncher, viewModel)

        observe(viewModel.appListItems) { items ->
            appListAdapter.appListItems = items
        }

        observe(viewModel.theme) { theme ->
            binding.appDrawerBackground.background = theme.appListBackground
            requireActivity().window.statusBarColor = theme.primaryColor
            requireActivity().window.navigationBarColor = theme.primaryColor
        }

        observe(viewModel.callback) { resource ->
            binding.homeWebView.evaluateJavascript(resource.callback, null)

            if (resource is WebCallback.RevokeAuthCallback) {
                navigate(HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph())
            }
        }

        binding.appList.layoutManager =
            GridLayoutManager(context, deviceProfile.appDrawerColumns)
        binding.appList.adapter = appListAdapter

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            this.isEnabled = true

            when {
                binding.homeWebView.canGoBack() -> binding.homeWebView.goBack()
            }
        }

        initializeWebView()
    }

    private fun initializeWebView() {
        WebView.setWebContentsDebuggingEnabled(true)
        binding.homeWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.loadUrl(getThemeCallback())
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
                    viewModel.setTheme(result)
                }

                @JavascriptInterface
                fun externalBus(message: String) {
                    binding.homeWebView.post {
                        when (JSONObject(message).get("type")) {
                            "config/get" -> {
                                val script = "externalBus(${JSONObject(
                                    mapOf(
                                        "id" to JSONObject(message).get("id"),
                                        "type" to "result",
                                        "success" to true,
                                        "result" to JSONObject(mapOf("hasSettingsScreen" to true))
                                    )
                                )});"
                                binding.homeWebView.evaluateJavascript(script, null)
                            }
                            "config_screen/show" -> navigate(
                                HomeFragmentDirections.actionHomeFragmentToMainPreferencesFragment()
                            )
                        }
                    }
                }
            }, "externalApp")
        }

        binding.homeWebView.loadUrl(viewModel.webviewUrl)
    }

    private fun getThemeCallback(): String? {
        val callback = try {
            val input = LauncherApplication.instance.assets.open("websocketBridge.js")
            input.bufferedReader().use(BufferedReader::readText)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }

        return callback?.let { "javascript:(function() { $it })()" }
    }
}
