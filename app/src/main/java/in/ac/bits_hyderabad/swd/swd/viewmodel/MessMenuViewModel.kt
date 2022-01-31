package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.Mess
import `in`.ac.bits_hyderabad.swd.swd.data.MessMenu
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
import java.util.*
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class MessMenuViewModel @Inject constructor(private val repository: AppDataSource) :
    ViewModel() {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _onErrorMessage = MutableLiveData<String>()
    val onMessageError: LiveData<String>
        get() = _onErrorMessage

    private val _menu = MutableLiveData<Mess>()
    val menu: LiveData<Mess>
        get() = _menu

    fun loadMessMenu() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getMessMenu()) {
                is OperationResult.Success -> {
                    _menu.postValue(result.data)
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

    private fun getDayString(day: Int): String {
        return when (day) {
            0 -> "Sunday"
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            else -> ""
        }
    }

    //1 is Sunday, so to make it zero-indexed, subtracting 1
    val dayTodayInt = Calendar.getInstance()[Calendar.DAY_OF_WEEK] - 1

    fun getMenuFor(day: Int): MessMenu {
        val dayString = getDayString(day)
        var menuForDay: MessMenu = MessMenu("", "", "", "", "")
        _menu.value?.messMenu?.forEach { menu ->
            if (menu.day == dayString)
                menuForDay = menu
        }
        return menuForDay
    }

    fun getMenuDayTitle(day: Int): String {
        val dayString = getDayString(day)
        return if (day == dayTodayInt) {
            "$dayString - today"
        } else {
            dayString
        }
    }

    init {
        loadMessMenu()
    }
}
