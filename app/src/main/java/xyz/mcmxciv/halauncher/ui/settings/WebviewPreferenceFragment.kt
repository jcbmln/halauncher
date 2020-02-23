package xyz.mcmxciv.halauncher.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.ui.LauncherFragment
import kotlinx.android.synthetic.main.fragment_webview_preference.*

class WebviewPreferenceFragment : LauncherFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview_preference, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString("url")
        webview.loadUrl(url)
    }
}
