package `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.FaqQuestionWrapper
import `in`.ac.bits_hyderabad.swd.swd.databinding.ListItemFaqSearchBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FaqsSearchItemAdapter : RecyclerView.Adapter<FaqsSearchItemAdapter.ViewHolder>() {

    lateinit var listener: (Int, Int) -> Unit

    var data = listOf<FaqQuestionWrapper>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private lateinit var _context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        _context = recyclerView.context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ListItemFaqSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = data[position]

        with(holder.binding) {
            faqGroupText.text = item.groupName
            faqTopicText.text = _context.getString(R.string.faq_search_topic, item.topicName)
            faqQuestionText.text = item.question.question
            faqAnswerText.text = item.question.answer

            faqGroupCard.setOnClickListener { listener(item.groupIndex, item.questionIndexInGroup) }
        }
    }

    class ViewHolder(val binding: ListItemFaqSearchBinding) :
        RecyclerView.ViewHolder(binding.root)
}