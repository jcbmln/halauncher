package xyz.mcmxciv.halauncher.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import org.json.JSONObject
import timber.log.Timber
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentHomeBinding
import xyz.mcmxciv.halauncher.models.DeviceProfile
import xyz.mcmxciv.halauncher.models.ErrorState
import xyz.mcmxciv.halauncher.models.WebCallback
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import xyz.mcmxciv.halauncher.ui.createViewModel
import xyz.mcmxciv.halauncher.ui.displayMessage
import xyz.mcmxciv.halauncher.ui.home.appdrawer.AppDrawerAdapter
import xyz.mcmxciv.halauncher.ui.navigate
import xyz.mcmxciv.halauncher.ui.observe
import xyz.mcmxciv.halauncher.utils.AppLauncher
import java.io.BufferedReader
import javax.inject.Inject

class HomeFragment : LauncherFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var appDrawerAdapter: AppDrawerAdapter

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

        appDrawerAdapter = AppDrawerAdapter(deviceProfile, appLauncher, viewModel)

        observe(viewModel.appListItems) { items ->
            appDrawerAdapter.appListItems = items
        }

        observe(viewModel.theme) { theme ->
            binding.webViewWrapper.background = ColorDrawable(theme.primaryBackgroundColor)
            binding.appDrawerBackground.background = theme.appListBackground
            binding.appDrawerHandle.drawable.setTint(theme.primaryTextColor)
            val background = ShapeDrawable(OvalShape())
            background.alpha = 127
            background.paint.color = theme.cardBackgroundColor
            binding.appDrawerHandle.background = background

            appDrawerAdapter.notifyDataSetChanged()
        }

        observe(viewModel.callback) { resource ->
            binding.homeWebView.evaluateJavascript(resource.callback, null)

            if (resource is WebCallback.RevokeAuthCallback) {
                navigate(HomeFragmentDirections.actionHomeFragmentToAuthenticationNavigationGraph())
            }
        }

        binding.appList.layoutManager =
            GridLayoutManager(context, deviceProfile.appDrawerColumns)
        binding.appList.adapter = appDrawerAdapter

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            this.isEnabled = true

            when {
                binding.homeWebView.canGoBack() -> binding.homeWebView.goBack()
            }
        }

        activity?.apply {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }

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

            binding.appList.updatePadding(bottom = insets.systemWindowInsetBottom)

            insets
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
