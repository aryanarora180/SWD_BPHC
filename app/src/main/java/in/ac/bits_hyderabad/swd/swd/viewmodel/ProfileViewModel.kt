package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.data.Profile
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.set

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppDataSource,
    application: Application
) : AndroidViewModel(application) {

    private val _application = application

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _onErrorMessage = MutableLiveData<String>()
    val onMessageError: LiveData<String>
        get() = _onErrorMessage

    private val _profile = MutableLiveData<Profile>()
    val profile: LiveData<Profile>
        get() = _profile

    private val _profileState = MutableLiveData<Int>()
    val profileState: LiveData<Int>
        get() = _profileState

    private val _isRegistrationCompleted = MutableLiveData<Boolean>()
    val isRegistrationCompleted: LiveData<Boolean>
        get() = _isRegistrationCompleted

    lateinit var hostels: List<String>

    var needsToComplete: Boolean = false

    companion object {
        const val STATE_NON_EDITABLE = 0
        const val STATE_EDITABLE = 1

        private const val SIZE_LIMIT = 2097152 // 2 MB
    }

    fun loadProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getProfile()) {
                is OperationResult.Success -> {
                    hostels = result.data.getHostelNamesList()
                    _isDataLoading.postValue(false)
                    _profile.postValue(result.data)
                    _profileState.postValue(if (needsToComplete) STATE_EDITABLE else STATE_NON_EDITABLE)
                }
                is OperationResult.Error -> {
                    _isDataLoading.postValue(false)
                    _onErrorMessage.postValue(
                        result.message ?: OperationResult.getErrorMessage(
                            result.status
                        )
                    )
                }
            }
        }
    }

    fun toggleState() {
        _profileState.value =
            if (_profileState.value == STATE_EDITABLE) STATE_NON_EDITABLE else STATE_EDITABLE
    }

    private val _onUpdateErrorMessage = MutableLiveData<LiveErrorEvent>()
    val onUpdateMessageError: LiveData<LiveErrorEvent>
        get() = _onUpdateErrorMessage

    fun sendProfileData(profile: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            if (validateData(profile)) {
                when (val result = repository.sendProfileDetails(getProfileAsMap(profile))) {
                    is OperationResult.Success -> {
                        if (needsToComplete) {
                            _isRegistrationCompleted.postValue(true)
                        } else {
                            loadProfile()
                        }
                    }
                    is OperationResult.Error -> {
                        _onUpdateErrorMessage.postValue(
                            LiveErrorEvent(
                                result.message ?: OperationResult.getErrorMessage(
                                    result.status
                                )
                            )
                        )
                        _isDataLoading.postValue(false)
                    }
                }
            }
        }
    }

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    fun formatDate(dateToFormat: Date): String {
        return formatter.format(dateToFormat)
    }

    private val dataFields = listOf(
        "hostel",
        "room",
        "gender",
        "phone",
        "alt_phone",
        "email",
        "dob",
        "aadhaar",
        "pan_card",
        "category",
        "nation",
        "father",
        "mother",
        "fmail",
        "fphone",
        "foccup",
        "fcomp",
        "fdesg",
        "mmail",
        "moccup",
        "mcomp",
        "mdesg",
        "income",
        "hphone",
        "homeadd",
        "dist",
        "city",
        "state",
        "pin_code",
        "guardian",
        "gphone",
        "localadd",
        "blood",
        "med_history",
        "current_med",
        "bank",
        "acno",
        "ifsc"
    )

    private fun getProfileAsMap(fieldValues: List<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        dataFields.forEachIndexed { index, field ->
            if (index == 0) {
                map[field] = profile.value?.getHostelKey(fieldValues[index]) ?: "NA"
            } else {
                map[field] = fieldValues[index]
            }
        }
        return map
    }

    private fun validateData(fieldValues: List<String>): Boolean {
        fieldValues.forEach {
            if (it.isEmpty()) {
                _isDataLoading.postValue(false)
                _onUpdateErrorMessage.postValue(
                    LiveErrorEvent(
                        _application.getString(
                            R.string.error_fields_empty
                        )
                    )
                )
                return false
            }
        }
        return true
    }

    init {
        loadProfile()
    }
}