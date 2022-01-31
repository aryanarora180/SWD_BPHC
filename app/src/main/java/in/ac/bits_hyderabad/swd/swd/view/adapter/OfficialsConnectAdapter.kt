package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.OfficialConnectPerson
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_official_connect.view.*

class OfficialsConnectAdapter : RecyclerView.Adapter<OfficialsConnectAdapter.ViewHolder>() {

    lateinit var listener: (OfficialConnectPerson) -> Unit
    var data = listOf<OfficialConnectPerson>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.name_text
        val positionText: TextView = view.position_text
        val emailImage: ImageView = view.email_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_official_connect, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = data[position]

        holder.nameText.text = person.name
        holder.positionText.text = person.position
        holder.emailImage.setOnClickListener {
            listener(person)
        }
    }

    override fun getItemCount() = data.size

}