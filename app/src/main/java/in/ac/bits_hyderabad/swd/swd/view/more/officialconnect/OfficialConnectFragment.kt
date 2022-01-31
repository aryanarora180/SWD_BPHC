package `in`.ac.bits_hyderabad.swd.swd.view.more.officialconnect

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.OfficialConnectGroup
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.OfficialConnectViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.internet_error_view.*
import kotlinx.android.synthetic.main.official_connect_fragment.*

@AndroidEntryPoint
class OfficialConnectFragment : Fragment(R.layout.official_connect_fragment) {

    private val viewModel by viewModels<OfficialConnectViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.officialConnect.observe(viewLifecycleOwner, connectObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
    }

    private val connectObserver = Observer<List<OfficialConnectGroup>> { data ->
        val titles = mutableListOf<String>()
        data.forEach {
            titles.add(it.group)
        }

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

    inner class ConnectStateAdapter(private val officialConnectData: List<OfficialConnectGroup>) :
        FragmentStateAdapter(requireActivity()) {

        override fun getItemCount() = officialConnectData.size

        override fun createFragment(position: Int): Fragment {
            return OfficialConnectViewFragment(officialConnectData[position].contacts)
        }
    }
}