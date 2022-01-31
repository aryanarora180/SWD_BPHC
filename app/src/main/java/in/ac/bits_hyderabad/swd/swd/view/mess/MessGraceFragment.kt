package `in`.ac.bits_hyderabad.swd.swd.view.mess

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Grace
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.MessGraceAdapter
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.MessGraceViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.internet_error_view.*
import kotlinx.android.synthetic.main.mess_grace_fragment.*

@AndroidEntryPoint
class MessGraceFragment : Fragment(R.layout.mess_grace_fragment) {

    private val viewModel by viewModels<MessGraceViewModel>()

    private val graceAdapter = MessGraceAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grace_recycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<MessGraceAdapter.ViewHolder>()
            adapter = graceAdapter
        }

        val picker: MaterialDatePicker<Long> = MaterialDatePicker.Builder.datePicker().build()
        apply_grace_button.setOnClickListener {
            picker.show(parentFragmentManager, picker.toString())
        }

        picker.addOnPositiveButtonClickListener { date ->
            viewModel.sendMessGraceRequest(date)
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
        viewModel.graces.observe(viewLifecycleOwner, gracesObserver)
        viewModel.graceRequestError.observe(viewLifecycleOwner, graceRequestError)
    }

    private val graceRequestError = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }

    private val gracesObserver = Observer<List<Grace>> { graces ->
        grace_recycler.visibility = View.VISIBLE
        graceAdapter.data = graces
    }

    private val onErrorMessageObserver = Observer<String> { error ->
        error_occurred_view.visibility = View.VISIBLE
        grace_recycler.visibility = View.GONE
        error_text.text = error
        error_retry_button.setOnClickListener {
            viewModel.loadGraces()
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            grace_progress.visibility = View.VISIBLE
            grace_recycler.visibility = View.GONE
            no_graces_text.visibility = View.GONE
            no_graces_image.visibility = View.GONE
            error_occurred_view.visibility = View.GONE
        } else {
            grace_recycler.visibility = View.VISIBLE
            grace_progress.visibility = View.GONE
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        if (isEmpty) {
            no_graces_text.visibility = View.VISIBLE
            no_graces_image.visibility = View.VISIBLE
        } else {
            no_graces_text.visibility = View.GONE
            no_graces_image.visibility = View.GONE
        }
    }
}