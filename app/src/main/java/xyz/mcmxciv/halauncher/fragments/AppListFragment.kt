package xyz.mcmxciv.halauncher.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.SettingsActivity
import xyz.mcmxciv.halauncher.utilities.UserSettings

class AppListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.settings_button)
        button.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
