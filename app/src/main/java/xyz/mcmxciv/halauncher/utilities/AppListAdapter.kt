package xyz.mcmxciv.halauncher.utilities

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

class AppListAdapter(private val appList: ArrayList<AppList.AppInfo>) :
        RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_list_item, parent, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = appList[position]

        val textView = holder.view.findViewById(R.id.app_item_text_view) as TextView
        textView.text = appInfo.displayName

        val imageView = holder.view.findViewById(R.id.app_item_image_view) as ImageView
        imageView.setImageDrawable(appInfo.icon)
    }

    override fun getItemCount() = appList.size
}