package `in`.ac.bits_hyderabad.swd.swd.view.more.mcn

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.FileDetails
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.databinding.McnApplyFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.ApplyForMcnViewModel
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplyForMcnFragment : Fragment() {

    private lateinit var _binding: McnApplyFragmentBinding
    private val viewModel by viewModels<ApplyForMcnViewModel>()

    private val categories = listOf("General", "SC", "ST", "OBC", "Others")

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.validateZip(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = McnApplyFragmentBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryDropdownAdapter =
            ArrayAdapter(requireContext(), R.layout.text_menu_item, categories)
        _binding.categoryDropdown.setAdapter(categoryDropdownAdapter)

        _binding.applyButton.setOnClickListener {
            showConfirmationDialog()
        }

        _binding.uploadZipButton.setOnClickListener {
            getContent.launch("application/zip")
        }

        _binding.othersCheckbox.setOnCheckedChangeListener { _, isChecked ->
            _binding.otherDocsLayout.isEnabled = isChecked
            if (!isChecked)
                _binding.otherDocsEdit.setText("")
        }

        viewModel.fileDetails.observe(viewLifecycleOwner, zipDetailsObserver)
        viewModel.isSendingRequest.observe(viewLifecycleOwner, isSendingRequestObserver)
        viewModel.applyError.observe(viewLifecycleOwner, applyErrorObserver)
        viewModel.applySuccess.observe(viewLifecycleOwner, applySuccessObserver)
    }

    private val isSendingRequestObserver = Observer<Boolean> { isSending ->
        if (isSending) {
            _binding.applyProgress.visibility = View.VISIBLE
            _binding.applyButton.apply {
                text = ""
                isEnabled = false
            }
            _binding.categoryLayout.isEnabled = false
            _binding.fatherSalaryLayout.isEnabled = false
            _binding.motherSalaryLayout.isEnabled = false
            _binding.uploadZipButton.isEnabled = false
            _binding.cgpaLayout.isEnabled = false
            _binding.loanTakenCheckbox.isEnabled = false
        } else {
            _binding.applyProgress.visibility = View.GONE
            _binding.applyButton.apply {
                text = getString(R.string.apply)
                isEnabled = true
            }
            _binding.categoryLayout.isEnabled = true
            _binding.fatherSalaryLayout.isEnabled = true
            _binding.motherSalaryLayout.isEnabled = true
            _binding.uploadZipButton.isEnabled = true
            _binding.cgpaLayout.isEnabled = true
            _binding.loanTakenCheckbox.isEnabled = true
        }
    }

    private val applyErrorObserver = Observer<LiveErrorEvent> { error ->
        requireView().showSnackbarError(error)
    }

    private val applySuccessObserver = Observer<Boolean> { isSuccess ->
        if (isSuccess) {
            findNavController().navigate(R.id.action_applyForMcnFragment_to_mcnStatusFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private val zipDetailsObserver = Observer<FileDetails> { details ->
        _binding.fileNameText.text = details.fileName

        if (details.sizeExceeded) {
            _binding.fileSizeText.apply {
                text = "Size exceeded: ${details.fileSize}"
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorSignout
                    )
                )
            }
        } else {
            _binding.fileSizeText.apply {
                text = details.fileSize
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorTextSecondary
                    )
                )
            }
        }
    }

    private fun getDocumentsSubmitted(): String {
        val list = mutableListOf<String>()

        if (_binding.fatherItrCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_father_itr))
        if (_binding.motherItrCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_mother_itr))
        if (_binding.guardianItrCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_guardian_itr))
        if (_binding.fatherBankStatementCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_father_bank))
        if (_binding.motherBankStatementCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_mother_bank))
        if (_binding.guardianBankStatementCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_guardian_bank))
        if (_binding.fatherCompCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_father_comp))
        if (_binding.motherCompCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_mother_comp))
        if (_binding.pensionCertificateCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_pension))
        if (_binding.form16Checkbox.isChecked)
            list.add(getString(R.string.mcn_doc_form_16))
        if (_binding.tehsildarIncomeCertificateCheckbox.isChecked)
            list.add(getString(R.string.mcn_doc_tehsildar))
        if (_binding.othersCheckbox.isChecked)
            list.add(_binding.otherDocsEdit.text.toString())

        return list.toString().let {
            if (it.isEmpty()) ""
            else it.substring(1, it.length - 1)
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.confirm_mcn_title)
            setMessage(R.string.confirm_mcn_body)
            setNeutralButton(R.string.confirm_neutral) { _, _ ->
                // Do nothing
            }
            setPositiveButton(R.string.confirm_positive) { _, _ ->
                viewModel.applyForMcn(
                    _binding.categoryDropdown.text.toString(),
                    _binding.fatherSalaryEdit.text.toString().trim(),
                    _binding.motherSalaryEdit.text.toString().trim(),
                    getDocumentsSubmitted(),
                    _binding.loanTakenCheckbox.isChecked,
                    _binding.cgpaEdit.text.toString(),
                )
            }
            show()
        }
    }
}