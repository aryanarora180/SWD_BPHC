package `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya

import `in`.ac.bits_hyderabad.swd.swd.data.DepartmentData
import `in`.ac.bits_hyderabad.swd.swd.databinding.ListItemKyaDepartmentBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class KyaDepartmentAdapter : RecyclerView.Adapter<KyaDepartmentAdapter.ViewHolder>() {

    var data = listOf<Pair<String, DepartmentData>>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemKyaDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                with(data[position].second) {
                    questionText.text = question
                    answerText.text = answer
                }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemKyaDepartmentBinding) :
        RecyclerView.ViewHolder(binding.root)
}