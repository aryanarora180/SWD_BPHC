package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_FUNDRAISER
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_TICKET
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_TSHIRT
import `in`.ac.bits_hyderabad.swd.swd.data.GoodieOrderDetails
import `in`.ac.bits_hyderabad.swd.swd.data.SingleLiveEvent
import `in`.ac.bits_hyderabad.swd.swd.databinding.BuyGoodieFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.goodies.BuyGoodieViewModel
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyGoodieFragment : Fragment() {

    private val binding by viewBinding<BuyGoodieFragmentBinding>()
    private val viewModel by viewModels<BuyGoodieViewModel>()
    private val args by navArgs<BuyGoodieFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goodie = args.GoodieData

        with(binding) {
            childFragmentManager.beginTransaction().apply {
                add(R.id.goodie_images, MultipleImagesFragment.newInstance(goodie.images))
                commit()
            }

            goodieNameText.text = goodie.name
            goodieHosterText.text = goodie.hostOrganization
            goodiePriceText.text = goodie.getDisplayPrice()

            when (goodie.type) {
                GOODIE_TYPE_TSHIRT -> setTypeTShirt()
                GOODIE_TYPE_TICKET -> setTypeTicket()
                GOODIE_TYPE_FUNDRAISER -> setTypeFundraiser()
            }

            if (viewModel.isHoster(goodie)) {
                viewSalesImage.isVisible = true
                viewSalesImage.setOnClickListener {
                    val action =
                        BuyGoodieFragmentDirections.actionBuyGoodieFragmentToViewSalesFragment(
                            goodie
                        )
                    findNavController().navigate(action)
                }
            }

            orderButton.setOnClickListener {
                viewModel.getOrderDetails()?.let {
                    proceedWithOrder(it)
                }
            }
        }

        with(viewModel) {
            quantity.observe(viewLifecycleOwner, quantityObserver)
            amount.observe(viewLifecycleOwner, priceObserver)
            onMessageError.observe(viewLifecycleOwner, onMessageErrorObserver)
        }
    }

    private fun setTypeTShirt() {
        with(binding) {
            selectSizesButton.isVisible = true

            viewModel.sizesSelected.observe(viewLifecycleOwner) {
                selectSizesButton.text = it.getSizesString()
                viewModel.updateSizes(it)
            }

            selectSizesButton.setOnClickListener {
                EnterSizeBottomSheet(viewModel).show(
                    requireActivity().supportFragmentManager,
                    "select_size"
                )
            }
        }
    }

    private fun setTypeTicket() {
        with(binding) {
            quantityText.isVisible = true
            goodieLimitText.isVisible = true
            decreaseQuantityFab.isVisible = true
            quantityAmountText.isVisible = true
            increaseQuantityFab.isVisible = true

            if (args.GoodieData.limit == 0) {
                goodieLimitText.visibility = View.GONE
            } else {
                goodieLimitText.text =
                    getString(R.string.order_limit, args.GoodieData.limit.toString())
            }

            increaseQuantityFab.setOnClickListener { viewModel.increaseQuantity() }
            decreaseQuantityFab.setOnClickListener { viewModel.decreaseQuantity() }
        }
    }

    private fun setTypeFundraiser() {
        with(binding) {
            amountInput.isVisible = true

            amountEdit.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    viewModel.validateFundraiserAmount(binding.amountEdit.text)
                    true
                } else {
                    false
                }
            }

            amountEdit.addTextChangedListener {
                viewModel.validateFundraiserAmount(
                    binding.amountEdit.text
                )
            }

            viewModel.onInvalidFundraiserAmount.observe(
                viewLifecycleOwner,
                onInvalidFundraiserAmountObserver
            )
        }
    }

    private val priceObserver = Observer<Int> {
        binding.orderTotalText.text = viewModel.formatAmount(it)
    }

    private val quantityObserver = Observer<Int> {
        binding.quantityAmountText.text = it.toString()
    }

    private val onMessageErrorObserver = Observer<SingleLiveEvent<String>> {
        binding.buyGoodieCoordinator.showSnackbarError(it)
    }

    private val onInvalidFundraiserAmountObserver = Observer<String?> {
        binding.amountInput.error = it
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.amountEdit.windowToken, 0)
    }

    private fun proceedWithOrder(orderDetails: GoodieOrderDetails) {
        val intent = Intent(requireActivity(), BuyGoodieConfirmationFragment::class.java)
        intent.apply {
            putExtra(INTENT_EXTRA_GOODIE_DATA, args.GoodieData)
            putExtra(INTENT_EXTRA_ORDER_DATA, orderDetails)
        }
        startActivity(intent)
    }

    companion object {
        const val INTENT_EXTRA_GOODIE_DATA = "goodie_data"
        const val INTENT_EXTRA_ORDER_DATA = "order_data"
    }
}