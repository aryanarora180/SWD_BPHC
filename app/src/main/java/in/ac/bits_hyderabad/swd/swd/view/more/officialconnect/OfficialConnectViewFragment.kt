package `in`.ac.bits_hyderabad.swd.swd.view.more.officialconnect

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.OfficialConnectPerson
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.OfficialsConnectAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.official_connect_view_fragment.*

@AndroidEntryPoint
class OfficialConnectViewFragment(
    private val officials: List<OfficialConnectPerson> = listOf()
) : Fragment(R.layout.official_connect_view_fragment) {

    private val officialViewAdapter = OfficialsConnectAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        officialViewAdapter.listener = { officialClicked ->
            val intent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts(
                    "mailto",
                    officialClicked.email,
                    null
                )
            )
            startActivity(intent)
        }

        connect_view_recycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<OfficialsConnectAdapter.ViewHolder>()
            adapter = officialViewAdapter
        }

        if (officials.isNotEmpty()) {
            officialViewAdapter.data = officials
        } else {
            connect_view_recycler.visibility = View.GONE
            no_connect_text.visibility = View.VISIBLE
        }
    }
}