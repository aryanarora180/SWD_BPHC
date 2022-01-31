package `in`.ac.bits_hyderabad.swd.swd.viewmodel.goodies

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_FUNDRAISER
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_TICKET
import `in`.ac.bits_hyderabad.swd.swd.data.Goodie.Companion.GOODIE_TYPE_TSHIRT
import `in`.ac.bits_hyderabad.swd.swd.data.GoodieOrderDetails
import `in`.ac.bits_hyderabad.swd.swd.data.GoodiesSizes
import `in`.ac.bits_hyderabad.swd.swd.data.SingleLiveEvent
import `in`.ac.bits_hyderabad.swd.swd.helper.DataStoreUtils
import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BuyGoodieViewModel @Inject constructor(
    application: Application,
    private val dataStoreUtils: DataStoreUtils,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _application = application

    val goodie = savedStateHandle.get<Goodie>("GoodieData")!!

    private val _quantity = MutableLiveData(0)
    val quantity: LiveData<Int> = _quantity

    private val _amount = MutableLiveData(0)
    val amount: LiveData<Int> = _amount

    var sizesSelected = MutableLiveData(GoodiesSizes())

    private val _onErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onMessageError: LiveData<SingleLiveEvent<String>> = _onErrorMessage

    private val _onInvalidFundraiserAmount = MutableLiveData<String?>()
    val onInvalidFundraiserAmount: LiveData<String?> = _onInvalidFundraiserAmount

    init {
        when (goodie.type) {
            GOODIE_TYPE_TSHIRT -> _amount.value = goodie.price
            GOODIE_TYPE_TICKET -> {
                _amount.value = goodie.price
                _quantity.value = 1
            }
        }
    }

    fun updateSizes(updatedSizes: GoodiesSizes) {
        val quantity = updatedSizes.getQuantity()

        _quantity.value = quantity
        _amount.value = goodie.price * quantity
    }

    fun increaseQuantity() {
        var quantity = _quantity.value ?: 0
        val price: Int

        if (_quantity.value ?: 0 < goodie.limit) {
            quantity++
            price = goodie.price * quantity

            _quantity.value = quantity
            _amount.value = price
        } else {
            if (goodie.limit == 0) {
                quantity++
                price = goodie.price * quantity

                _quantity.value = quantity
                _amount.value = price
            }
        }
    }

    fun decreaseQuantity() {
        var quantity = _quantity.value ?: 0
        val price: Int

        if (quantity > 1) {
            quantity--
            price = goodie.price * quantity

            _quantity.value = quantity
            _amount.value = price
        }
    }

    fun validateFundraiserAmount(amount: Editable?) {
        try {
            if (amount.toString().isNotEmpty()) {
                val setPrice = amount.toString().toInt()
                if (setPrice >= goodie.minAmount && setPrice <= goodie.maxAmount) {
                    _amount.value = setPrice
                    _onInvalidFundraiserAmount.value = null
                } else {
                    _onInvalidFundraiserAmount.value =
                        _application.getString(R.string.error_goodie_amount_min_max_range)
                    _amount.value = 0
                }
            } else {
                _onInvalidFundraiserAmount.value =
                    _application.getString(R.string.error_goodie_invalid_amount)
                _amount.value = 0
            }
        } catch (e: Exception) {
            _onInvalidFundraiserAmount.value =
                _application.getString(R.string.error_goodie_invalid_amount)
        }
    }

    /* Returns GoodieOrderDetails. If the order is invalid, it returns null and posts an
    error message to _onErrorMessage
    */
    fun getOrderDetails(): GoodieOrderDetails? {
        return when (goodie.type) {
            GOODIE_TYPE_TSHIRT -> {
                if (_quantity.value != 0)
                    GoodieOrderDetails(
                        goodie.id,
                        sizesSelected.value!!,
                        quantity.value!!,
                        amount.value!!
                    )
                else {
                    _onErrorMessage.value =
                        SingleLiveEvent(_application.getString(R.string.error_goodie_size_not_selected))
                    null
                }
            }
            GOODIE_TYPE_TICKET -> {
                GoodieOrderDetails(
                    goodie.id,
                    GoodiesSizes(),
                    quantity.value!!, // double bangs are fine coz we know it wont be null since we assigned a value at declaration
                    amount.value!!
                )
            }
            // Ticket needs no validation since it's restricted on the frontend
            GOODIE_TYPE_FUNDRAISER -> {
                if (_amount.value != 0)
                    GoodieOrderDetails(
                        goodie.id,
                        GoodiesSizes(),
                        quantity.value!!,
                        amount.value!!
                    )
                else {
                    _onErrorMessage.value =
                        SingleLiveEvent(_application.getString(R.string.error_goodie_invalid_amount))
                    null
                }
            }
            else -> null
        }
    }

    fun isHoster(goodie: Goodie) = goodie.hostUid == dataStoreUtils.getUid()

    private val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    fun formatAmount(amount: Int): String {
        return "₹${formatter.format(amount)}"
    }
}