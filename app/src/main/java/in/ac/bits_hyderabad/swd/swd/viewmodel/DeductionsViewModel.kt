package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.Deduction
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
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class DeductionsViewModel @Inject constructor(private val repository: AppDataSource) :
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

    private val _deductions = MutableLiveData<List<Deduction>>()
    val deductions: LiveData<List<Deduction>>
        get() = _deductions

    private val _amountSpent = MutableLiveData<String>()
    val amountSpent: LiveData<String>
        get() = _amountSpent

    fun loadDeductions() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getDeductions()) {
                is OperationResult.Success -> {
                    _isDataLoading.postValue(false)
                    if (result.data.isNullOrEmpty()) {
                        _isEmptyList.postValue(true)
                        _amountSpent.postValue(formatAmount(0))
                    } else {
                        _deductions.postValue(result.data)
                        _amountSpent.postValue(formatAmount(getTotalAmountSpent(result.data)))
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

    private fun getTotalAmountSpent(deductions: List<Deduction>): Int {
        var amount = 0
        deductions.forEach {
            amount += it.totalAmount
        }
        return amount
    }

    private val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    private fun formatAmount(amount: Int): String {
        return "₹${formatter.format(amount)}"
    }

    init {
        loadDeductions()
    }

}