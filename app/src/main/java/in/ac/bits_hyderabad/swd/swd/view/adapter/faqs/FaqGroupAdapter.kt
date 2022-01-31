package `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.FaqGroupWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.list_item_faq_group.view.*

class FaqGroupAdapter : RecyclerView.Adapter<FaqGroupAdapter.ViewHolder>() {

    lateinit var listener: (Int) -> Unit

    var data = listOf<FaqGroupWrapper>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val faqGroupText: TextView = view.faq_topic_text
        val faqGroupTopicsText: TextView = view.faq_group_topics_text
        val faqGroupCard: MaterialCardView = view.faq_group_card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_faq_group, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = data[position]

        holder.faqGroupText.text = group.groupName
        holder.faqGroupTopicsText.text = group.topicNames
        holder.faqGroupCard.setOnClickListener { listener(position) }
    }
}