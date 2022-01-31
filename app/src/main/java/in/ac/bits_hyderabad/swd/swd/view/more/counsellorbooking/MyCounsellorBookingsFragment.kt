package `in`.ac.bits_hyderabad.swd.swd.view.more.counsellorbooking

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.CounsellorBooking
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.MyBookingsAdapter
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.CounsellorBookingViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.internet_error_view.*
import kotlinx.android.synthetic.main.my_bookings_fragment.*

@AndroidEntryPoint
class MyCounsellorBookingsFragment(private val viewModel: CounsellorBookingViewModel) :
    Fragment(R.layout.my_bookings_fragment) {

    private val myBookingsAdapter = MyBookingsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myBookingsAdapter.listener = { bookingId ->
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.delete_booking_title)
                setMessage(R.string.delete_booking_message)
                setPositiveButton(R.string.delete_booking_positive) { _, _ ->
                    viewModel.deleteRequest(bookingId)
                }
                setNeutralButton(R.string.delete_booking_neutral) { _, _ ->
                    // Do nothing
                }
                show()
            }
        }

        my_bookings_recycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<MyBookingsAdapter.ViewHolder>()
            adapter = myBookingsAdapter
        }

        viewModel.myBookings.observe(viewLifecycleOwner, slotsObserver)
        viewModel.slotDeleteError.observe(viewLifecycleOwner, slotRequestErrorObserver)
        viewModel.onMyBookingsMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
        viewModel.isMyBookingsLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.isMyBookingsEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
    }

    private val slotsObserver = Observer<List<CounsellorBooking>> { slots ->
        my_bookings_recycler.visibility = View.VISIBLE
        myBookingsAdapter.data = slots
    }

    private val onErrorMessageObserver = Observer<String> { error ->
        slots_error_occurred_view.visibility = View.VISIBLE
        my_bookings_recycler.visibility = View.GONE
        no_slots_image.visibility = View.GONE
        no_slots_text.visibility = View.GONE
        error_text.text = error
        error_retry_button.setOnClickListener {
            viewModel.loadMyBookings()
        }
    }

    private val slotRequestErrorObserver = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            my_bookings_progress.visibility = View.VISIBLE
            my_bookings_recycler.visibility = View.GONE
            no_slots_image.visibility = View.GONE
            no_slots_text.visibility = View.GONE
            slots_error_occurred_view.visibility = View.GONE
        } else {
            my_bookings_progress.visibility = View.GONE
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        if (isEmpty) {
            my_bookings_recycler.visibility = View.GONE
            no_slots_image.visibility = View.VISIBLE
            no_slots_text.visibility = View.VISIBLE
        } else {
            my_bookings_recycler.visibility = View.VISIBLE
            no_slots_image.visibility = View.GONE
            no_slots_text.visibility = View.GONE
        }
    }
}