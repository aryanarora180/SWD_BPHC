package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.McnApplication
import `in`.ac.bits_hyderabad.swd.swd.data.McnFileWrapper
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
import okhttp3.ResponseBody
import java.io.*
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class McnStatusViewModel @Inject constructor(
    private val repository: AppDataSource,
    application: Application
) : AndroidViewModel(application) {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading

    private val _isClosed = MutableLiveData<Boolean>()
    val isClosed: LiveData<Boolean>
        get() = _isClosed

    private val _onErrorMessage = MutableLiveData<String>()
    val onMessageError: LiveData<String>
        get() = _onErrorMessage

    private val _hasApplied = MutableLiveData<Boolean>()
    val hasApplied: LiveData<Boolean>
        get() = _hasApplied

    private val _application = MutableLiveData<McnApplication>()
    val application: LiveData<McnApplication>
        get() = _application

    fun loadStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getMcnPortalStatus()) {
                is OperationResult.Success -> {
                    _isClosed.postValue(false)
                    loadApplicationDetails()
                }
                is OperationResult.Error -> {
                    // Server returns a 404 if the portal is closed
                    if (result.status == 404) {
                        _isClosed.postValue(true)
                    } else {
                        _isClosed.postValue(false)
                        _onErrorMessage.postValue(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status

                            )
                        )
                    }
                    _isDataLoading.postValue(false)
                }
            }
        }
    }

    private fun loadApplicationDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.getMcnApplication()) {
                is OperationResult.Success -> {
                    _hasApplied.postValue(true)
                    _application.postValue(result.data)
                }
                is OperationResult.Error -> {
                    // Server returns a 400 if the user hasn't applied
                    if (result.status == 400) {
                        _hasApplied.postValue(false)
                    } else {
                        _onErrorMessage.postValue(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    }
                }
            }
            _isDataLoading.postValue(false)
        }
    }

    private val _onDeleteErrorMessage = MutableLiveData<LiveErrorEvent>()
    val onDeleteMessageError: LiveData<LiveErrorEvent>
        get() = _onDeleteErrorMessage

    fun deleteApplication() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDataLoading.postValue(true)
            when (val result = repository.deleteCurrentMcnApplication()) {
                is OperationResult.Success -> {
                    loadApplicationDetails()
                }
                is OperationResult.Error -> {
                    _onDeleteErrorMessage.postValue(
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

    private val _mcnDownloadState = MutableLiveData<McnFileWrapper>().apply {
        value = McnFileWrapper(McnFileWrapper.STATE_NOT_DOWNLOADED, null)
    }
    val mcnDownloadState: LiveData<McnFileWrapper>
        get() = _mcnDownloadState

    fun downloadZip() {
        viewModelScope.launch(Dispatchers.IO) {
            _mcnDownloadState.postValue(McnFileWrapper(McnFileWrapper.STATE_DOWNLOADING, null))
            when (val result = repository.downloadMcnFiles(application.value?.downloadLink ?: "")) {
                is OperationResult.Success -> {
                    saveFile(result.data)
                }
                is OperationResult.Error -> {
                    _mcnDownloadState.postValue(McnFileWrapper(McnFileWrapper.STATE_DOWNLOAD_FAIL))
                }
            }
        }
    }

    private val externalFileDir = application.getExternalFilesDir(null).toString()
    private fun saveFile(body: ResponseBody): Boolean {
        // Standard shitty Java boilerplate code just to save a file
        return try {
            val fileLocation =
                externalFileDir + File.separator.toString() + "saved-mcn.zip"
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded = 0L
                inputStream = body.byteStream()
                outputStream = FileOutputStream(fileLocation)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                _mcnDownloadState.postValue(
                    McnFileWrapper(
                        McnFileWrapper.STATE_DOWNLOADED,
                        fileLocation
                    )
                )
                true
            } catch (e: IOException) {
                _mcnDownloadState.postValue(
                    McnFileWrapper(
                        McnFileWrapper.STATE_DOWNLOAD_FAIL,
                        null
                    )
                )
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    private val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    fun formatAmount(amount: Int): String {
        return "₹${formatter.format(amount)}"
    }

    init {
        loadStatus()
    }
}