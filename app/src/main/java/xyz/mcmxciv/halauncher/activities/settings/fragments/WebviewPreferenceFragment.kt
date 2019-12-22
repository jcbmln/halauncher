package xyz.mcmxciv.halauncher.activities.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.utils.BaseFragment
import kotlinx.android.synthetic.main.webview_preference_fragment.*

class WebviewPreferenceFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.webview_preference_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString("url")
        webview.loadUrl(url)
    }
}
