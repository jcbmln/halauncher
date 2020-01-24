package xyz.mcmxciv.halauncher.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.home_fragment.*
import org.json.JSONObject
import timber.log.Timber
import xyz.mcmxciv.halauncher.AppListAdapter
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.extensions.createViewModel
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.utils.BaseFragment
import xyz.mcmxciv.halauncher.utils.Resource
import xyz.mcmxciv.halauncher.utils.SessionState
import java.io.BufferedReader
import javax.inject.Inject


class HomeFragment : BaseFragment() {
    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var invariantDeviceProfile: InvariantDeviceProfile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        component.inject(this)
        viewModel = createViewModel { component.homeViewModel() }

        viewModel.sessionState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is Resource.Error -> displayMessage(state.message)
                is Resource.Success -> {
                    when (state.data) {
                        SessionState.NewUser -> navigateToSetupGraph()
                        SessionState.Invalid -> navigateToAuthenticationGraph()
                        SessionState.Valid -> {
                            initializeWebView()
                        }
                    }
                }
            }
        })

        appList.layoutManager = GridLayoutManager(context, invariantDeviceProfile.numColumns)

        viewModel.launchableActivities.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is Resource.Error -> displayMessage(state.message)
                is Resource.Success -> appList.adapter = AppListAdapter(state.data)
            }
        })

        viewModel.webCallback.observe(viewLifecycleOwner, Observer { state ->
            if (state != Resource.Loading) {
                homeWebView.evaluateJavascript(state.data?.callback, null)
            }

            when (state) {
                is Resource.Error -> {
                    displayMessage(state.message)
                    navigateToAuthenticationGraph()
                }
            }
        })

        allAppsButton.setOnClickListener {
            setAppListVisibility()
            changeStatusBar()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            this.isEnabled = true
            appList.isVisible = false
        }
    }

    private fun initializeWebView() {
        WebView.setWebContentsDebuggingEnabled(true)
        homeWebView.apply {
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
                    val name = JSONObject(result).get("name") as String
                    Timber.d(name)
                }

                @JavascriptInterface
                fun externalBus(message: String) {
                    Timber.d("External bus $message")
                    homeWebView.post {
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
                                homeWebView.evaluateJavascript(script, null)
                            }
                            "config_screen/show" -> navigateToSettingsActivity()
                            "frontend/get_themes" -> {
                                val keys: MutableList<String> = ArrayList()
                                for (key in JSONObject(JSONObject(message).get("themes").toString()).keys()) {
                                    keys.add(key as String)
                                }
                                Timber.d("Got themes!")
                            }
                        }
                    }
                }
            }, "externalApp")
        }

        homeWebView.loadUrl(viewModel.buildUrl())
    }

    private fun getThemeCallback() : String? {
        val callback = try {
            val input = LauncherApplication.instance.assets.open("websocketBridge.js")
            input.bufferedReader().use(BufferedReader::readText)
        }
        catch (ex: Exception) {
            Timber.e(ex)
            null
        }

        return callback?.let { "javascript:(function() { $it })()" }
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

    private fun setAppListVisibility() {
        appList.isVisible = !appList.isVisible
    }

    private fun changeStatusBar() {
        activity?.let {
            if (appList.isVisible) {
                it.window.statusBarColor = it.getColor(R.color.colorWindowBackground)
                it.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            else {
                it.window.statusBarColor = it.getColor(R.color.colorAccent)
                it.window.decorView.systemUiVisibility =
                    it.window.decorView.systemUiVisibility and
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}
