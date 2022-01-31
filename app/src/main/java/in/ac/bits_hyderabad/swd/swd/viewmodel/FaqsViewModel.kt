package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.FaqsWrapper
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class FaqsViewModel @Inject constructor(
    private val repository: AppDataSource,
    application: Application
) : AndroidViewModel(application) {

    private val _application = application

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _onErrorMessage = MutableLiveData<String>()
    val onMessageError: LiveData<String>
        get() = _onErrorMessage

    private val _faqGroups = MutableLiveData<FaqsWrapper>()
    val faqGroups: LiveData<FaqsWrapper>
        get() = _faqGroups

    fun loadFaqs() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getFaqs()) {
                is OperationResult.Success -> {
                    _faqGroups.postValue(result.data)
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

    private val onDateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    private val onTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    fun formatLastUpdated(time: Long) = _application.getString(
        R.string.last_updated,
        onDateFormatter.format(time * 1000),
        onTimeFormatter.format(time * 1000)
    )

    init {
        loadFaqs()
    }
}