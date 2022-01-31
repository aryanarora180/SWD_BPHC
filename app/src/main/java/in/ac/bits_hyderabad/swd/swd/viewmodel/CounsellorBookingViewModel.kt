package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.CounsellorBooking
import `in`.ac.bits_hyderabad.swd.swd.data.CounsellorSlot
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class CounsellorBookingViewModel @Inject constructor(private val repository: AppDataSource) :
    ViewModel() {

    // START: Available slots
    private val _isAvailableSlotsLoading = MutableLiveData<Boolean>()
    val isAvailableSlotsLoading: LiveData<Boolean>
        get() = _isAvailableSlotsLoading

    private val _isAvailableSlotsEmptyList = MutableLiveData<Boolean>()
    val isAvailableSlotsEmptyList: LiveData<Boolean>
        get() = _isAvailableSlotsEmptyList

    private val _onAvailableSlotsErrorMessage = MutableLiveData<String>()
    val onAvailableSlotsMessageError: LiveData<String>
        get() = _onAvailableSlotsErrorMessage

    private val _availableSlots = MutableLiveData<List<CounsellorSlot>>()
    val availableSlots: LiveData<List<CounsellorSlot>>
        get() = _availableSlots

    fun loadAvailableSlots() {
        viewModelScope.launch(Dispatchers.IO) {
            _isAvailableSlotsLoading.postValue(true)
            when (val result = repository.getCounsellorSlots()) {
                is OperationResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _isAvailableSlotsEmptyList.postValue(true)
                    } else {
                        _isAvailableSlotsEmptyList.postValue(false)
                        _availableSlots.postValue(result.data)
                    }
                }
                is OperationResult.Error -> {
                    _onAvailableSlotsErrorMessage.postValue(
                        result.message ?: OperationResult.getErrorMessage(
                            result.status
                        )
                    )
                }
            }
            _isAvailableSlotsLoading.postValue(false)
        }
    }

    private val dateFormatter = DateFormatter()

    fun formatSlotTiming(slot: CounsellorSlot): String {
        val time = when {
            slot.slot < 12 -> "${slot.slot} AM"
            slot.slot == 12 -> "${slot.slot} noon"
            else -> "${slot.slot - 12} PM"
        }
        return "${
            dateFormatter.getFormattedDate(
                slot.date,
                DateFormatter.FORMAT_FULL_MONTH_NAME_DAY_FULL_YEAR
            )
        } at $time"
    }

    private val _slotRequestError = MutableLiveData<LiveErrorEvent>()
    val slotRequestError: LiveData<LiveErrorEvent>
        get() = _slotRequestError

    fun sendBookingRequest(slotToBook: CounsellorSlot) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAvailableSlotsLoading.postValue(true)
            val result = repository.bookCounsellor(slotToBook.date, slotToBook.slot)
            _isAvailableSlotsLoading.postValue(false)
            when (result) {
                is OperationResult.Success -> {
                    loadAvailableSlots()
                    loadMyBookings()
                }
                is OperationResult.Error -> {
                    _slotRequestError.postValue(
                        LiveErrorEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                    if (_availableSlots.value.isNullOrEmpty()) {
                        _isAvailableSlotsEmptyList.postValue(true)
                    }
                }
            }
        }
    }

    // END: Available slots
    // START: My bookings
    private val _isMyBookingsLoading = MutableLiveData<Boolean>()
    val isMyBookingsLoading: LiveData<Boolean>
        get() = _isMyBookingsLoading

    private val _isMyBookingsEmptyList = MutableLiveData<Boolean>()
    val isMyBookingsEmptyList: LiveData<Boolean>
        get() = _isMyBookingsEmptyList

    private val _onMyBookingsErrorMessage = MutableLiveData<String>()
    val onMyBookingsMessageError: LiveData<String>
        get() = _onMyBookingsErrorMessage

    private val _myBookings = MutableLiveData<List<CounsellorBooking>>()
    val myBookings: LiveData<List<CounsellorBooking>>
        get() = _myBookings

    fun loadMyBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            _isMyBookingsLoading.postValue(true)
            when (val result = repository.getUserBookedCounsellorSlots()) {
                is OperationResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _isMyBookingsEmptyList.postValue(true)
                    } else {
                        _isMyBookingsEmptyList.postValue(false)
                        _myBookings.postValue(result.data)
                    }
                }
                is OperationResult.Error -> {
                    _onMyBookingsErrorMessage.postValue(
                        result.message ?: OperationResult.getErrorMessage(
                            result.status
                        )
                    )
                }
            }
            _isMyBookingsLoading.postValue(false)
        }
    }

    private val _slotDeleteError = MutableLiveData<LiveErrorEvent>()
    val slotDeleteError: LiveData<LiveErrorEvent>
        get() = _slotDeleteError

    fun deleteRequest(bookingId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isMyBookingsLoading.postValue(true)
            val result = repository.deleteCounsellorSlot(bookingId)
            _isMyBookingsLoading.postValue(false)
            when (result) {
                is OperationResult.Success -> {
                    loadMyBookings()
                    loadAvailableSlots()
                }
                is OperationResult.Error -> {
                    _slotDeleteError.postValue(
                        LiveErrorEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                }
            }
        }
    }
    // END: My

    init {
        loadAvailableSlots()
        loadMyBookings()
    }

}