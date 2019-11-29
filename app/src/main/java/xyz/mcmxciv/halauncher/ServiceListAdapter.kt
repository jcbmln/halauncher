package xyz.mcmxciv.halauncher

import android.content.Context
import android.net.nsd.NsdServiceInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.databinding.ServiceListItemBinding
import xyz.mcmxciv.halauncher.activities.setup.discovery.DiscoveryViewModel

class ServiceListAdapter(
    context: Context
) : RecyclerView.Adapter<ServiceListAdapter.ServiceListViewHolder>() {

    private lateinit var binding: ServiceListItemBinding
    private val viewModel =
        ViewModelProviders.of(context as FragmentActivity).get(DiscoveryViewModel::class.java)
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
            viewModel.selectedService.value = serviceInfo
        }
    }

    override fun getItemCount(): Int = serviceList.count()

    fun setData(services: MutableList<NsdServiceInfo>) {
        serviceList = services
        notifyDataSetChanged()
    }
}