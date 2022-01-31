package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.DocumentWrapper
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.data.SingleLiveEvent
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.*
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val repository: AppDataSource,
    application: Application
) : AndroidViewModel(application) {

    val isDataLoading = mutableStateOf(true)
    val isErrorState = mutableStateOf(false)
    var error = mutableStateOf("")
    val showDocumentsList = mutableStateOf(false)
    var documents = mutableStateOf(listOf<DocumentWrapper>())

    fun loadAvailableDocuments() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isDataLoading.value = true
                isErrorState.value = false
                showDocumentsList.value = false
            }
            when (val result = repository.getDocumentsAvailable()) {
                is OperationResult.Success -> {
                    val documentsList = mutableListOf<DocumentWrapper>()
                    result.data.forEach { document ->
                        documentsList.add(
                            DocumentWrapper(
                                document,
                                DocumentWrapper.STATE_NOT_DOWNLOADED
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        isDataLoading.value = false
                        isErrorState.value = false
                        showDocumentsList.value = true
                        documents.value = documentsList
                    }
                }
                is OperationResult.Error -> {
                    withContext(Dispatchers.Main) {
                        error.value = result.getErrorMessage()
                        isErrorState.value = true
                        isDataLoading.value = false
                        showDocumentsList.value = true
                    }
                }
            }
        }
    }

    init {
        loadAvailableDocuments()
    }

    private val _onErrorMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onMessageError: LiveData<SingleLiveEvent<String>>
        get() = _onErrorMessage

    fun downloadDocument(document: DocumentWrapper) {
        viewModelScope.launch(Dispatchers.IO) {
            setStatusForDocument(document, DocumentWrapper.STATE_DOWNLOADING)
            when (val result = repository.downloadDocument(document.documentDetails.key)) {
                is OperationResult.Success -> {
                    saveFile(document, result.data)
                }
                is OperationResult.Error -> {
                    _onErrorMessage.postValue(SingleLiveEvent(result.getErrorMessage()))
                    setStatusForDocument(document, DocumentWrapper.STATE_DOWNLOAD_FAIL)
                }
            }
        }
    }

    private val externalFileDir = application.getExternalFilesDir(null).toString()
    private fun saveFile(document: DocumentWrapper, body: ResponseBody): Boolean {
        // Standard shitty gae Java boilerplate code just to save a file
        return try {
            val fileLocation =
                externalFileDir + File.separator.toString() + "${document.documentDetails.key}.pdf"
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
                setStatusForDocument(document, DocumentWrapper.STATE_DOWNLOADED, fileLocation)
                true
            } catch (e: IOException) {
                setStatusForDocument(document, DocumentWrapper.STATE_DOWNLOAD_FAIL)
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    private fun setStatusForDocument(
        documentToUpdate: DocumentWrapper,
        status: Long,
        uri: String? = null
    ) {
        val documentsList = mutableListOf<DocumentWrapper>()
        documents.value.forEach { document ->
            if (documentToUpdate.documentDetails.key == document.documentDetails.key) {
                documentsList.add(DocumentWrapper(documentToUpdate.documentDetails, status, uri))
            } else {
                documentsList.add(document)
            }
        }
        documents.value = documentsList
    }

}