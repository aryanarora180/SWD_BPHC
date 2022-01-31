package `in`.ac.bits_hyderabad.swd.swd.viewmodel.goodies

import `in`.ac.bits_hyderabad.swd.swd.data.*
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_FUNDRAISER
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_TICKET
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_TSHIRT
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceGoodieOrderViewModel @Inject constructor(
    private val repository: AppDataSource
) : ViewModel() {

    lateinit var goodieData: Goodie
    lateinit var orderDetails: GoodieOrderDetails

    private val _onErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onMessageError: LiveData<SingleLiveEvent<String>> = _onErrorMessage

    private val _isPlacingOrder = MutableLiveData<Boolean>()
    val isPlacingOrder: LiveData<Boolean> = _isPlacingOrder

    private val _placeOrderSuccess = MutableLiveData<SingleLiveEvent<Boolean>>()
    val placeOrderSuccess: LiveData<SingleLiveEvent<Boolean>> = _placeOrderSuccess

    lateinit var otherAdvancesPaymentDetails: OtherAdvancesPaymentDetails

    fun placeOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            _isPlacingOrder.postValue(true)
            val result: OperationResult<Unit> = when (goodieData.type) {
                GOODIE_TYPE_TSHIRT -> {
                    repository.placeGoodieOrder(
                        goodieId = goodieData.id,
                        sizeDetails = orderDetails.sizes.getSizeMap(),
                        netQuantity = orderDetails.netQuantity,
                        totalAmount = orderDetails.totalAmount,
                    )
                }
                GOODIE_TYPE_TICKET -> {
                    repository.placeGoodieOrder(
                        goodieId = goodieData.id,
                        netQuantity = orderDetails.netQuantity,
                        totalAmount = orderDetails.totalAmount,
                    )
                }
                GOODIE_TYPE_FUNDRAISER -> {
                    repository.placeGoodieOrder(
                        goodieId = goodieData.id,
                        netQuantity = orderDetails.netQuantity,
                        totalAmount = orderDetails.totalAmount,
                    )
                }
                else -> OperationResult.Error(400, null)
            }
            when (result) {
                is OperationResult.Success -> {
                    otherAdvancesPaymentDetails = OtherAdvancesPaymentDetails(
                        true, null, orderDetails.totalAmount
                    )
                    _placeOrderSuccess.postValue(SingleLiveEvent(true))
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(
                        SingleLiveEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                }
            }
            _isPlacingOrder.postValue(false)
        }
    }
}