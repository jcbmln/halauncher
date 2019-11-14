package xyz.mcmxciv.halauncher

import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceListAdapter(
    private val serviceList: MutableList<NsdServiceInfo>
) : RecyclerView.Adapter<ServiceListAdapter.ServiceListViewHolder>() {

    class ServiceListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_list_item, parent, false)
        return ServiceListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {
        val serviceInfo = serviceList[position]
        val name = serviceInfo.serviceName
        //val host = "${serviceInfo.host.hostAddress}:${serviceInfo.port}"

        val serviceName = holder.view.findViewById<TextView>(R.id.service_name)
        serviceName.text = name

//        val serviceHost = holder.view.findViewById<TextView>(R.id.service_host)
//        serviceHost.text = host
    }

    override fun getItemCount(): Int = serviceList.count()

    fun addServiceItem(serviceInfo: NsdServiceInfo) {
        serviceList.add(serviceInfo)
        notifyDataSetChanged()
    }
}