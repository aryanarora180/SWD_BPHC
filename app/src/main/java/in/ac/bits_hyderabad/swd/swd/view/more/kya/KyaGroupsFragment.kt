package `in`.ac.bits_hyderabad.swd.swd.view.more.kya

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Kya
import `in`.ac.bits_hyderabad.swd.swd.data.KyaData.Companion.KYA_COURSE_GUIDES
import `in`.ac.bits_hyderabad.swd.swd.data.KyaData.Companion.KYA_DEPTS
import `in`.ac.bits_hyderabad.swd.swd.data.KyaData.Companion.KYA_GEN_ELECTIVES
import `in`.ac.bits_hyderabad.swd.swd.data.KyaData.Companion.KYA_MINORS
import `in`.ac.bits_hyderabad.swd.swd.databinding.KyaGroupsFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya.KyaGroupsAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.KyaViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KyaGroupsFragment : Fragment() {

    private lateinit var _binding: KyaGroupsFragmentBinding
    private val viewModel by activityViewModels<KyaViewModel>()

    private val kyaGroupsAdapter = KyaGroupsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = KyaGroupsFragmentBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kyaGroupsAdapter.listener = { groupName ->
            when (groupName) {
                KYA_GEN_ELECTIVES -> findNavController().navigate(R.id.action_kyaFragment_to_generalFragment)
                KYA_MINORS -> findNavController().navigate(R.id.action_kyaFragment_to_minorsFragment)
                KYA_DEPTS -> findNavController().navigate(R.id.action_kyaFragment_to_kyaDepartmentsFragment)
                KYA_COURSE_GUIDES -> findNavController().navigate(R.id.action_kyaFragment_to_courseYearsFragment)
            }
        }
        _binding.kyaRecycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<KyaGroupsAdapter.ViewHolder>()
            adapter = kyaGroupsAdapter
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
        viewModel.kya.observe(viewLifecycleOwner, faqGroupsObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorObserver)
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(_binding) {
            if (isLoading) {
                kyaProgress.visibility = View.VISIBLE
                errorOccurredView.root.visibility = View.GONE
                kyaRecycler.visibility = View.GONE
                kyaLastUpdatedText.visibility = View.GONE
            } else {
                kyaProgress.visibility = View.GONE
            }
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        with(_binding) {
            if (isEmpty) {
                kyaRecycler.visibility = View.GONE
                kyaLastUpdatedText.visibility = View.GONE
                noKyaImage.visibility = View.VISIBLE
                noKyaText.visibility = View.VISIBLE
            } else {
                kyaRecycler.visibility = View.VISIBLE
                kyaLastUpdatedText.visibility = View.VISIBLE
                noKyaImage.visibility = View.GONE
                noKyaText.visibility = View.GONE
            }
        }
    }

    private val faqGroupsObserver = Observer<Kya> { faqs ->
        with(_binding) {
            kyaRecycler.visibility = View.VISIBLE
            kyaLastUpdatedText.text = viewModel.formatLastUpdated(faqs.lastUpdated)
            kyaLastUpdatedText.visibility = View.VISIBLE
        }
    }

    private val onErrorObserver = Observer<String> { error ->
        with(_binding) {
            errorOccurredView.root.visibility = View.VISIBLE
            kyaRecycler.visibility = View.GONE
            errorOccurredView.errorText.text = error
            errorOccurredView.errorRetryButton.setOnClickListener {
                viewModel.loadKya()
            }
        }
    }
}