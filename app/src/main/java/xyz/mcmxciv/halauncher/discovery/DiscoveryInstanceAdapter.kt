package xyz.mcmxciv.halauncher.discovery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.mcmxciv.halauncher.databinding.ListItemInstanceBinding

class DiscoveryInstanceAdapter
    : RecyclerView.Adapter<DiscoveryInstanceAdapter.DiscoveryInstanceViewHolder>() {
    private var _instances = listOf<HomeAssistantInstance>()
    private var _onInstanceSelectedListener: (instance: HomeAssistantInstance) -> Unit = {}

    class DiscoveryInstanceViewHolder(
        val binding: ListItemInstanceBinding
    ) : RecyclerView.ViewHolder(binding.root)

    fun setInstances(instances: List<HomeAssistantInstance>) {
        _instances = instances
        notifyDataSetChanged()
    }

    fun setOnInstanceSelectedListener(listener: (instance: HomeAssistantInstance) -> Unit) {
        _onInstanceSelectedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveryInstanceViewHolder {
        val binding = ListItemInstanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoveryInstanceViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: DiscoveryInstanceViewHolder, position: Int) {
        holder.binding.instanceName.text = _instances[position].name
        holder.binding.instanceUrl.text = _instances[position].hostName
        holder.binding.root.setOnClickListener {
            _onInstanceSelectedListener(_instances[position])
        }
    }

    override fun getItemCount(): Int = _instances.count()
}
