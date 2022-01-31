package `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya

import `in`.ac.bits_hyderabad.swd.swd.data.CourseGuideData
import `in`.ac.bits_hyderabad.swd.swd.databinding.ListItemKyaDepartmentBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class KyaCourseGuideDataAdapter : RecyclerView.Adapter<KyaCourseGuideDataAdapter.ViewHolder>() {

    var data = listOf<CourseGuideData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ListItemKyaDepartmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(binding) {
                with(data[position]) {
                    questionText.text = courseNumber
                    answerText.text = advice
                }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemKyaDepartmentBinding) :
        RecyclerView.ViewHolder(binding.root)
}