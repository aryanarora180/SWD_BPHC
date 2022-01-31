package `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya

import `in`.ac.bits_hyderabad.swd.swd.data.MinorData
import `in`.ac.bits_hyderabad.swd.swd.databinding.ListItemKyaMinorsBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class KyaMinorsAdapter : RecyclerView.Adapter<KyaMinorsAdapter.ViewHolder>() {

    var data = listOf<Pair<String, MinorData>>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemKyaMinorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                with(data[position].second) {
                    topicText.text = topic
                    textText.text = text
                }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemKyaMinorsBinding) :
        RecyclerView.ViewHolder(binding.root)
}