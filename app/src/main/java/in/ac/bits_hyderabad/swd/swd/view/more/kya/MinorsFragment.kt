package `in`.ac.bits_hyderabad.swd.swd.view.more.kya

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.MinorData
import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentKyaGeneralBinding
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs.FaqsTopicDecoration
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya.KyaMinorsAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.KyaViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MinorsFragment : Fragment() {

    private lateinit var _binding: FragmentKyaGeneralBinding
    private val viewModel by activityViewModels<KyaViewModel>()

    private val kyaMinorsAdapter = KyaMinorsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKyaGeneralBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.kya.value?.kya?.getRecyclerFormattedMinors()?.let {
            kyaMinorsAdapter.data = it
            _binding.kyaGeneralRecycler.apply {
                adapter = kyaMinorsAdapter
                if (itemDecorationCount == 0) {
                    val decoration = FaqsTopicDecoration(
                        requireContext(),
                        resources.getDimensionPixelSize(R.dimen.header_height),
                        getSectionCallback(it)
                    )
                    addItemDecoration(decoration)
                }
            }
        }
    }

    private fun getSectionCallback(list: List<Pair<String, MinorData>>): FaqsTopicDecoration.SectionCallback {
        return object : FaqsTopicDecoration.SectionCallback {
            override fun isSectionHeader(pos: Int): Boolean {
                return pos == 0 || list[pos].first != list[pos - 1].first
            }

            override fun getSectionHeaderName(pos: Int): String {
                return list[pos].first
            }
        }
    }
}

