package `in`.ac.bits_hyderabad.swd.swd.view.more.outstation

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.RequestOutstationViewModel
import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.request_outstation_fragment.*

@AndroidEntryPoint
class RequestOutstationFragment : Fragment(R.layout.request_outstation_fragment) {

    private val viewModel by viewModels<RequestOutstationViewModel>()

    private var _fromDate = 0L
    private var _toDate = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datesPicker: MaterialDatePicker<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker().build()
        dates_layout.setEndIconOnClickListener {
            datesPicker.show(parentFragmentManager, datesPicker.toString())
        }

        val datesEdit = dates_edit
        datesPicker.addOnPositiveButtonClickListener { datesPair ->
            _fromDate = datesPair.first ?: 0L
            _toDate = datesPair.second ?: 0L
            datesEdit.setText(viewModel.getFormattedDates(_fromDate, _toDate))
        }

        request_button.setOnClickListener {
            if (validateData()) {
                showConfirmationDialog()
            }
        }

        viewModel.isSendingRequest.observe(viewLifecycleOwner, isSendingRequestObserver)
        viewModel.outstationRequestError.observe(viewLifecycleOwner, outstationRequestErrorObserver)
        viewModel.outstationRequestSuccess.observe(
            viewLifecycleOwner,
            outstationRequestSuccessObserver
        )
    }

    private val isSendingRequestObserver = Observer<Boolean> { isSending ->
        if (isSending) {
            request_progress.visibility = View.VISIBLE
            request_button.text = ""
            request_button.isEnabled = false
            dates_layout.isEnabled = false
            reason_layout.isEnabled = false
            location_layout.isEnabled = false
        } else {
            request_progress.visibility = View.GONE
            request_button.text = getString(R.string.request)
            request_button.isEnabled = true
            dates_layout.isEnabled = true
            reason_layout.isEnabled = true
            location_layout.isEnabled = true
        }
    }

    private val outstationRequestErrorObserver = Observer<LiveErrorEvent> { error ->
        error.getContentIfNotHandled()?.let {
            Snackbar.make(request_outstation_coordinator, it, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private val outstationRequestSuccessObserver = Observer<Boolean> { isSuccess ->
        if (isSuccess) {
            findNavController().navigate(R.id.action_requestOutstationFragment_to_outstationFragment)
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.confirm_outstation_title)
            setMessage(R.string.confirm_outstation_body)
            setNeutralButton(R.string.confirm_neutral) { _, _ ->
                // Do nothing
            }
            setPositiveButton(R.string.confirm_positive) { _, _ ->
                viewModel.sendOutstationRequest(
                    _fromDate,
                    _toDate,
                    reason_edit.text.toString().trim(),
                    location_edit.text.toString().trim()
                )
            }
            show()
        }
    }

    private fun validateData(): Boolean {
        if (_fromDate == 0L || _toDate == 0L) {
            showSnackbar(R.string.error_outstation_date_range_not_selected)
            return false
        }
        if (dates_edit.text.isNullOrEmpty()) {
            showSnackbar(R.string.error_outstation_date_range_not_selected)
            return false
        }
        if (location_edit.text.isNullOrEmpty()) {
            showSnackbar(R.string.error_outstation_location_empty)
            return false
        }
        if (reason_edit.text.isNullOrEmpty()) {
            showSnackbar(R.string.error_outstation_reason_empty)
            return false
        }
        return true
    }

    private fun showSnackbar(textId: Int) =
        Snackbar.make(request_outstation_coordinator, textId, Snackbar.LENGTH_SHORT)
            .show()

}