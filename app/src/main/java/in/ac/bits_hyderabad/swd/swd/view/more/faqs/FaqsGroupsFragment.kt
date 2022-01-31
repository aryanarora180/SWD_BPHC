package `in`.ac.bits_hyderabad.swd.swd.view.more.faqs

import `in`.ac.bits_hyderabad.swd.swd.data.FaqsWrapper
import `in`.ac.bits_hyderabad.swd.swd.databinding.FaqsGroupsFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs.FaqGroupAdapter
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs.FaqsSearchItemAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.FaqsViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

class FaqsGroupsFragment : Fragment() {

    private lateinit var binding: FaqsGroupsFragmentBinding
    private val viewModel by activityViewModels<FaqsViewModel>()

    private val faqGroupAdapter = FaqGroupAdapter()
    private val faqSearchItemAdapter = FaqsSearchItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FaqsGroupsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        faqGroupAdapter.listener = { groupIndex ->
            val action =
                FaqsGroupsFragmentDirections.actionFaqsGroupsFragmentToFaqsGroupDataFragment(
                    groupIndex
                )
            findNavController().navigate(action)
        }

        faqSearchItemAdapter.listener = { groupIndex, indexToScrollTo ->
            val action =
                FaqsGroupsFragmentDirections.actionFaqsGroupsFragmentToFaqsGroupDataFragment(
                    groupIndex, indexToScrollTo
                )
            findNavController().navigate(action)
            binding.faqsSearchEdit.setText("")
        }

        with(viewModel) {
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
            faqGroups.observe(viewLifecycleOwner, faqGroupsObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                faqGroupsProgress.visibility = View.VISIBLE
                errorOccurredView.root.visibility = View.GONE
                faqGroupsRecycler.visibility = View.GONE
                faqsLastUpdatedText.visibility = View.GONE
                faqsSearchLayout.visibility = View.GONE
            } else {
                faqGroupsProgress.visibility = View.GONE
            }
        }
    }

    private val faqGroupsObserver = Observer<FaqsWrapper> { faqs ->
        with(binding) {
            faqGroupsRecycler.apply {
                visibility = View.VISIBLE
                adapter = faqGroupAdapter
            }
            faqGroupAdapter.data = faqs.faqs

            faqsLastUpdatedText.visibility = View.VISIBLE
            faqsLastUpdatedText.text = viewModel.formatLastUpdated(faqs.lastUpdated)
            faqsSearchLayout.visibility = View.VISIBLE

            faqsSearchEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        faqGroupsRecycler.adapter = faqGroupAdapter
                        faqGroupAdapter.data = faqs.faqs
                    } else {
                        faqGroupsRecycler.adapter = faqSearchItemAdapter

                        faqSearchItemAdapter.data = faqs.formattedFaqsToSearch.filter {
                            it.question.question.contains(
                                s,
                                ignoreCase = true,
                            ) or it.question.answer.contains(
                                s,
                                ignoreCase = true,
                            ) or it.topicName.contains(
                                s,
                                ignoreCase = true,
                            )
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // Do nothing
                }
            })
        }
    }

    private val onErrorObserver = Observer<String> { error ->
        with(binding) {
            faqGroupsRecycler.visibility = View.GONE

            errorOccurredView.root.visibility = View.VISIBLE
            errorOccurredView.errorText.text = error
            errorOccurredView.errorRetryButton.setOnClickListener {
                viewModel.loadFaqs()
            }
        }
    }
}