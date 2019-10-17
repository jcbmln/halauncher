package xyz.mcmxciv.halauncher.utilities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ScaleDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import xyz.mcmxciv.halauncher.R

class AppListAdapter(private val appList: ArrayList<AppList.AppInfo>,
                     private val invariantDeviceProfile: InvariantDeviceProfile) :
        RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_list_item, parent, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = appList[position]

        holder.view.setOnClickListener {
            launchApp(holder.view.context, appInfo.packageName)
        }

        val textView = holder.view.findViewById(R.id.app_item_text_view) as TextView
        textView.text = appInfo.displayName

        val imageView = holder.view.findViewById(R.id.app_item_image_view) as ImageView
        imageView.setImageDrawable(appInfo.icon)
        imageView.layoutParams.height = invariantDeviceProfile.iconBitmapSize
    }

    override fun getItemCount() = appList.size

    private fun launchApp(context: Context, packageName: String) {
        val launcherIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launcherIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launcherIntent)
    }
}