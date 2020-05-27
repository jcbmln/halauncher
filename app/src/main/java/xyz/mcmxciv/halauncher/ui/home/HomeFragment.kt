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
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import org.json.JSONObject
import timber.log.Timber
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.background.PackageReceiver
import xyz.mcmxciv.halauncher.databinding.FragmentHomeBinding
import xyz.mcmxciv.halauncher.ui.BaseFragment
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.ui.fragmentViewModels
import xyz.mcmxciv.halauncher.ui.home.appdrawer.AppDrawerAdapter
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe
import java.io.BufferedReader
import javax.inject.Inject

class HomeFragment : BaseFragment(), PackageReceiver.PackageListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var packageReceiver: PackageReceiver
    private val viewModel by fragmentViewModels { component.homeViewModelProvider().get() }

    @Inject
    lateinit var appDrawerAdapter: AppDrawerAdapter

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

        observe(viewModel.navigationEvent) { action -> navigate(action) }
        observe(viewModel.errorEvent) { error -> displayMessage(error) }
        observe(viewModel.callbackEvent) { callback ->
            binding.homeWebView.evaluateJavascript(callback, null)
        }

        observe(viewModel.appListItems) { items ->
            appDrawerAdapter.appListItems = items
        }

        observe(viewModel.theme) { theme ->
            binding.webViewWrapper.background = ColorDrawable(theme.primaryBackgroundColor)
            binding.appDrawerBackground.background = theme.appListBackground
            binding.appDrawerHandle.drawable.setTint(theme.primaryTextColor)
            binding.appDrawerHandle.background = theme.appDrawerHandleBackground

            appDrawerAdapter.notifyDataSetChanged()
        }

        observe(appDrawerAdapter.appHiddenEvent) { viewModel.hideApp(it) }

        binding.appList.layoutManager =
            GridLayoutManager(context, viewModel.appDrawerColumns)
        binding.appList.adapter = appDrawerAdapter

        activity?.apply {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT

            onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                this.isEnabled = true
                if (binding.homeWebView.canGoBack()) binding.homeWebView.goBack()
            }
        }

        initializeWebView()
        applyInsets()
    }

    override fun onResume() {
        super.onResume()
        packageReceiver = PackageReceiver.initialize(this)
        requireContext().registerReceiver(packageReceiver, packageReceiver.filter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(packageReceiver)
    }

    override fun onPackageReceived() {
        viewModel.updateAppList()
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.homeWebView) { _, insets ->
            binding.webViewWrapper.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )

            binding.homeContainer.constraintSetIds.forEach { id ->
                binding.homeContainer.getConstraintSet(id).apply {
                    setMargin(
                        binding.appDrawerHandle.id,
                        ConstraintSet.TOP,
                        insets.systemWindowInsetTop
                    )
                    setMargin(
                        binding.appDrawerHandle.id,
                        ConstraintSet.BOTTOM,
                        insets.systemWindowInsetBottom
                    )
                }
            }

            insets
        }
    }

    private fun initializeWebView() {
        if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true)

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
