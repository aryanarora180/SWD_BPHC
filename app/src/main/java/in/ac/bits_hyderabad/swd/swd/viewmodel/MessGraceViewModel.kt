package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.Grace
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
class MessGraceViewModel @Inject constructor(private val repository: AppDataSource) :
    ViewModel() {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList

    private val _onErrorMessage = MutableLiveData<String>()
    val onMessageError: LiveData<String>
        get() = _onErrorMessage

    private val _graces = MutableLiveData<List<Grace>>()
    val graces: LiveData<List<Grace>>
        get() = _graces

    fun loadGraces() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getMessGraces()) {
                is OperationResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _graces.postValue(result.data)
                    }
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(
                        result.message ?: OperationResult.getErrorMessage(
                            result.status
                        )
                    )
                }
            }
            _isDataLoading.postValue(false)
        }
    }

    private val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadGraces()
    }

    private val _graceRequestError = MutableLiveData<LiveErrorEvent>()
    val graceRequestError: LiveData<LiveErrorEvent>
        get() = _graceRequestError

    fun sendMessGraceRequest(date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.sendMessGraceRequest(dateParser.format(date))) {
                is OperationResult.Success -> {
                    loadGraces()
                }
                is OperationResult.Error -> {
                    _graceRequestError.postValue(
                        LiveErrorEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                    if (_onErrorMessage.value.isNullOrBlank()) {
                        _isDataLoading.postValue(false)
                    } else {
                        loadGraces()
                    }
                }
            }
        }
    }
}