package xyz.mcmxciv.halauncher.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.SettingsActivity
import xyz.mcmxciv.halauncher.utilities.AppList

class AppListFragment
internal constructor(drawable: Drawable?): ViewPagerFragment(drawable) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppList.getAppList(context!!)

        val button = view.findViewById<Button>(R.id.settings_button)
        button.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
