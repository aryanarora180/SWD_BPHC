package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.data.GoodiesSizes
import `in`.ac.bits_hyderabad.swd.swd.databinding.BottomSheetSelectSizeBinding
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.goodies.BuyGoodieViewModel
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterSizeBottomSheet(private val viewModel: BuyGoodieViewModel) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSelectSizeBinding

    private val sizesSelected = viewModel.sizesSelected.value!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSelectSizeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateSizes()

        with(binding) {
            val sizes = viewModel.goodie.sizesAvailable
            if (sizes.contains(GoodiesSizes.SIZE_XS))
                xsConstraintLayout.isVisible = true
            if (sizes.contains(GoodiesSizes.SIZE_S))
                sConstraintLayout.isVisible = true
            if (sizes.contains(GoodiesSizes.SIZE_M))
                mConstraintLayout.isVisible = true
            if (sizes.contains(GoodiesSizes.SIZE_L))
                lConstraintLayout.isVisible = true
            if (sizes.contains(GoodiesSizes.SIZE_XL))
                xlConstraintLayout.isVisible = true
            if (sizes.contains(GoodiesSizes.SIZE_XXL))
                xxlConstraintLayout.isVisible = true
            if (sizes.contains(GoodiesSizes.SIZE_XXXL))
                xxxlConstraintLayout.isVisible = true
        }

        val incrementButtons = listOf(
            binding.increaseXsQuantityFab,
            binding.increaseSQuantityFab,
            binding.increaseMQuantityFab,
            binding.increaseLQuantityFab,
            binding.increaseXlQuantityFab,
            binding.increaseXxlQuantityFab,
            binding.increaseXxxlQuantityFab
        )
        val decrementButtons = listOf(
            binding.decreaseXsQuantityFab,
            binding.decreaseSQuantityFab,
            binding.decreaseMQuantityFab,
            binding.decreaseLQuantityFab,
            binding.decreaseXlQuantityFab,
            binding.decreaseXxlQuantityFab,
            binding.decreaseXxxlQuantityFab
        )

        incrementButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                with(sizesSelected) {
                    when (index) {
                        0 -> xs += 1
                        1 -> s += 1
                        2 -> m += 1
                        3 -> l += 1
                        4 -> xl += 1
                        5 -> xxl += 1
                        6 -> xxxl += 1
                    }
                    updateSizes()
                }
            }
        }

        decrementButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                with(sizesSelected) {
                    when (index) {
                        0 -> if (xs > 0) xs -= 1
                        1 -> if (s > 0) s -= 1
                        2 -> if (m > 0) m -= 1
                        3 -> if (l > 0) l -= 1
                        4 -> if (xl > 0) xl -= 1
                        5 -> if (xxl > 0) xxl -= 1
                        6 -> if (xxxl > 0) xxxl -= 1
                    }
                    updateSizes()
                }
            }
            button.setOnLongClickListener {
                with(sizesSelected) {
                    when (index) {
                        0 -> xs = 0
                        1 -> s = 0
                        2 -> m = 0
                        3 -> l = 0
                        4 -> xl = 0
                        5 -> xxl = 0
                        6 -> xxxl = 0
                    }
                    updateSizes()
                    return@setOnLongClickListener true
                }
            }
        }
    }

    private fun updateSizes() {
        with(binding) {
            with(sizesSelected) {
                xsQuantityAmountText.text = xs.toString()
                sQuantityAmountText.text = s.toString()
                mQuantityAmountText.text = m.toString()
                lQuantityAmountText.text = l.toString()
                xlQuantityAmountText.text = xl.toString()
                xxlQuantityAmountText.text = xxl.toString()
                xxxlQuantityAmountText.text = xxxl.toString()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.sizesSelected.value = sizesSelected
    }
}