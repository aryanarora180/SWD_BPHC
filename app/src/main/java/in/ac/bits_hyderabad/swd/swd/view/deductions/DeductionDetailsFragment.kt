package `in`.ac.bits_hyderabad.swd.swd.view.deductions

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Deduction
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_FUNDRAISER
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_TICKET
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.databinding.DeductionDetailsFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.DeductionDetailsViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeductionDetailsFragment : Fragment() {

    private lateinit var binding: DeductionDetailsFragmentBinding
    private val viewModel by activityViewModels<DeductionDetailsViewModel>()
    private val args by navArgs<DeductionDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DeductionDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.deduction
        when (order.type) {
            GOODIE_TYPE_TICKET -> setTypeTicket()
            GOODIE_TYPE_FUNDRAISER -> setTypeFundraiser()
        }

        with(binding) {
            orderGoodieNameText.text = order.goodieName
            orderGoodieHostText.text = order.hostOrganization
            orderIdText.text = getString(R.string.transaction_id, order.transactionId)
            orderPlacedText.text = getString(
                R.string.transaction_date,
                viewModel.getDate(order.purchaseDate),
                viewModel.getTime(order.purchaseDate)
            )

            orderGoodiePriceText.text = order.getDisplayPrice()
            orderGoodieQuantityText.text = order.quantity.toString()
            binding.orderGoodieSizesText.text =
                getString(R.string.deductions_sizes, order.sizes.getSizesString())
            deductionTotalAmountText.text = order.getDisplayPrice()
        }

        if (order.isCancellable == Deduction.DEDUCTION_CANCELLABLE) {
            binding.cancelButton.visibility = View.VISIBLE
            binding.cancelButton.setOnClickListener { cancelOrder() }
        }
    }

    private fun cancelOrder() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.cancel_order)
            setMessage(R.string.cancel_message)
            setPositiveButton(R.string.cancel_positive) { _, _ ->
                viewModel.cancelDeduction(TODO("Goodie ID is now a String and not an Int"))
                viewModel.isCanceling.observe(viewLifecycleOwner, isCancelingObserver)
                viewModel.onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
                viewModel.cancelSuccessful.observe(
                    viewLifecycleOwner,
                    cancelSuccessfulObserver
                )
            }
            setNeutralButton(R.string.cancel_neutral) { _, _ ->
                //Do nothing
            }
            show()
        }
    }

    private val cancelSuccessfulObserver = Observer<Boolean> { canceled ->
        if (canceled) {
            findNavController().navigate(R.id.action_deductionDetailsFragment_to_deductionsFragment)
        }
    }

    private val onErrorMessageObserver = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }

    private val isCancelingObserver = Observer<Boolean> { isCanceling ->
        with(binding) {
            deductionDetailsConstraint.isGone = isCanceling
            cancelingDeductionProgress.isVisible = isCanceling
        }
    }

    private fun setTypeTicket() {
        binding.orderGoodieSizesText.isGone = true
    }

    private fun setTypeFundraiser() {
        with(binding) {
            orderGoodiePricePrompt.isGone = true
            orderGoodiePriceText.isGone = true
            orderGoodieSizesText.isGone = true
        }
    }
}