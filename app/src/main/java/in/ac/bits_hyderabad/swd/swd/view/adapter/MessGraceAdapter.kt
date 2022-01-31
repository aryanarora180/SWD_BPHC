package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Grace
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_grace.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessGraceAdapter :
    RecyclerView.Adapter<MessGraceAdapter.ViewHolder>() {

    private val dateFormatter = DateFormatter()

    private lateinit var _context: Context
    var data = listOf<Grace>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.date_text
        val appliedDateText: TextView = view.applied_on_text
        val outstationText: TextView = view.loan_taken_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_grace, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val grace = data[position]
        holder.dateText.text = getGraceFormattedDate(grace.date)
        holder.appliedDateText.text = getAppliedFormattedDate(grace.requestedOn)
        if (grace.isOutstation == 0) {
            holder.outstationText.visibility = View.GONE
        }
    }

    private val onDateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    private val onTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    private fun getGraceFormattedDate(date: String): String {
        val dateString =
            dateFormatter.getFormattedDate(date, DateFormatter.FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR)
        return _context.getString(R.string.mess_grace_for, dateString)
    }

    private fun getAppliedFormattedDate(date: Long): String {
        return _context.getString(
            R.string.applied_on_date_time,
            onDateFormatter.format(Date(date)),
            onTimeFormatter.format(Date(date))
        )
    }
}