package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.FileDetails
import `in`.ac.bits_hyderabad.swd.swd.data.LiveErrorEvent
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.text.format.Formatter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class ApplyForMcnViewModel @Inject constructor(
    private val repository: AppDataSource,
    application: Application
) : AndroidViewModel(application) {

    private val _application = application

    var selectedUri: Uri? = null

    private val _isSendingRequest = MutableLiveData<Boolean>()
    val isSendingRequest: LiveData<Boolean>
        get() = _isSendingRequest

    private val _applySuccess = MutableLiveData<Boolean>()
    val applySuccess: LiveData<Boolean>
        get() = _applySuccess

    private val _applyError = MutableLiveData<LiveErrorEvent>()
    val applyError: LiveData<LiveErrorEvent>
        get() = _applyError

    private val _zipDetails = MutableLiveData<FileDetails>()
    val fileDetails: LiveData<FileDetails>
        get() = _zipDetails

    fun applyForMcn(
        category: String,
        fatherSalary: String,
        motherSalary: String,
        documentsSubmitted: String,
        isLoan: Boolean,
        cgpa: String
    ) {
        if (validateData(category, fatherSalary, motherSalary, documentsSubmitted, cgpa)) {
            viewModelScope.launch(Dispatchers.IO) {
                _isSendingRequest.postValue(true)
                when (val result = repository.applyForMcn(
                    fatherSalary.toInt(),
                    motherSalary.toInt(),
                    category,
                    getFile(selectedUri!!), // We've ensured that _selectedUri is not null, so !! is fine
                    documentsSubmitted,
                    if (isLoan) 1 else 0,
                    cgpa
                )) {
                    is OperationResult.Success -> {
                        _applySuccess.postValue(true)
                    }
                    is OperationResult.Error -> {
                        _applySuccess.postValue(false)
                        _applyError.postValue(
                            LiveErrorEvent(
                                result.message ?: OperationResult.getErrorMessage(
                                    result.status
                                )
                            )
                        )
                    }
                }
                _isSendingRequest.postValue(false)
            }
        }
    }

    fun validateZip(fileUri: Uri?) {
        selectedUri = fileUri
        if (fileUri != null) {
            val cursor = _application.contentResolver.query(fileUri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayName: String =
                        it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    val size = if (!it.isNull(sizeIndex)) {
                        it.getString(sizeIndex).toLong()
                    } else {
                        -1
                    }
                    _zipDetails.postValue(
                        FileDetails(
                            displayName,
                            Formatter.formatFileSize(_application, size),
                            size > 10485760 // No. of bytes (10 MB)
                        )
                    )
                }
            }
        } else {
            _zipDetails.postValue(
                FileDetails(
                    "",
                    "",
                    false
                )
            )
        }
    }

    private val externalFileDir = application.getExternalFilesDir(null).toString()
    private fun getFile(fileUri: Uri): MultipartBody.Part {
        /*
        What's basically happening is the URI scheme we get is a content resolver and it's possible
        that the document shared is from the cloud. So we create a copy of the file onto our Scooped
        storage directory which we have full access to. Then, we create a File object using that
        directory which can then be used to create the MultipartBody Part.
         */
        val fileLocation =
            externalFileDir + File.separator.toString() + "mcn.zip"

        val inputStream = _application.contentResolver.openInputStream(fileUri)!!
        val outputStream = FileOutputStream(File(fileLocation))
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            outputStream.write(buf, 0, len)
        }
        outputStream.close()
        inputStream.close()

        val file = File(fileLocation)
        val body = RequestBody.create(
            MediaType.parse(
                _application.contentResolver.getType(fileUri) ?: "application/zip"
            ), file
        )
        return MultipartBody.Part.createFormData("upload", file.name, body)
    }

    private fun validateData(
        category: String,
        fatherSalary: String,
        motherSalary: String,
        documentsSubmitted: String,
        cgpa: String
    ): Boolean {
        if (category.isEmpty()) {
            _applyError.postValue(LiveErrorEvent("Select a category"))
            return false
        }
        if (fatherSalary.isEmpty()) {
            _applyError.postValue(LiveErrorEvent("Father's salary not entered"))
            return false
        }
        if (motherSalary.isEmpty()) {
            _applyError.postValue(LiveErrorEvent("Mother's salary not entered"))
            return false
        }
        try {
            cgpa.toFloat().let {
                if (it < 0 || it > 10) {
                    _applyError.postValue(LiveErrorEvent("Invalid CGPA"))
                    return false
                }
            }
        } catch (e: NumberFormatException) {
            _applyError.postValue(LiveErrorEvent("Invalid CGPA"))
            return false
        }
        if (selectedUri == null || fileDetails.value?.sizeExceeded == true) {
            _applyError.postValue(LiveErrorEvent(_application.getString(R.string.no_valid_zip)))
            return false
        }
        if (documentsSubmitted.isEmpty()) {
            _applyError.postValue(LiveErrorEvent("Submitted documents not selected"))
            return false
        }
        return true
    }
}