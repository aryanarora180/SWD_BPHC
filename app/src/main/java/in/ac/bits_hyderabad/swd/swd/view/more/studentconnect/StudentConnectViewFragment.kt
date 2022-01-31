package `in`.ac.bits_hyderabad.swd.swd.view.more.studentconnect

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.StudentConnectPerson
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.StudentConnectAdapter
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.student_connect_view_fragment.*

@AndroidEntryPoint
class StudentConnectViewFragment(private val members: List<StudentConnectPerson> = listOf()) :
    Fragment(R.layout.student_connect_view_fragment) {

    private val connectViewAdapter = StudentConnectAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectViewAdapter.listener = { memberClicked, action ->
            when (action) {
                StudentConnectAdapter.ACTION_CALL -> {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle(R.string.call_prompt_title)
                        setMessage(
                            getString(
                                R.string.call_prompt_message,
                                memberClicked.name
                            )
                        )
                        setNeutralButton(R.string.call_prompt_neutral) { _, _ ->
                            // DO NOTHING
                        }
                        setPositiveButton(R.string.call_prompt_positive) { _, _ ->
                            val intent = Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:${memberClicked.number}")
                            )
                            startActivity(intent)
                        }
                        show()
                    }
                }
                StudentConnectAdapter.ACTION_WHATSAPP -> {
                    val packageManager = requireActivity().packageManager
                    val intent = Intent(Intent.ACTION_VIEW)
                    val url =
                        "https://api.whatsapp.com/send?phone=+91" + memberClicked.number
                    intent.apply {
                        setPackage("com.whatsapp")
                        data = Uri.parse(url)
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        requireView().showSnackbarError("WhatsApp is not installed")
                    }
                }
                StudentConnectAdapter.ACTION_EMAIL -> {
                    val intent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.fromParts(
                            "mailto",
                            memberClicked.uid + getString(R.string.bits_email_ending),
                            null
                        )
                    )
                    startActivity(intent)
                }
            }
        }
        connect_view_recycler.apply {
            edgeEffectFactory =
                SpringyRecycler.springEdgeEffectFactory<StudentConnectAdapter.ViewHolder>()
            adapter = connectViewAdapter
        }

        if (members.isNotEmpty()) {
            connectViewAdapter.data = members
        } else {
            connect_view_recycler.visibility = View.GONE
            no_connect_text.visibility = View.VISIBLE
        }
    }
}