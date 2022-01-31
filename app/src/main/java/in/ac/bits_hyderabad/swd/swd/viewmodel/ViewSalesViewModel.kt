package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.GoodieSales
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
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class ViewSalesViewModel @Inject constructor(private val repository: AppDataSource) :
    ViewModel() {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _onErrorMessage = MutableLiveData<LiveErrorEvent>()
    val onMessageError: LiveData<LiveErrorEvent>
        get() = _onErrorMessage

    private val _goodieSales = MutableLiveData<GoodieSales>()
    val goodieSales: LiveData<GoodieSales>
        get() = _goodieSales

    fun getGoodieSales(goodieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getGoodieSales(goodieId)) {
                is OperationResult.Success -> {
                    _goodieSales.postValue(result.data)
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(
                        LiveErrorEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                }
            }
            _isDataLoading.postValue(false)
        }
    }

    private val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    fun formatAmount(amount: Int): String {
        return "₹${formatter.format(amount)}"
    }

    fun getFormattedSizes(): String {
        val sales = _goodieSales.value
        return if (sales != null) "XS: ${sales.xs}\n S: ${sales.s}\n M: ${sales.m}\n L: ${sales.l}\n XL: ${sales.xl}\n XXL: ${sales.xxl}\n XXXL: ${sales.xxxl}" else ""
    }
}