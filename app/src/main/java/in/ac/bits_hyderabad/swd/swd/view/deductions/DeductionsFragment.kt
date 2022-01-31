package `in`.ac.bits_hyderabad.swd.swd.view.deductions

import `in`.ac.bits_hyderabad.swd.swd.data.Deduction
import `in`.ac.bits_hyderabad.swd.swd.databinding.DeductionsFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.DeductionsAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.DeductionsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeductionsFragment : Fragment() {

    private lateinit var binding: DeductionsFragmentBinding
    private val viewModel by viewModels<DeductionsViewModel>()

    private val deductionsAdapter = DeductionsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DeductionsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deductionsAdapter.listener = { deduction ->
            val action =
                DeductionsFragmentDirections.actionDeductionsFragmentToDeductionDetailsFragment(
                    deduction
                )
            findNavController().navigate(action)
        }

        with(binding) {
            deductionsRecycler.apply {
                edgeEffectFactory =
                    SpringyRecycler.springEdgeEffectFactory<DeductionsAdapter.ViewHolder>()
                adapter = deductionsAdapter
            }

            totalDeductionsAmountTicker.setCharacterLists(TickerUtils.provideNumberList())
            totalDeductionsAmountTicker.text = 0.toString()
        }

        with(viewModel) {
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
            isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
            deductions.observe(viewLifecycleOwner, deductionsObserver)
            amountSpent.observe(viewLifecycleOwner, amountSpentObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            deductionsErrorOccurredView.root.isGone = true
            deductionsProgress.isVisible = isLoading
            deductionsRecycler.isGone = isLoading
            totalDeductionsSpentHeaderText.isGone = isLoading
            totalDeductionsAmountTicker.isGone = isLoading
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        with(binding) {
            if (isEmpty) {
                noDeductionsText.isVisible = true
                noDeductionsImage.isVisible = true
                totalDeductionsAmountTicker.isVisible = true
                totalDeductionsSpentHeaderText.isVisible = true
            }
        }
    }

    private val deductionsObserver = Observer<List<Deduction>> { deductions ->
        with(binding) {
            noDeductionsText.isGone = true
            noDeductionsImage.isGone = true
            deductionsProgress.isGone = true
            deductionsRecycler.isVisible = true
            totalDeductionsAmountTicker.isVisible = true
            totalDeductionsSpentHeaderText.isVisible = true

            deductionsAdapter.data = deductions
        }
    }

    private val amountSpentObserver = Observer<String> { amountSpent ->
        binding.totalDeductionsAmountTicker.text = amountSpent
    }

    private val onErrorObserver = Observer<String> { error ->
        with(binding) {
            deductionsErrorOccurredView.root.isVisible = true
            deductionsErrorOccurredView.errorText.text = error
            totalDeductionsSpentHeaderText.visibility = View.GONE
            totalDeductionsAmountTicker.isGone = true
            deductionsErrorOccurredView.errorRetryButton.setOnClickListener {
                viewModel.loadDeductions()
            }
        }
    }
}