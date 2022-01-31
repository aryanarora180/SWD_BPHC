package `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.FaqQuestionWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_faq_item.view.*

class FaqItemAdapter : RecyclerView.Adapter<FaqItemAdapter.ViewHolder>() {

    var data = emptyList<FaqQuestionWrapper>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.question_text
        val answerText: TextView = view.answer_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_faq_item, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val qa = data[position].question

        holder.questionText.text = qa.question
        holder.answerText.text = qa.answer
    }
}