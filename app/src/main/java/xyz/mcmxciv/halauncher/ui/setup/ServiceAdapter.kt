package xyz.mcmxciv.halauncher.ui.setup

import android.annotation.SuppressLint
import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.databinding.ListItemServiceBinding

class ServiceAdapter(
    private val listener: ServiceSelectedListener
) : RecyclerView.Adapter<ServiceAdapter.ServiceListViewHolder>() {

    private var serviceList: List<NsdServiceInfo> = ArrayList()

    class ServiceListViewHolder(val binding: ListItemServiceBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ServiceListViewHolder(
            ListItemServiceBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {
        val serviceInfo = serviceList[position]
        val name = serviceInfo.serviceName
        val url = "http://${serviceInfo.host.hostAddress}:${serviceInfo.port}"

        holder.binding.serviceNameText.text = name
        holder.binding.serviceHostUrl.text = url
        holder.binding.serviceItemContainer.setOnClickListener {
            listener.onServiceSelected(url)
        }
    }

    override fun getItemCount(): Int = serviceList.count()

    fun setData(services: List<NsdServiceInfo>) {
        serviceList = services
        notifyDataSetChanged()
    }
}