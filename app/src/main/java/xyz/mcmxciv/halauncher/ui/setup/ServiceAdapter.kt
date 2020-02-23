package xyz.mcmxciv.halauncher.ui.setup

import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_service.view.*
import xyz.mcmxciv.halauncher.R

class ServiceAdapter(
    private val listener: ServiceSelectedListener
) : RecyclerView.Adapter<ServiceAdapter.ServiceListViewHolder>() {

    private var serviceList: List<NsdServiceInfo> = ArrayList()

    class ServiceListViewHolder(val view: View)
        : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ServiceListViewHolder(
            layoutInflater.inflate(
                R.layout.list_item_service,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {
        val serviceInfo = serviceList[position]
        val name = serviceInfo.serviceName

        val serviceText = holder.view.serviceText
        serviceText.text = name
        serviceText.setOnClickListener {
            listener.onServiceSelected(serviceInfo)
        }
    }

    override fun getItemCount(): Int = serviceList.count()

    fun setData(services: List<NsdServiceInfo>) {
        serviceList = services
        notifyDataSetChanged()
    }
}