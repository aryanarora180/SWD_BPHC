package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.data.Outstation
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
class OutstationViewModel @Inject constructor(private val repository: AppDataSource) :
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

    private val _outstations = MutableLiveData<List<Outstation>>()
    val outstations: LiveData<List<Outstation>>
        get() = _outstations

    fun loadOutstations() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getOutstations()) {
                is OperationResult.Success -> {
                    _isDataLoading.postValue(false)
                    if (result.data.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                    } else {
                        _outstations.postValue(result.data)
                    }
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

    init {
        loadOutstations()
    }

    private val _onCancelErrorMessage = MutableLiveData<LiveErrorEvent>()
    val onCancelMessageError: LiveData<LiveErrorEvent>
        get() = _onCancelErrorMessage

    fun cancelOutstation(outstationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.cancelOutstation(outstationId)) {
                is OperationResult.Success -> {
                    loadOutstations()
                }
                is OperationResult.Error -> {
                    _onCancelErrorMessage.postValue(
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