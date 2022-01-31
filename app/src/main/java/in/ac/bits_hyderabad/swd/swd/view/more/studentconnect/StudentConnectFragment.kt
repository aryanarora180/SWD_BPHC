package `in`.ac.bits_hyderabad.swd.swd.view.more.studentconnect

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.StudentConnect
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.StudentConnectViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.internet_error_view.*
import kotlinx.android.synthetic.main.student_connect_fragment.*

@AndroidEntryPoint
class StudentConnectFragment : Fragment(R.layout.student_connect_fragment) {

    private val viewModel by viewModels<StudentConnectViewModel>()

    private val titles = arrayOf("SWD", "SUC", "PU", "EC", "CRC", "SMC")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.studentConnect.observe(viewLifecycleOwner, connectObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
    }

    private val connectObserver = Observer<StudentConnect> { data ->
        connect_viewpager.adapter = ConnectStateAdapter(data)
        TabLayoutMediator(
            connect_tab,
            connect_viewpager
        ) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    private val onErrorMessageObserver = Observer<String> { error ->
        error_occurred_view.visibility = View.VISIBLE
        error_text.text = error
        error_retry_button.setOnClickListener {
            viewModel.loadConnect()
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            connect_progress.visibility = View.VISIBLE
            connect_tab.visibility = View.GONE
            connect_viewpager.visibility = View.GONE
            error_occurred_view.visibility = View.GONE
        } else {
            connect_progress.visibility = View.GONE
            connect_tab.visibility = View.VISIBLE
            connect_viewpager.visibility = View.VISIBLE
        }
    }

    inner class ConnectStateAdapter(private val studentConnectData: StudentConnect) :
        FragmentStateAdapter(requireActivity()) {

        override fun getItemCount() = 6

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StudentConnectViewFragment(studentConnectData.swd)
                1 -> StudentConnectViewFragment(studentConnectData.suc)
                2 -> StudentConnectViewFragment(studentConnectData.pu)
                3 -> StudentConnectViewFragment(studentConnectData.ec)
                4 -> StudentConnectViewFragment(studentConnectData.crc)
                5 -> StudentConnectViewFragment(studentConnectData.smc)
                else -> StudentConnectViewFragment()
            }
        }
    }
}