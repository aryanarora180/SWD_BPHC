package `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya

import `in`.ac.bits_hyderabad.swd.swd.data.KyaData
import `in`.ac.bits_hyderabad.swd.swd.databinding.ListItemKyaSingleBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class KyaGroupsAdapter : RecyclerView.Adapter<KyaGroupsAdapter.ViewHolder>() {

    lateinit var listener: (String) -> Unit

    private val data = KyaData.kyaGroups

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ListItemKyaSingleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                kyaGroupText.text = data[position].second
                kyaGroupCard.setOnClickListener { listener(data[position].first) }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemKyaSingleBinding) :
        RecyclerView.ViewHolder(binding.root)
}