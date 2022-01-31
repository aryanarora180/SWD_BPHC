package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.CounsellorBooking
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_booking.view.*
import java.text.SimpleDateFormat
import java.util.*

class MyBookingsAdapter
    : RecyclerView.Adapter<MyBookingsAdapter.ViewHolder>() {

    private val dateFormatter = DateFormatter()

    private lateinit var _context: Context

    lateinit var listener: (Int) -> Unit

    var data = listOf<CounsellorBooking>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.booking_timing_text
        val appliedOnText: TextView = view.booking_applied_on_text
        val deleteButton: ImageView = view.booking_delete_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        return ViewHolder(
            LayoutInflater.from(_context)
                .inflate(R.layout.list_item_booking, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = data[position]

        holder.dateText.text = "${
            dateFormatter.getFormattedDate(
                slot.date,
                DateFormatter.FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR
            )
        } at ${formatHour(slot.slot)}"
        holder.appliedOnText.text = getAppliedFormattedDate(slot.bookingTime)
        holder.deleteButton.setOnClickListener {
            listener(slot.bookingId)
        }
    }

    private fun formatHour(hour: Int) = when {
        hour < 12 -> "$hour AM"
        hour == 12 -> "$hour noon"
        else -> "${hour - 12} PM"
    }

    private val onTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    private fun getAppliedFormattedDate(date: Long): String {
        return _context.getString(
            R.string.applied_on_date_time,
            dateFormatter.getFormattedDate(
                date,
                DateFormatter.FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR
            ),
            onTimeFormatter.format(Date(date))
        )
    }
}