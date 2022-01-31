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
class DeductionDetailsViewModel @Inject constructor(private val repository: AppDataSource) :
    ViewModel() {

    private val _isCanceling = MutableLiveData<Boolean>()
    val isCanceling: LiveData<Boolean>
        get() = _isCanceling

    private val _onErrorMessage = MutableLiveData<LiveErrorEvent>()
    val onMessageError: LiveData<LiveErrorEvent>
        get() = _onErrorMessage

    private val _cancelSuccessful = MutableLiveData<Boolean>()
    val cancelSuccessful: LiveData<Boolean>
        get() = _cancelSuccessful

    fun cancelDeduction(transactionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isCanceling.postValue(true)
            when (val result = repository.cancelOrder(transactionId)) {
                is OperationResult.Success -> {
                    _cancelSuccessful.postValue(true)
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(
                        LiveErrorEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                    _cancelSuccessful.postValue(false)
                }
            }
            _isCanceling.postValue(false)
        }
    }

    fun getDate(date: Long) =
        SimpleDateFormat("MMMM dd, YYYY", Locale.getDefault()).format(Date(date))

    fun getTime(date: Long) = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(date))
}