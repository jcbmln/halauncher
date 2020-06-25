package xyz.mcmxciv.halauncher.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.mcmxciv.halauncher.BaseFragment
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.apps.AppDrawerAdapter
import xyz.mcmxciv.halauncher.databinding.FragmentHomeBinding
import xyz.mcmxciv.halauncher.fragmentViewModels
import javax.inject.Inject

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by fragmentViewModels { component.homeViewModel() }

    @Inject
    lateinit var appDrawerAdapter: AppDrawerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.navigation) { navigate(it) }
        observe(viewModel.error) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.unable_to_connect)
                .setMessage(id)
                .setPositiveButton(R.string.retry) { _, _ ->
                    binding.homeWebView.reload()
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    findNavController().popBackStack()
                }
        }
        observe(viewModel.authenticationUrl) { binding.homeWebView.loadUrl(it) }
        observe(viewModel.callbackEvent) {
            binding.homeWebView.evaluateJavascript(it, null)
        }

        adjustSystemUi()
        applyInsets()
        setOnBackPressedDispatcher()
        initializeAppDrawer()
        initializeWebView()
    }

    private fun adjustSystemUi() {
        requireActivity().apply {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.homeWebView) { _, insets ->
            binding.webviewWrapper.updatePadding(
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

    private fun setOnBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = true
            if (binding.homeWebView.canGoBack()) binding.homeWebView.goBack()
        }
    }

    private fun initializeAppDrawer() {
        binding.appList.layoutManager = GridLayoutManager(context, viewModel.appDrawerColumns)
        binding.appList.adapter = appDrawerAdapter
    }

    private fun initializeWebView() {
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        binding.homeWebView.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.loadUrl(viewModel.themeCallback)
                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    viewModel.showWebError()
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    super.onReceivedSslError(view, handler, error)
                    viewModel.showSslError()
                }
            }

            addJavascriptInterface(object : HomeAssistantJavaScriptInterface {
                @JavascriptInterface
                override fun getExternalAuth(result: String) {
                    viewModel.getExternalAuth(result)
                }

                @JavascriptInterface
                override fun revokeExternalAuth(result: String) {
                    viewModel.revokeExternalAuth(result)
                }

                @JavascriptInterface
                override fun themesUpdated(result: String) {
                    viewModel.setTheme(result)
                }

                @JavascriptInterface
                override fun externalBus(message: String) {
                    viewModel.parseMessage(message)
                }
            }, "externalApp")
        }
    }
}
