package xyz.mcmxciv.halauncher.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import xyz.mcmxciv.halauncher.BaseFragment
import xyz.mcmxciv.halauncher.BuildConfig
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.databinding.FragmentPrivacyPolicyBinding
import xyz.mcmxciv.halauncher.requireHalauncherActivity

class PrivacyPolicyFragment : BaseFragment() {
    private lateinit var binding: FragmentPrivacyPolicyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.privacy_policy_title)
        requireHalauncherActivity().apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        binding.webview.apply {
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl(BuildConfig.PRIVACY_POLICY_URL)
        }
    }
}
