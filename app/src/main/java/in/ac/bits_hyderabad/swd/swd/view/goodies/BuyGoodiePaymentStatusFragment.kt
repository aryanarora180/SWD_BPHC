package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentBuyGoodieStatusBinding
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.goodies.PlaceGoodieOrderViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

@AndroidEntryPoint
class BuyGoodiePaymentStatusFragment : Fragment() {

    private val binding by viewBinding<FragmentBuyGoodieStatusBinding>()
    private val viewModel by navGraphViewModels<PlaceGoodieOrderViewModel>(R.navigation.goodies_navigation)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val paymentSuccess = viewModel.otherAdvancesPaymentDetails
        if (viewModel.otherAdvancesPaymentDetails.isSuccessful) {
            setPaymentSuccess()
            binding.amountDeductedText.text =
                getString(R.string.amount_deducted, paymentSuccess.paymentAmount.toString())
        } else {
            setPaymentFailed()
        }
    }

    private fun setPaymentSuccess() {
        with(binding) {
            orderStatusImage.setImageResource(R.drawable.outline_check_circle_outline_24)
            orderStatusText.text = getString(R.string.order_success)
            statusMessageText.text = getString(R.string.payment_success_advances)

            amountDeductedText.isVisible = true

            confetti.build()
                .addColors(Color.parseColor("#3e87f6"))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square, Shape.Circle)
                .addSizes(Size(12), Size(16, 6f))
                .setPosition(
                    binding.confetti.x + binding.confetti.width / 2,
                    binding.confetti.y + binding.confetti.height / 3
                )
                .burst(250)

            binding.continueTryAgainButton.text = getString(R.string.order_continue)
            binding.continueTryAgainButton.setOnClickListener {
                // todo
            }
        }
    }

    private fun setPaymentFailed() {
        with(binding) {
            orderStatusImage.setImageResource(R.drawable.outline_error_outline_24)
            orderStatusText.text = getString(R.string.order_failed)
            statusMessageText.text = getString(R.string.payment_failure_advances)

            binding.continueTryAgainButton.text = getString(R.string.retry)
            binding.continueTryAgainButton.setOnClickListener {
                findNavController().navigate(R.id.action_buyGoodiePaymentStatusFragment_to_buyGoodieConfirmationFragment)
            }
        }
    }
}