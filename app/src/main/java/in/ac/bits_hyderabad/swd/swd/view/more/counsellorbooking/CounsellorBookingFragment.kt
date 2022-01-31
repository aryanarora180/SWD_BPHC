package `in`.ac.bits_hyderabad.swd.swd.view.more.counsellorbooking

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.CounsellorBookingViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.counsellor_booking_fragment.*

@AndroidEntryPoint
class CounsellorBookingFragment : Fragment(R.layout.counsellor_booking_fragment) {

    private val viewModel by viewModels<CounsellorBookingViewModel>()

    private val titles = arrayOf("Available slots", "My bookings")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        counsellor_booking_viewpager.adapter = CounsellorStateAdapter()
        TabLayoutMediator(
            counsellor_booking_tab,
            counsellor_booking_viewpager
        ) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    inner class CounsellorStateAdapter :
        FragmentStateAdapter(requireActivity()) {

        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AvailableSlotsFragment(viewModel)
                1 -> MyCounsellorBookingsFragment(viewModel)
                else -> MyCounsellorBookingsFragment(viewModel)
            }
        }
    }
}