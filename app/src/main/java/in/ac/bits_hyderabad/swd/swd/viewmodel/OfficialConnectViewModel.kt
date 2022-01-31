package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.OfficialConnectGroup
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
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class OfficialConnectViewModel @Inject constructor(private val repository: AppDataSource) :
    ViewModel() {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _onErrorMessage = MutableLiveData<String>()
    val onMessageError: LiveData<String>
        get() = _onErrorMessage

    private val _officialConnect = MutableLiveData<List<OfficialConnectGroup>>()
    val officialConnect: LiveData<List<OfficialConnectGroup>>
        get() = _officialConnect

    fun loadConnect() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getOfficialsConnect()) {
                is OperationResult.Success -> {
                    _officialConnect.postValue(result.data)
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

    init {
        loadConnect()
    }
}