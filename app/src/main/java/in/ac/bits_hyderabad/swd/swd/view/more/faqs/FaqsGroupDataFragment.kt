package `in`.ac.bits_hyderabad.swd.swd.view.more.faqs

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.FaqQuestionWrapper
import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentFaqsGroupDataBinding
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs.FaqItemAdapter
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs.FaqsTopicDecoration
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.FaqsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs

class FaqsGroupDataFragment : Fragment(R.layout.fragment_faqs_group_data) {

    private lateinit var binding: FragmentFaqsGroupDataBinding
    private val viewModel by activityViewModels<FaqsViewModel>()
    private val navArgs by navArgs<FaqsGroupDataFragmentArgs>()

    private val faqItemAdapter = FaqItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFaqsGroupDataBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.faqGroups.value?.faqs?.get(navArgs.groupIndex)?.questions?.let {
            faqItemAdapter.data = it
            binding.faqDataRecycler.apply {
                adapter = faqItemAdapter
                if (itemDecorationCount == 0) {
                    val decoration = FaqsTopicDecoration(
                        requireContext(),
                        resources.getDimensionPixelSize(R.dimen.header_height),
                        getSectionCallback(it)
                    )
                    addItemDecoration(decoration)
                }

                navArgs.questionNumber.let { scrollTo ->
                    if (scrollTo != -1)
                        scrollToPosition(if (scrollTo == 0) 0 else (scrollTo - 1))
                }
            }
        }
    }

    private fun getSectionCallback(list: List<FaqQuestionWrapper>): FaqsTopicDecoration.SectionCallback {
        return object : FaqsTopicDecoration.SectionCallback {
            override fun isSectionHeader(pos: Int): Boolean {
                return pos == 0 || list[pos].topicName != list[pos - 1].topicName
            }

            override fun getSectionHeaderName(pos: Int): String {
                return list[pos].topicName
            }
        }
    }
}