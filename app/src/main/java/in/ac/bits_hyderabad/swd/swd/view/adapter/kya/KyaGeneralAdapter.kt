package `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya

import `in`.ac.bits_hyderabad.swd.swd.data.GeneralElectives
import `in`.ac.bits_hyderabad.swd.swd.databinding.ListItemKyaGeneralBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class KyaGeneralAdapter : RecyclerView.Adapter<KyaGeneralAdapter.ViewHolder>() {

    var data = listOf<GeneralElectives>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemKyaGeneralBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                with(data[position]) {
                    questionText.text = topic
                    answerText.text = text
                }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemKyaGeneralBinding) :
        RecyclerView.ViewHolder(binding.root)
}