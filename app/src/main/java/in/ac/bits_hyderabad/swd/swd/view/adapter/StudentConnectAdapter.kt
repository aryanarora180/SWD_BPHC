package `in`.ac.bits_hyderabad.swd.swd.view.adapter

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.StudentConnectPerson
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_student_connect.view.*

class StudentConnectAdapter : RecyclerView.Adapter<StudentConnectAdapter.ViewHolder>() {

    lateinit var listener: (StudentConnectPerson, Int) -> Unit
    var data = listOf<StudentConnectPerson>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        const val ACTION_CALL = 1
        const val ACTION_WHATSAPP = 2
        const val ACTION_EMAIL = 3
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.name_text
        val positionText: TextView = view.position_text
        val emailImage: ImageView = view.email_image
        val whatsappImage: ImageView = view.whatsapp_image
        val callImage: ImageView = view.call_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_student_connect, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = data[position]

        holder.nameText.text = person.name.trim()
        holder.positionText.text = person.position.trim()
        holder.callImage.setOnClickListener {
            listener(person, ACTION_CALL)
        }
        holder.whatsappImage.setOnClickListener {
            listener(person, ACTION_WHATSAPP)
        }
        holder.emailImage.setOnClickListener {
            listener(person, ACTION_EMAIL)
        }
    }

    override fun getItemCount() = data.size

}