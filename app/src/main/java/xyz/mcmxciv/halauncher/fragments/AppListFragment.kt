package xyz.mcmxciv.halauncher.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.activities.SettingsActivity
import xyz.mcmxciv.halauncher.utilities.AppList
import xyz.mcmxciv.halauncher.utilities.AppListAdapter
import xyz.mcmxciv.halauncher.utilities.InvariantDeviceProfile

class AppListFragment
internal constructor(drawable: Drawable?): ViewPagerFragment(drawable) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val invariantDeviceProfile = InvariantDeviceProfile(view.context)
        val appList = AppList.getAppList(view.context, invariantDeviceProfile)

        viewManager = GridLayoutManager(context, invariantDeviceProfile.numColumns)
        viewAdapter = AppListAdapter(appList, invariantDeviceProfile)

        recyclerView = view.findViewById<RecyclerView>(R.id.app_list_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val button = view.findViewById<Button>(R.id.settings_button)
        button.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
