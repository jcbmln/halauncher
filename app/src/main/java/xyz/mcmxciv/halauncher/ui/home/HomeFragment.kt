package xyz.mcmxciv.halauncher.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_webview_preference.*
import org.json.JSONObject
import timber.log.Timber
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.models.ErrorState
import xyz.mcmxciv.halauncher.models.InvariantDeviceProfile
import xyz.mcmxciv.halauncher.ui.*
import xyz.mcmxciv.halauncher.models.WebCallback
import java.io.BufferedReader
import javax.inject.Inject


class HomeFragment : LauncherFragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var appListAdapter: AppListAdapter

    @Inject
    lateinit var invariantDeviceProfile: InvariantDeviceProfile

    private var color: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)
        viewModel = createViewModel { component.homeViewModel() }
        color = activity?.getColor(R.color.colorAccent)

        appListAdapter = AppListAdapter(context!!, listOf())
        appList.layoutManager = GridLayoutManager(context, invariantDeviceProfile.numColumns)
        appList.adapter = appListAdapter

        observe(viewModel.error) { error ->
            if (error == ErrorState.AUTHENTICATION) {
                displayMessage(getString(R.string.error_no_session_message))
                navigate {
                    HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph()
                }
            }
            else {
                displayMessage(getString(R.string.error_webview_message))
            }
        }

        observe(viewModel.activityList) { resource ->
            appListAdapter.update(resource)
        }
        
        observe(viewModel.callback) { resource ->
            homeWebView.evaluateJavascript(resource.callback, null)
            viewModel.getConfig()

            if (resource is WebCallback.RevokeAuthCallback) {
                navigate {
                    HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph()
                }
            }
        }

        observe(viewModel.configEvent) { config ->
            setStatusBarColor(Color.parseColor(config.themeColor))
        }

        allAppsButton.setOnClickListener {
            setAppListVisibility()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            this.isEnabled = true

            when {
                appList.isVisible -> appList.isVisible = false
                webview.canGoBack() -> webview.goBack()
            }
        }

        activity?.window?.setBackgroundDrawable(ColorDrawable(getColor(context!!, R.color.colorAccent)))

        initializeWebView()
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
                            "config_screen/show" -> navigate {
                                HomeFragmentDirections.actionHomeFragmentToMainPreferencesFragment()
                            }
                            "frontend/get_themes" -> {
                                val keys: MutableList<String> = ArrayList()
                                val themes = JSONObject(message).get("themes")
                                val themesWrapper = JSONObject(themes.toString())

                                for (key in themesWrapper.keys()) {
                                    keys.add(key as String)
                                }
                            }
                        }
                    }
                }
            }, "externalApp")
        }

        homeWebView.loadUrl(viewModel.webviewUrl)
    }

    private fun getThemeCallback() : String? {
        val callback = try {
            val input = LauncherApplication.instance.assets.open("var handleThemeUpdate=function(a){a=a.data||a;var b=a.default_theme;window.externalApp.externalBus(JSON.stringify({type:\"frontend/get_themes\",themes:a.themes}));\"default\"===b?window.externalApp.themesUpdated(JSON.stringify({name:b})):window.externalApp.themesUpdated(JSON.stringify({name:b,styles:a.themes[b]}))};window.hassConnection.then(function(a){a=a.conn;a.sendMessagePromise({type:\"frontend/get_themes\"}).then(handleThemeUpdate);a.subscribeEvents(handleThemeUpdate,\"themes_updated\")});\n")
            input.bufferedReader().use(BufferedReader::readText)
        }
        catch (ex: Exception) {
            Timber.e(ex)
            null
        }

        return callback?.let { "javascript:(function() { $it })()" }
    }

    private fun setAppListVisibility() {
        appList.isVisible = !appList.isVisible
    }

    private fun setStatusBarColor(color: Int) {
        activity?.let {
            it.window.statusBarColor = color
            it.window.navigationBarColor = color
        }

        val drawable = ColorDrawable(color)
        drawable.alpha = 240
        appList.background = drawable

        appListAdapter.setThemeColor(color)
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
