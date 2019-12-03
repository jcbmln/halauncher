package xyz.mcmxciv.halauncher

import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.databinding.ServiceListItemBinding
import xyz.mcmxciv.halauncher.interfaces.ServiceSelectedListener

class ServiceListAdapter(
    private val listener: ServiceSelectedListener
) : RecyclerView.Adapter<ServiceListAdapter.ServiceListViewHolder>() {

    private lateinit var binding: ServiceListItemBinding
    private var serviceList: MutableList<NsdServiceInfo> = ArrayList()

    class ServiceListViewHolder(val binding: ServiceListItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ServiceListItemBinding.inflate(layoutInflater, parent, false)

        return ServiceListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {
        val serviceInfo = serviceList[position]
        val name = serviceInfo.serviceName

        val serviceText = holder.binding.serviceText
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