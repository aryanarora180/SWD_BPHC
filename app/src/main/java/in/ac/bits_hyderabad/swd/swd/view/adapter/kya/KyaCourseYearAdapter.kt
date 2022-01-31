package `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya

import `in`.ac.bits_hyderabad.swd.swd.databinding.ListItemKyaSingleBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class KyaCourseYearAdapter : RecyclerView.Adapter<KyaCourseYearAdapter.ViewHolder>() {

    lateinit var listener: (String) -> Unit

    var data = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
                kyaGroupText.text = data[position]
                kyaGroupCard.setOnClickListener { listener(data[position]) }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemKyaSingleBinding) :
        RecyclerView.ViewHolder(binding.root)
}