package `in`.ac.bits_hyderabad.swd.swd.view.more.mcn

import `in`.ac.bits_hyderabad.swd.swd.BuildConfig
import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.McnApplication
import `in`.ac.bits_hyderabad.swd.swd.data.McnFileWrapper
import `in`.ac.bits_hyderabad.swd.swd.databinding.McnStatusFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.McnStatusViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class McnStatusFragment : Fragment(R.layout.mcn_status_fragment) {

    private lateinit var _binding: McnStatusFragmentBinding
    private val viewModel by viewModels<McnStatusViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = McnStatusFragmentBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.deleteImage.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.delete_mcn)
                setMessage(R.string.cancel_mcn_message)
                setPositiveButton(R.string.cancel_positive) { _, _ ->
                    viewModel.deleteApplication()
                }
                setNeutralButton(R.string.cancel_neutral) { _, _ ->
                    // Do nothing
                }
                show()
            }
        }

        viewModel.isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
        viewModel.isClosed.observe(viewLifecycleOwner, isClosedObserver)
        viewModel.onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
        viewModel.onDeleteMessageError.observe(viewLifecycleOwner, onDeleteErrorMessageObserver)
        viewModel.hasApplied.observe(viewLifecycleOwner, hasAppliedObserver)
        viewModel.application.observe(viewLifecycleOwner, applicationObserver)
        viewModel.mcnDownloadState.observe(viewLifecycleOwner, mcnDownloadStateObserver)
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            _binding.run {
                mcnStatusCard.visibility = View.GONE
                mcnDetailsCard.visibility = View.GONE
                applyButton.visibility = View.GONE
                mcnProgress.visibility = View.VISIBLE
                errorOccurredView.root.visibility = View.GONE
            }
        } else {
            _binding.mcnProgress.visibility = View.GONE
        }
    }

    private val isClosedObserver = Observer<Boolean> { isClosed ->
        if (isClosed) {
            _binding.run {
                applyButton.visibility = View.GONE
                mcnStatusCard.visibility = View.VISIBLE
                mcnPortalStatusText.setText(R.string.mcn_portal_closed_text)
                mcnDetailsCard.visibility = View.GONE
            }
        } else {
            _binding.mcnStatusCard.visibility = View.GONE
        }
    }

    private val onErrorMessageObserver = Observer<String> { error ->
        _binding.run {
            errorOccurredView.root.visibility = View.VISIBLE
            errorOccurredView.errorText.text = error
            errorOccurredView.errorRetryButton.setOnClickListener {
                viewModel.loadStatus()
            }
        }
    }

    private val onDeleteErrorMessageObserver = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }

    private val hasAppliedObserver = Observer<Boolean> { hasApplied ->
        _binding.run {
            if (hasApplied) {
                mcnStatusCard.visibility = View.GONE
                mcnDetailsCard.visibility = View.VISIBLE
                applyButton.setText(R.string.resubmit)

            } else {
                mcnStatusCard.visibility = View.VISIBLE
                mcnPortalStatusText.setText(R.string.mcn_not_applied)
                mcnDetailsCard.visibility = View.GONE
                applyButton.setText(R.string.apply)

            }
            applyButton.visibility = View.VISIBLE
            applyButton.setOnClickListener {
                findNavController().navigate(R.id.action_mcnStatusFragment_to_applyForMcnFragment)
            }
        }
    }

    private val mcnDownloadStateObserver = Observer<McnFileWrapper> { fileWrapper ->
        _binding.run {
            when (fileWrapper.state) {
                McnFileWrapper.STATE_NOT_DOWNLOADED -> {
                    downloadZipImage.visibility = View.VISIBLE
                    downloadZipImage.setImageResource(R.drawable.outline_get_app_24)
                    mcnZipDownloadProgress.visibility = View.GONE

                    downloadZipImage.setOnClickListener { viewModel.downloadZip() }
                }
                McnFileWrapper.STATE_DOWNLOADING -> {
                    downloadZipImage.visibility = View.INVISIBLE
                    mcnZipDownloadProgress.visibility = View.VISIBLE
                }
                McnFileWrapper.STATE_DOWNLOAD_FAIL -> {
                    downloadZipImage.visibility = View.VISIBLE
                    downloadZipImage.setImageResource(R.drawable.outline_error_outline_24)
                    mcnZipDownloadProgress.visibility = View.GONE

                    downloadZipImage.setOnClickListener { viewModel.downloadZip() }
                }
                McnFileWrapper.STATE_DOWNLOADED -> {
                    downloadZipImage.visibility = View.VISIBLE
                    downloadZipImage.setImageResource(R.drawable.outline_launch_24)
                    mcnZipDownloadProgress.visibility = View.GONE

                    downloadZipImage.setOnClickListener { openDocument(fileWrapper.fileLocation) }
                }
            }
        }
    }

    private val applicationObserver = Observer<McnApplication> { application ->
        _binding.run {
            nameText.text = application.name
            categoryText.text = application.category
            fatherSalaryText.text = viewModel.formatAmount(application.fatherSalary)
            motherSalaryText.text = viewModel.formatAmount(application.motherSalary)
            cgpaText.text = application.cgpa
            documentsSubmittedText.text =
                getString(R.string.mcn_doc_list, application.documentsSubmitted)

            when (application.status) {
                McnApplication.MCN_STATUS_APPLICATION_DENIED -> {
                    statusImage.setImageResource(R.drawable.outline_cancel_24)
                    statusText.setText(R.string.denied)
                }
                McnApplication.MCN_STATUS_APPLICATION_PENDING -> {
                    statusImage.setImageResource(R.drawable.outline_hourglass_top_24)
                    statusText.setText(R.string.pending)
                }
                McnApplication.MCN_STATUS_APPLICATION_APPROVED -> {
                    statusImage.setImageResource(R.drawable.outline_check_circle_24)
                    statusText.setText(R.string.approved)
                }
            }

            if (application.isLoan == 1) {
                loanTakenText.visibility = View.VISIBLE
            }

            remarksText.text = application.remark
        }
    }

    private fun openDocument(fileLocation: String?) {
        if (!fileLocation.isNullOrEmpty()) {
            val file = File(fileLocation)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    FileProvider.getUriForFile(
                        requireActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    ),
                    "application/zip"
                )
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val viewerIntent = Intent.createChooser(intent, "Open ZIP")
            startActivity(viewerIntent)
        }
    }

}