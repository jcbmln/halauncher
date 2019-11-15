package xyz.mcmxciv.halauncher

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ServiceListAdapter(
    private val serviceList: MutableList<NsdServiceInfo>,
    private val nsdManager: NsdManager,
    private val callback: HomeAssistantResolveListener.Callback
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

        val serviceButton = holder.view.findViewById<Button>(R.id.service_button)
        serviceButton.text = name
        serviceButton.setOnClickListener {
            val resolveListener = HomeAssistantResolveListener(callback)
            nsdManager.resolveService(serviceInfo, resolveListener)
        }

//        val serviceHost = holder.view.findViewById<TextView>(R.id.service_host)
//        serviceHost.text = host
    }

    override fun getItemCount(): Int = serviceList.count()

    fun addServiceItem(serviceInfo: NsdServiceInfo) {
        serviceList.add(serviceInfo)
        notifyDataSetChanged()
    }
}