package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Deduction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_deduction.view.*

class DeductionsAdapter : RecyclerView.Adapter<DeductionsAdapter.ViewHolder>() {

    var data = listOf<Deduction>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var listener: (Deduction) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_deduction, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deduction = data[position]

        holder.amountText.text =
            holder.itemView.context.getString(
                R.string.deductions_price,
                deduction.totalAmount.toString()
            )
        holder.nameText.text = deduction.goodieName
        holder.quantityText.text =
            holder.itemView.context.getString(
                R.string.deductions_quantity,
                deduction.quantity.toString()
            )

        holder.itemView.setOnClickListener { listener(deduction) }
    }

    override fun getItemCount() = data.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountText: TextView = view.deduction_amount_text
        val nameText: TextView = view.deduction_name_text
        val quantityText: TextView = view.deduction_quantity_text
    }

}