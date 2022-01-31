package `in`.ac.bits_hyderabad.swd.swd.view.mess

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Mess
import `in`.ac.bits_hyderabad.swd.swd.data.MessMenu
import `in`.ac.bits_hyderabad.swd.swd.databinding.MessMenuFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.MessMenuViewModel
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.internet_error_view.*
import kotlinx.android.synthetic.main.mess_menu_fragment.*

@AndroidEntryPoint
class MessMenuFragment : Fragment(R.layout.mess_menu_fragment) {

    private val viewModel by viewModels<MessMenuViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mess_register_image.setOnClickListener { findNavController().navigate(R.id.action_messMenuFragment_to_messRegistrationFragment) }
        mess_grace_button.setOnClickListener { findNavController().navigate(R.id.action_messMenuFragment_to_messGraceFragment) }

        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.menu.observe(viewLifecycleOwner, messObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorObserver)
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            mess_error_occurred_view.visibility = View.GONE
            menu_viewpager.visibility = View.GONE
            registered_mess_text.visibility = View.GONE
            mess_menu_progress.visibility = View.VISIBLE
//            mess_registered_progress.visibility = View.VISIBLE
            page_indicator.visibility = View.INVISIBLE
        } else {
            mess_menu_progress.visibility = View.GONE
//            mess_registered_progress.visibility = View.GONE
        }
    }

    private val messObserver = Observer<Mess> { mess ->
        menu_viewpager.visibility = View.VISIBLE
        registered_mess_text.visibility = View.VISIBLE
        page_indicator.visibility = View.VISIBLE
        mess_error_occurred_view.visibility = View.GONE

        if (mess.registeredMess == 0)
            registered_mess_text.text =
                getString(R.string.registered_mess_unknown)
        else
            registered_mess_text.text =
                getString(R.string.registered_mess, mess.registeredMess.toString())
        menu_viewpager.adapter = IntroStateAdapter(requireActivity())
        menu_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                page_indicator.selection = position
            }
        })
        menu_viewpager.postDelayed({
            menu_viewpager.currentItem = viewModel.dayTodayInt
        }, 10)
    }

    private val onErrorObserver = Observer<String> { error ->
        mess_error_occurred_view.visibility = View.VISIBLE
        menu_viewpager.visibility = View.GONE
        registered_mess_text.visibility = View.VISIBLE
        registered_mess_text.text =
            getString(R.string.registered_mess_unknown)
        page_indicator.visibility = View.INVISIBLE
        error_text.text = error
        error_retry_button.setOnClickListener {
            viewModel.loadMessMenu()
        }

    }

    inner class IntroStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 7

        override fun createFragment(position: Int): Fragment {
            return MenuViewFragment.newInstance(
                viewModel.getMenuDayTitle(position),
                viewModel.getMenuFor(position)
            )
        }
    }
}