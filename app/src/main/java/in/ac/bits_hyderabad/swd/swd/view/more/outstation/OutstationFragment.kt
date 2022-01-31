package `in`.ac.bits_hyderabad.swd.swd.view.more.outstation

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.Outstation
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.OutstationAdapter
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.OutstationViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.internet_error_view.*
import kotlinx.android.synthetic.main.outstation_fragment.*

@AndroidEntryPoint
class OutstationFragment : Fragment(R.layout.outstation_fragment) {

    private val viewModel by viewModels<OutstationViewModel>()

    private val outstationAdapter = OutstationAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apply_outstation_button.setOnClickListener {
            findNavController().navigate(R.id.action_outstationFragment_to_requestOutstationFragment)
        }

        outstationAdapter.listener = { outstationToDelete ->
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.cancel_outstation)
                setMessage(R.string.cancel_outstation_message)
                setPositiveButton(R.string.cancel_positive) { _, _ ->
                    viewModel.cancelOutstation(outstationToDelete.outstationId)
                }
                setNeutralButton(R.string.cancel_neutral) { _, _ ->
                    // Do nothing
                }
                show()
            }
        }
        outstation_recycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<OutstationAdapter.ViewHolder>()
            adapter = outstationAdapter
        }

        outstations_swipe_refresh.setOnRefreshListener {
            viewModel.loadOutstations()
            outstations_swipe_refresh.isRefreshing = false
        }

        viewModel.outstations.observe(viewLifecycleOwner, outstationsObserver)
        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
        viewModel.onCancelMessageError.observe(viewLifecycleOwner, onCancelErrorMessageObserver)
    }

    private val outstationsObserver = Observer<List<Outstation>> { result ->
        outstations_swipe_refresh.visibility = View.VISIBLE
        no_outstations_text.visibility = View.GONE
        no_outstations_image.visibility = View.GONE

        outstationAdapter.data = result
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            outstation_progress.visibility = View.VISIBLE
            outstations_swipe_refresh.visibility = View.GONE
            no_outstations_text.visibility = View.GONE
            no_outstations_text.visibility = View.GONE
            no_outstations_image.visibility = View.GONE
            error_occurred_view.visibility = View.GONE
        } else {
            outstation_progress.visibility = View.GONE
            outstations_swipe_refresh.isRefreshing = false
            outstation_recycler.visibility = View.VISIBLE
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        if (isEmpty) {
            outstations_swipe_refresh.visibility = View.GONE
            no_outstations_text.visibility = View.VISIBLE
            no_outstations_image.visibility = View.VISIBLE
        } else {
            outstations_swipe_refresh.visibility = View.VISIBLE
            no_outstations_text.visibility = View.GONE
            no_outstations_image.visibility = View.GONE
        }
    }

    private val onErrorMessageObserver = Observer<String> { error ->
        error_occurred_view.visibility = View.VISIBLE
        error_text.text = error
        error_retry_button.setOnClickListener {
            viewModel.loadOutstations()
        }
    }

    private val onCancelErrorMessageObserver = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }
}