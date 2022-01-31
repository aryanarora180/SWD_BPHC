package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.SingleLiveEvent
import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentBuyGoodieConfirmDetailsBinding
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.goodies.PlaceGoodieOrderViewModel
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyGoodieConfirmationFragment : Fragment() {

    private val binding by viewBinding<FragmentBuyGoodieConfirmDetailsBinding>()
    private val viewModel by navGraphViewModels<PlaceGoodieOrderViewModel>(R.navigation.goodies_navigation)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            goodieNameText.text = viewModel.goodieData.name
            goodieHostText.text = viewModel.goodieData.hostOrganization
            goodiePriceText.text =
                getString(R.string.rupee_amount, viewModel.goodieData.price.toString())
            goodieQuantityText.text = viewModel.orderDetails.netQuantity.toString()
            goodieSizesText.text =
                getString(R.string.deductions_sizes, viewModel.orderDetails.sizes.getSizesString())
            totalAmountText.text =
                getString(R.string.rupee_amount, viewModel.orderDetails.totalAmount.toString())
        }

        binding.payButton.setOnClickListener { viewModel.placeOrder() }

        with(viewModel) {
            onMessageError.observe(viewLifecycleOwner, onMessageErrorObserver)
            isPlacingOrder.observe(viewLifecycleOwner, loadingObserver)
            placeOrderSuccess.observe(viewLifecycleOwner, otherAdvancesOrderSuccessObserver)
        }
    }

    private val loadingObserver = Observer<Boolean> { isLoading -> setLoadState(isLoading) }

    private val onMessageErrorObserver = Observer<SingleLiveEvent<String>> {
        binding.root.showSnackbarError(it)
    }

    private fun setLoadState(isLoading: Boolean) {
        with(binding) {
            payButton.isEnabled = !isLoading
            orderProgress.isVisible = isLoading
        }
    }

    private val otherAdvancesOrderSuccessObserver =
        Observer<SingleLiveEvent<Boolean>> { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                findNavController().navigate(R.id.action_buyGoodieConfirmationFragment_to_buyGoodiePaymentStatusFragment)
            }
        }
}