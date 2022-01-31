package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class RequestOutstationViewModel @Inject constructor(private val repository: AppDataSource) :
    ViewModel() {

    private val _isSendingRequest = MutableLiveData<Boolean>()
    val isSendingRequest: LiveData<Boolean>
        get() = _isSendingRequest

    private val _outstationRequestSuccess = MutableLiveData<Boolean>()
    val outstationRequestSuccess: LiveData<Boolean>
        get() = _outstationRequestSuccess

    private val _outstationRequestError = MutableLiveData<LiveErrorEvent>()
    val outstationRequestError: LiveData<LiveErrorEvent>
        get() = _outstationRequestError

    private val uiDateFormatter = SimpleDateFormat("MMM dd, YYYY", Locale.getDefault())
    private val apiDateFormatter = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
    private fun parseDate(date: Long): String = uiDateFormatter.format(Date(date))
    fun getFormattedDates(fromDate: Long, toDate: Long) =
        "${parseDate(fromDate)} to ${parseDate(toDate)}"

    fun sendOutstationRequest(
        fromDate: Long,
        toDate: Long,
        reason: String,
        location: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSendingRequest.postValue(true)
            when (val result = repository.requestOutstation(
                apiDateFormatter.format(Date(fromDate)),
                apiDateFormatter.format(Date(toDate)),
                reason,
                location
            )) {
                is OperationResult.Success -> {
                    _outstationRequestSuccess.postValue(true)
                }
                is OperationResult.Error -> {
                    _outstationRequestSuccess.postValue(false)
                    _outstationRequestError.postValue(
                        LiveErrorEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                }
            }
            _isSendingRequest.postValue(false)
        }
    }
}