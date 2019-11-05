package xyz.mcmxciv.halauncher

import android.content.Context
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.icons.IconShape
import xyz.mcmxciv.halauncher.models.AppInfo

class AppListAdapter(private val context: Context, private val appList: List<AppInfo>, private val idp: InvariantDeviceProfile) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    class AppListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_view_item, parent, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = appList[position]
        val appImage = holder.view.findViewById<ImageView>(R.id.app_image)
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, Paint())

        val radius = idp.iconBitmapSize.toFloat() / 2
        val circle = Bitmap.createBitmap(idp.iconBitmapSize, idp.iconBitmapSize, Bitmap.Config.ARGB_8888)
        val clip = Canvas(circle)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        IconShape.setShape(IconShape.ShapeType.Circle, radius)
        clip.drawPath(IconShape.shapePath, paint)

        val bitmap = Bitmap.createBitmap(idp.iconBitmapSize, idp.iconBitmapSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)


        val drawable = appInfo.icon as AdaptiveIconDrawable
        val background = drawable.background
        val foreground = drawable.foreground
        background.setBounds(0, 0, idp.iconBitmapSize, idp.iconBitmapSize)
        foreground.setBounds(0, 0, idp.iconBitmapSize, idp.iconBitmapSize)
        background.draw(canvas)

        val mask = Paint()
        mask.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(circle, 0f, 0f, mask)
        foreground.draw(canvas)
        canvas.drawBitmap(circle, 0f, 0f, mask)

        appImage.setImageDrawable(bitmap.toDrawable(context.resources))
        appImage.layoutParams.height = idp.iconBitmapSize
    }

    override fun getItemCount() = appList.size
}