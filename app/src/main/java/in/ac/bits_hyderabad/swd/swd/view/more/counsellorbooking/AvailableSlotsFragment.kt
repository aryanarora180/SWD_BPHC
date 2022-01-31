package `in`.ac.bits_hyderabad.swd.swd.view.more.counsellorbooking

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.CounsellorSlot
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.AvailableSlotAdapter
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.CounsellorBookingViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.available_slots_fragment.*
import kotlinx.android.synthetic.main.internet_error_view.*

@AndroidEntryPoint
class AvailableSlotsFragment(private val viewModel: CounsellorBookingViewModel) :
    Fragment(R.layout.available_slots_fragment) {

    private val availableSlotAdapter = AvailableSlotAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        availableSlotAdapter.listener = { slotToBook ->
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.book_slot_title)
                setMessage(
                    getString(
                        R.string.book_slot_message,
                        viewModel.formatSlotTiming(slotToBook)
                    )
                )
                setPositiveButton(R.string.book_slot_positive) { _, _ ->
                    viewModel.sendBookingRequest(slotToBook)
                }
                setNeutralButton(R.string.book_slot_neutral) { _, _ ->
                    // Do nothing
                }
                show()
            }
        }

        available_slots_recycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<AvailableSlotAdapter.ViewHolder>()
            adapter = availableSlotAdapter
        }

        viewModel.availableSlots.observe(viewLifecycleOwner, slotsObserver)
        viewModel.slotRequestError.observe(viewLifecycleOwner, slotRequestErrorObserver)
        viewModel.onAvailableSlotsMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
        viewModel.isAvailableSlotsLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.isAvailableSlotsEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
    }

    private val slotsObserver = Observer<List<CounsellorSlot>> { slots ->
        available_slots_recycler.visibility = View.VISIBLE
        availableSlotAdapter.data = slots
    }

    private val onErrorMessageObserver = Observer<String> { error ->
        slots_error_occurred_view.visibility = View.VISIBLE
        available_slots_recycler.visibility = View.GONE
        no_slots_image.visibility = View.GONE
        no_slots_text.visibility = View.GONE
        error_text.text = error
        error_retry_button.setOnClickListener {
            viewModel.loadAvailableSlots()
        }
    }

    private val slotRequestErrorObserver = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            available_slots_progress.visibility = View.VISIBLE
            available_slots_recycler.visibility = View.GONE
            no_slots_image.visibility = View.GONE
            no_slots_text.visibility = View.GONE
            slots_error_occurred_view.visibility = View.GONE
        } else {
            available_slots_progress.visibility = View.GONE
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        if (isEmpty) {
            available_slots_recycler.visibility = View.GONE
            no_slots_image.visibility = View.VISIBLE
            no_slots_text.visibility = View.VISIBLE
        } else {
            available_slots_recycler.visibility = View.VISIBLE
            no_slots_image.visibility = View.GONE
            no_slots_text.visibility = View.GONE
        }
    }
}