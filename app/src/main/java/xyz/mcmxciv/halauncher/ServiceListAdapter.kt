package xyz.mcmxciv.halauncher

import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.service_list_item.view.*
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener

class ServiceListAdapter(
    private val listener: ServiceSelectedListener
) : RecyclerView.Adapter<ServiceListAdapter.ServiceListViewHolder>() {

    private var serviceList: MutableList<NsdServiceInfo> = ArrayList()

    class ServiceListViewHolder(val view: View)
        : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ServiceListViewHolder(
            layoutInflater.inflate(R.layout.service_list_item, parent, false)
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

    fun setData(services: MutableList<NsdServiceInfo>) {
        serviceList = services
        notifyDataSetChanged()
    }
}