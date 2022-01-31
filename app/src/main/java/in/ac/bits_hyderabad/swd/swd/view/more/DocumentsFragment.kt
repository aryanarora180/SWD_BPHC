package `in`.ac.bits_hyderabad.swd.swd.view.more

import `in`.ac.bits_hyderabad.swd.swd.BuildConfig
import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.Document
import `in`.ac.bits_hyderabad.swd.swd.data.DocumentWrapper
import `in`.ac.bits_hyderabad.swd.swd.data.DocumentWrapper.Companion.STATE_DOWNLOADED
import `in`.ac.bits_hyderabad.swd.swd.data.DocumentWrapper.Companion.STATE_DOWNLOADING
import `in`.ac.bits_hyderabad.swd.swd.data.DocumentWrapper.Companion.STATE_DOWNLOAD_FAIL
import `in`.ac.bits_hyderabad.swd.swd.data.DocumentWrapper.Companion.STATE_NOT_DOWNLOADED
import `in`.ac.bits_hyderabad.swd.swd.data.SingleLiveEvent
import `in`.ac.bits_hyderabad.swd.swd.view.*
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.DocumentsViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DocumentsFragment : Fragment() {

    private val viewModel by viewModels<DocumentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            DocumentsScreen(viewModel = viewModel) { document ->
                when (document.state) {
                    STATE_NOT_DOWNLOADED -> viewModel.downloadDocument(
                        document
                    )
                    STATE_DOWNLOAD_FAIL -> viewModel.downloadDocument(
                        document
                    )
                    STATE_DOWNLOADED -> openDocument(document)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onMessageError.observe(viewLifecycleOwner, documentDownloadErrorObserver)
    }

    private val documentDownloadErrorObserver = Observer<SingleLiveEvent<String>> {
        requireView().showSnackbarError(it)
    }

    private fun openDocument(document: DocumentWrapper) {
        if (!document.fileLocation.isNullOrEmpty()) {
            val file = File(document.fileLocation ?: "")
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    FileProvider.getUriForFile(
                        requireActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    ),
                    "application/pdf"
                )
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val viewerIntent = Intent.createChooser(intent, "Open PDF")
            startActivity(viewerIntent)
        }
    }
}

@Composable
fun DocumentsScreen(
    viewModel: DocumentsViewModel = viewModel(),
    onDocumentClick: (DocumentWrapper) -> Unit
) {
    val isDataLoading by viewModel.isDataLoading
    val isErrorState by viewModel.isErrorState
    val error by viewModel.error
    val showDocumentList by viewModel.showDocumentsList
    val documents by viewModel.documents

    MdcTheme {
        DocumentsScreenView(
            isLoading = isDataLoading,
            isErrorState = isErrorState,
            error = error,
            onRetryClick = {
                viewModel.loadAvailableDocuments()
            },
            showDocumentList = showDocumentList,
            documents = documents,
            onDocumentClick = onDocumentClick
        )
    }
}

@Composable
fun DocumentsScreenView(
    isLoading: Boolean = true,
    isErrorState: Boolean = false,
    error: String? = null,
    onRetryClick: () -> Unit,
    showDocumentList: Boolean = false,
    documents: List<DocumentWrapper>?,
    onDocumentClick: (DocumentWrapper) -> Unit,
) {
    Surface(color = colorResource(id = R.color.colorBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderText(
                modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
                text = stringResource(id = R.string.documents)
            )
            if (isLoading) {
                CenteredView(modifier = Modifier.weight(1f)) {
                    CircularProgressIndicator()
                }
            } else if (isErrorState) {
                error?.let { error ->
                    CenteredView(modifier = Modifier.weight(1f)) {
                        ServerConnectionError(errorText = error, onRetryClick = onRetryClick)
                    }
                }
            } else if (showDocumentList) {
                documents?.let {
                    if (it.isEmpty()) {
                        EmptyList(text = stringResource(id = R.string.no_documents_available))
                    } else {
                        DocumentsList(
                            modifier = Modifier.weight(1f),
                            documents = documents,
                            onDocumentClick = onDocumentClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentsList(
    modifier: Modifier = Modifier,
    documents: List<DocumentWrapper>,
    onDocumentClick: (DocumentWrapper) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(documents) { document ->
            DocumentListItem(document = document, onClick = {
                onDocumentClick(document)
            })
        }
    }
}

@Composable
fun DocumentListItem(document: DocumentWrapper, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        backgroundColor = colorResource(id = R.color.colorSecondaryBackground)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = document.documentDetails.name,
                color = colorResource(id = R.color.colorTextPrimary),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
                    .wrapContentWidth(Alignment.Start)
            )

            document.state.let {
                if (it in listOf(STATE_NOT_DOWNLOADED, STATE_DOWNLOAD_FAIL, STATE_DOWNLOADED)) {
                    Icon(
                        imageVector = when (it) {
                            STATE_DOWNLOAD_FAIL -> Icons.Outlined.Error
                            STATE_DOWNLOADED -> Icons.Outlined.Launch
                            else -> Icons.Outlined.Download
                        },
                        contentDescription = null,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDocumentsScreen() {
    MdcTheme {
        DocumentsScreenView(
            documents = listOf(
                DocumentWrapper(
                    Document(name = "ID card", key = "id"),
                    state = STATE_DOWNLOADING,
                    fileLocation = null
                ),
            ),
            onDocumentClick = { },
            onRetryClick = { },
            isLoading = false,
            isErrorState = false,
            error = "Unable to connect to the internet",
            showDocumentList = true
        )
    }
}