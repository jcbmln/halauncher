package xyz.mcmxciv.halauncher.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.LauncherApplication
import xyz.mcmxciv.halauncher.R
import xyz.mcmxciv.halauncher.models.ActivityInfo
import xyz.mcmxciv.halauncher.utils.Utilities

class AppListAdapter(private val context: Context, activityList: List<ActivityInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var isDarkBackground: Boolean = false
    private var activities = activityList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_app, parent, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = activities[position]
        val appItem = holder.view.findViewById<TextView>(R.id.app_item)
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())
        val resources = LauncherApplication.instance.resources
        val drawable = appInfo.icon.toDrawable(resources)
        appItem.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        appItem.text = appInfo.displayName

        if (isDarkBackground) appItem.setTextColor(context.getColor(R.color.colorBackground))

        appItem.setOnClickListener {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(appInfo.packageName)
            context.startActivity(intent)
        }

        appItem.setOnLongClickListener {
            val popup = PopupMenu(context, appItem)
            popup.menuInflater.inflate(R.menu.app_popup_menu, popup.menu)
            popup.show()

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = activities.size

    fun update(activityList: List<ActivityInfo>) {
        activities = activityList
        notifyDataSetChanged()
    }

    fun setThemeColor(color: Int) {
        isDarkBackground = Utilities.isDarkColor(color)
        notifyDataSetChanged()
    }
}