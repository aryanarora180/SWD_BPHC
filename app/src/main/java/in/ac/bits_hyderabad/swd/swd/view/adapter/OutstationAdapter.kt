package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Outstation
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_outstation.view.*

class OutstationAdapter :
    RecyclerView.Adapter<OutstationAdapter.ViewHolder>() {

    private val dateFormatter = DateFormatter()

    lateinit var listener: (Outstation) -> Unit
    var data = listOf<Outstation>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private fun formatDate(fromDate: String, toDate: String) =
        "${
            dateFormatter.getFormattedDate(
                fromDate,
                DateFormatter.FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR
            )
        } to ${dateFormatter.getFormattedDate(toDate)}"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val datesText: TextView = view.outstaion_dates_text
        val locationText: TextView = view.location_text
        val reasonText: TextView = view.reason_text
        val statusImage: ImageView = view.status_image
        val statusText: TextView = view.status_text
        val deleteImage: ImageView = view.delete_image
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_outstation, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val outstation = data[position]

        holder.datesText.text = formatDate(outstation.fromDate, outstation.toDate)
        holder.locationText.text = outstation.location
        holder.reasonText.text = outstation.reason

        when (outstation.approved) {
            -1 -> {
                holder.statusImage.setImageResource(R.drawable.outline_cancel_24)
                holder.statusText.setText(R.string.outstation_denied)

                holder.deleteImage.setOnClickListener {
                    listener(outstation)
                }
            }
            0 -> {
                holder.statusImage.setImageResource(R.drawable.outline_hourglass_top_24)
                holder.statusText.setText(R.string.outstation_waiting)

                holder.deleteImage.setOnClickListener {
                    listener(outstation)
            }
            }
            1 -> {
                holder.statusImage.setImageResource(R.drawable.outline_check_circle_24)
                holder.statusText.setText(R.string.outstation_accepted)
                holder.deleteImage.visibility = View.INVISIBLE
            }
        }
    }

}