package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.CounsellorSlot
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.list_item_available_slot.view.*

class AvailableSlotAdapter
    : RecyclerView.Adapter<AvailableSlotAdapter.ViewHolder>() {

    private val dateFormatter = DateFormatter()

    lateinit var listener: (CounsellorSlot) -> Unit

    var data = listOf<CounsellorSlot>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.slot_date_text
        val hourText: TextView = view.slot_hour_text
        val bookButton: MaterialButton = view.book_slot_button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_available_slot, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = data[position]

        holder.dateText.text = dateFormatter.getFormattedDate(
            slot.date,
            DateFormatter.FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR
        )
        holder.hourText.text = formatHour(slot.slot)
        holder.bookButton.setOnClickListener {
            listener(slot)
        }
    }


    private fun formatHour(hour: Int) = when {
        hour < 12 -> "$hour AM"
        hour == 12 -> "$hour noon"
        else -> "${hour - 12} PM"
    }
}