package `in`.ac.bits_hyderabad.swd.swd.view.mess

import `in`.ac.bits_hyderabad.swd.swd.R
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.mess_registration_fragment.*

@AndroidEntryPoint
class MessRegistrationFragment : Fragment(R.layout.mess_registration_fragment) {

//    private val viewModel by activityViewModels<MessRegistrationViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMessRegistrationClosed()

        mess_registration_swipe_refresh.setOnRefreshListener {
            hideCards()
            mess_registration_swipe_refresh.isRefreshing = false
            setMessRegistrationClosed()
        }
//        checkRegistrationStatus()
//        binding.messRegistrationSwipeRefresh.setOnRefreshListener {
//            checkRegistrationStatus()
//        }
    }

//    private fun checkRegistrationStatus() {
//        _snackbar?.dismiss()
//        setLoading()
//        viewModel.getRegistrationStatus().observe(viewLifecycleOwner, Observer { currentState ->
//            if (!viewModel.registrationStatusIsLoading) {
//                binding.messRegistrationSwipeRefresh.isRefreshing = false
//                if (currentState != null) {
//                    setMessRegistrationOpen(currentState)
//                } else {
//                    if (viewModel.registrationStatusError == Entities.STATUS_502_BAD_GATEWAY) {
//                        setMessRegistrationClosed()
//                    } else {
//                        hideCards()
//                        showSnackbar(viewModel.getRegistrationStatusErrorMessageStringId())
//                    }
//                }
//            }
//        })
//    }
//
//    private var _snackbar: Snackbar? = null
//    private fun showSnackbar(@StringRes message: Int) {
//        _snackbar =
//            Snackbar.make(binding.messRegistrationCoordinator, message, Snackbar.LENGTH_INDEFINITE)
//        _snackbar?.show()
//    }
//
//    private fun showSnackbar(message: String) {
//        _snackbar =
//            Snackbar.make(binding.messRegistrationCoordinator, message, Snackbar.LENGTH_INDEFINITE)
//        _snackbar?.show()
//    }
//
//    private fun setMessRegistrationOpen(currentState: Entities.MessRegCurrentState) {
//        binding.registerMessCard.visibility = View.VISIBLE
//        binding.registerMessClosedCard.visibility = View.GONE
//
//        binding.messOneSeatsText.text = getString(R.string.seats_left, currentState.Mess1left)
//        binding.messTwoSeatsText.text = getString(R.string.seats_left, currentState.Mess2left)
//
//        if (currentState.Mess1left.toInt() == 0)
//            binding.messOneRadio.isEnabled = false
//        if (currentState.Mess2left.toInt() == 0)
//            binding.messTwoRadio.isEnabled = false
//
//        binding.registerButton.setOnClickListener {
//            setRegisteringForMess()
//            var messToRegister = 2
//            if (binding.messChoiceRadiogroup.checkedRadioButtonId == R.id.mess_one_radio)
//                messToRegister = 1
//            viewModel.registerToMess(messToRegister)
//                .observe(viewLifecycleOwner, Observer { messRegistrationResponse ->
//                    if (messRegistrationResponse != null) {
//                        setNotRegisteringForMess()
//                        if (messRegistrationResponse.Pass == "0") {
//                            showSnackbar(
//                                getString(
//                                    R.string.register_successful,
//                                    messRegistrationResponse.Mess
//                                )
//                            )
//                        } else {
//                            showSnackbar(R.string.error_mess_registration)
//                        }
//
//                        if (currentState.Mess1left.toInt() == 0)
//                            binding.messOneRadio.isEnabled = false
//                        if (currentState.Mess2left.toInt() == 0)
//                            binding.messTwoRadio.isEnabled = false
//
//                        binding.messOneSeatsText.text =
//                            getString(R.string.seats_left, messRegistrationResponse.Mess1left)
//                        binding.messTwoSeatsText.text =
//                            getString(R.string.seats_left, messRegistrationResponse.Mess2left)
//                    } else {
//                        if (!viewModel.registeringLoading) {
//                            setNotRegisteringForMess()
//                            showSnackbar(viewModel.getMessRegistrationStatusErrorMessageStringId())
//                        }
//                    }
//                })
//        }
//    }
//
//    private fun setLoading() {
//        hideCards()
//        binding.messRegistrationSwipeRefresh.isRefreshing = true
//    }
//

    private fun setMessRegistrationClosed() {
        register_mess_card.visibility = View.GONE
        register_mess_closed_card.visibility = View.VISIBLE
    }


    private fun hideCards() {
        register_mess_card.visibility = View.GONE
        register_mess_closed_card.visibility = View.GONE
    }
//
//    private fun setRegisteringForMess() {
//        binding.messOneRadio.isEnabled = false
//        binding.messTwoRadio.isEnabled = false
//        binding.registerButton.isEnabled = false
//        binding.registerButton.text = ""
//        binding.registerProgress.visibility = View.VISIBLE
//    }
//
//    private fun setNotRegisteringForMess() {
//        binding.messOneRadio.isEnabled = true
//        binding.messTwoRadio.isEnabled = true
//        binding.registerButton.isEnabled = true
//        binding.registerButton.text = getString(R.string.register)
//        binding.registerProgress.visibility = View.GONE
//    }

}