package `in`.ac.bits_hyderabad.swd.swd.view.more

import `in`.ac.bits_hyderabad.swd.swd.MainActivity
import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.Profile
import `in`.ac.bits_hyderabad.swd.swd.databinding.ProfileFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.ProfileViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var _binding: ProfileFragmentBinding
    private val viewModel by viewModels<ProfileViewModel>()
    private val args by navArgs<ProfileFragmentArgs>()

    private val gender = listOf("M", "F", "O")
    private val incomes = listOf(
        "Below INR 4,00,000",
        "INR 4,00,000 to INR 8,00,000",
        "INR 8,00,000 to INR 12,00,000",
        "Above INR 12,00,000"
    )
    private val categories = listOf("General", "SC", "ST", "OBC", "Others")
    private val bloodTypes = listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")

    private lateinit var editTexts: List<EditText>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.needsToComplete = args.needsToComplete
        if (args.needsToComplete)
            _binding.completeProfilePromptText.visibility = View.VISIBLE

        editTexts = listOf(
            _binding.hostelDropdown,
            _binding.roomEdit,
            _binding.genderDropdown,
            _binding.phoneEdit,
            _binding.alternatePhoneEdit,
            _binding.emailEdit,
            _binding.dobEdit,
            _binding.aadhaarEdit,
            _binding.panCardEdit,
            _binding.categoryDropdown,
            _binding.nationalityEdit,
            _binding.fatherNameEdit,
            _binding.motherNameEdit,
            _binding.fatherEmailEdit,
            _binding.fatherPhoneEdit,
            _binding.fatherOccupationEdit,
            _binding.fatherCompanyEdit,
            _binding.fatherDesignationEdit,
            _binding.motherEmailEdit,
            _binding.motherOccupationEdit,
            _binding.motherCompanyEdit,
            _binding.motherDesignationEdit,
            _binding.householdIncomeDropdown,
            _binding.housePhoneEdit,
            _binding.homeAddressEdit,
            _binding.districtEdit,
            _binding.cityEdit,
            _binding.stateEdit,
            _binding.pinCodeEdit,
            _binding.guardianEdit,
            _binding.guardianPhoneEdit,
            _binding.guardianAddressEdit,
            _binding.bloodTypeDropdown,
            _binding.medicalHistoryEdit,
            _binding.currentMedicationsEdit,
            _binding.bankNameEdit,
            _binding.bankAccountNoEdit,
            _binding.bankAccountIfscCodeEdit,
        )

        _binding.updateFab.setOnClickListener {
            viewModel.toggleState()
        }

        _binding.profileScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            when {
                scrollY > oldScrollY -> {
                    _binding.updateFab.shrink()
                }
                scrollX == scrollY -> {
                    _binding.updateFab.extend()
                }
                else -> {
                    _binding.updateFab.extend()
                }
            }
        }

        with(viewModel) {
            profileState.observe(viewLifecycleOwner, profileStateObserver)
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorMessageObserver)
            onUpdateMessageError.observe(viewLifecycleOwner, onUpdateErrorMessageObserver)
            profile.observe(viewLifecycleOwner, profileObserver)
            isRegistrationCompleted.observe(viewLifecycleOwner, isRegistrationCompletedObserver)
        }
    }

    private val profileStateObserver = Observer<Int> { updatedState ->
        if (updatedState == ProfileViewModel.STATE_EDITABLE) {
            enableInput()
            _binding.updateFab.setOnClickListener {
                showDataValidPrompt()
            }
        } else {
            disableInput()
            _binding.updateFab.setOnClickListener {
                viewModel.toggleState()
            }
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            _binding.profileScroll.visibility = View.GONE
            _binding.updateFab.visibility = View.GONE
            _binding.errorOccurredView.root.visibility = View.GONE
            _binding.profileLoadProgress.visibility = View.VISIBLE
        } else {
            _binding.profileLoadProgress.visibility = View.GONE
        }
    }

    private val onErrorMessageObserver = Observer<String> { error ->
        _binding.profileScroll.visibility = View.GONE
        _binding.updateFab.visibility = View.GONE
        _binding.errorOccurredView.root.visibility = View.VISIBLE
        _binding.errorOccurredView.errorText.text = error
        _binding.errorOccurredView.errorText.setOnClickListener {
            viewModel.loadProfile()
        }
    }

    private val onUpdateErrorMessageObserver = Observer<LiveErrorEvent> { error ->
        _binding.profileScroll.visibility = View.VISIBLE
        _binding.updateFab.visibility = View.VISIBLE
        enableInput()
        requireView().showSnackbarError(error)
    }

    private val profileObserver = Observer<Profile> { result ->
        _binding.profileScroll.visibility = View.VISIBLE
        _binding.updateFab.visibility = View.VISIBLE
        setProfileData(result)
        disableInput()
    }

    private val isRegistrationCompletedObserver = Observer<Boolean> { completed ->
        if (completed) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun disableInput() {
        _binding.updateFab.setIconResource(R.drawable.outline_create_24)
        _binding.updateFab.text = getString(R.string.edit)

        editTexts.forEach { it.isEnabled = false }

        _binding.dobLayout.setEndIconOnClickListener(null)

        _binding.hostelLayout.isEndIconVisible = false
        _binding.genderLayout.isEndIconVisible = false
        _binding.householdIncomeLayout.isEndIconVisible = false
        _binding.categoryLayout.isEndIconVisible = false
        _binding.bloodTypeLayout.isEndIconVisible = false

        _binding.hostelDropdown.setAdapter(null)
        _binding.genderDropdown.setAdapter(null)
        _binding.householdIncomeDropdown.setAdapter(null)
        _binding.categoryDropdown.setAdapter(null)
        _binding.bloodTypeDropdown.setAdapter(null)
    }

    private fun enableInput() {
        _binding.updateFab.setIconResource(R.drawable.outline_save_24)
        _binding.updateFab.text = getString(R.string.save)

        editTexts.forEach {
            it.isEnabled = true
        }

        _binding.dobLayout.setEndIconOnClickListener {
            val picker: MaterialDatePicker<Long> =
                MaterialDatePicker.Builder.datePicker().build().apply {
                    addOnPositiveButtonClickListener {
                        val date = Date(it)
                        _binding.dobEdit.setText(viewModel.formatDate(date))
                    }
                }
            picker.show(parentFragmentManager, picker.toString())
        }

        val genderDropdownAdapter = ArrayAdapter(requireContext(), R.layout.text_menu_item, gender)
        _binding.genderDropdown.setAdapter(genderDropdownAdapter)

        val incomeDropdownAdapter = ArrayAdapter(requireContext(), R.layout.text_menu_item, incomes)
        _binding.householdIncomeDropdown.setAdapter(incomeDropdownAdapter)


        val categoryDropdownAdapter =
            ArrayAdapter(requireContext(), R.layout.text_menu_item, categories)
        _binding.categoryDropdown.setAdapter(categoryDropdownAdapter)

        val bloodTypeDropdownAdapter =
            ArrayAdapter(requireContext(), R.layout.text_menu_item, bloodTypes)
        _binding.bloodTypeDropdown.setAdapter(bloodTypeDropdownAdapter)

        val hostelDropdownAdapter =
            ArrayAdapter(requireContext(), R.layout.text_menu_item, viewModel.hostels)
        _binding.hostelDropdown.setAdapter(hostelDropdownAdapter)

        _binding.hostelLayout.isEndIconVisible = true
        _binding.genderLayout.isEndIconVisible = true
        _binding.householdIncomeLayout.isEndIconVisible = true
        _binding.categoryLayout.isEndIconVisible = true
        _binding.bloodTypeLayout.isEndIconVisible = true
    }

    private fun setProfileData(profile: Profile) {
        val profileData = profile.profileData

        with(_binding) {
            nameText.text = profileData.name
            idText.text = profileData.id
            hostelDropdown.setText(profile.getHostelName(profileData.hostel), false)
            roomEdit.setText(profileData.room)
            genderDropdown.setText(profileData.gender, false)
            phoneEdit.setText(profileData.phoneNumber)
            alternatePhoneEdit.setText(profileData.alternatePhoneNumber)
            emailEdit.setText(profileData.email)
            dobEdit.setText(profileData.dateOfBirth)
            aadhaarEdit.setText(profileData.aadhaarCardNumber)
            panCardEdit.setText(profileData.panCardNumber)
            categoryDropdown.setText(profileData.category, false)
            nationalityEdit.setText(profileData.nationality)
            fatherNameEdit.setText(profileData.fatherName)
            motherNameEdit.setText(profileData.motherName)
            fatherEmailEdit.setText(profileData.fatherEmailAddress)
            fatherPhoneEdit.setText(profileData.fatherPhoneNumber)
            fatherOccupationEdit.setText(profileData.fatherOccupation)
            fatherCompanyEdit.setText(profileData.fatherCompanyName)
            fatherDesignationEdit.setText(profileData.fatherDesignation)
            motherEmailEdit.setText(profileData.motherEmailAddress)
            motherOccupationEdit.setText(profileData.motherOccupation)
            motherCompanyEdit.setText(profileData.motherCompanyName)
            motherDesignationEdit.setText(profileData.motherDesignation)
            householdIncomeDropdown.setText(profileData.householdIncome, false)
            housePhoneEdit.setText(profileData.housePhoneNumber)
            homeAddressEdit.setText(profileData.homeAddress)
            districtEdit.setText(profileData.district)
            cityEdit.setText(profileData.city)
            stateEdit.setText(profileData.state)
            pinCodeEdit.setText(profileData.pinCode)
            guardianEdit.setText(profileData.guardianName)
            guardianPhoneEdit.setText(profileData.guardianPhoneNumber)
            guardianAddressEdit.setText(profileData.guardianAddress)
            bloodTypeDropdown.setText(profileData.bloodGroup, false)
            medicalHistoryEdit.setText(profileData.medicalHistory)
            currentMedicationsEdit.setText(profileData.currentMedications)
            bankNameEdit.setText(profileData.bankName)
            bankAccountNoEdit.setText(profileData.bankAccountNumber)
            bankAccountIfscCodeEdit.setText(profileData.bankIfscCode)
        }
    }

    private fun showDataValidPrompt() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.confirm_profile_correctness_title)
            setMessage(R.string.confirm_profile_correctness_body)
            setNeutralButton(R.string.confirm_profile_correctness_neutral) { _, _ ->
                // Do nothing
            }
            setPositiveButton(R.string.confirm_profile_correctness_positive) { _, _ ->
                viewModel.sendProfileData(getProfileFields())
            }
            show()
        }
    }

    private fun getProfileFields(): List<String> {
        val fields = mutableListOf<String>()
        editTexts.forEach {
            val text = it.text ?: ""
            fields.add(text.toString())
        }
        return fields
    }
}