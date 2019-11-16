package xyz.mcmxciv.halauncher

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.fragments.DiscoveryViewModel

class ServiceListAdapter(
    private val viewModel: DiscoveryViewModel
) : RecyclerView.Adapter<ServiceListAdapter.ServiceListViewHolder>() {

    private var serviceList: MutableList<NsdServiceInfo> = ArrayList()

    class ServiceListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_list_item, parent, false)
        return ServiceListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {
        val serviceInfo = serviceList[position]
        val name = serviceInfo.serviceName

        val serviceButton = holder.view.findViewById<Button>(R.id.service_button)
        serviceButton.text = name
        serviceButton.setOnClickListener {
            viewModel.selectedService.value = serviceInfo
        }
    }

    override fun getItemCount(): Int = serviceList.count()

    fun setData(services: MutableList<NsdServiceInfo>) {
        serviceList = services
        notifyDataSetChanged()
    }
}