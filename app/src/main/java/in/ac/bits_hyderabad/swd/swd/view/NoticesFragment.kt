package `in`.ac.bits_hyderabad.swd.swd.view

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.SWDFirebaseMessagingService
import `in`.ac.bits_hyderabad.swd.swd.data.Notice
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter.Companion.FORMAT_HALF_DAY_NAME_HALF_MONTH_DAY_TIME
import `in`.ac.bits_hyderabad.swd.swd.helper.NotificationRegistrationWorker
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.NoticesViewModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import java.util.*

@AndroidEntryPoint
class NoticesFragment : Fragment() {

    private val viewModel by viewModels<NoticesViewModel>()

    @ExperimentalCoilApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            NoticesScreen(
                noticesViewModel = viewModel,
                openUrl = {
                    if (it.isNotEmpty() && it != 0.toString()) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                    }
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val registerTokenRequest =
                    OneTimeWorkRequestBuilder<NotificationRegistrationWorker>()
                        .setInputData(Data.Builder().apply {
                            putString(SWDFirebaseMessagingService.KEY_NEW_TOKEN, task.result)
                        }.build())
                        .build()
                WorkManager.getInstance(requireContext()).enqueue(registerTokenRequest)
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun NoticesScreen(noticesViewModel: NoticesViewModel = viewModel(), openUrl: (String) -> Unit) {
    MdcTheme {
        NoticesScreenView(noticesViewModel.noticesPagingFlow, openUrl = openUrl)
    }
}

@ExperimentalCoilApi
@Composable
fun NoticesScreenView(notices: Flow<PagingData<Notice>>, openUrl: (String) -> Unit) {
    Surface(color = colorResource(id = R.color.colorBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderText(
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                text = stringResource(id = R.string.notices)
            )
            NoticesList(
                notices = notices,
                openUrl = openUrl
            )
        }
    }
}

@ExperimentalCoilApi
@Composable
fun NoticesList(notices: Flow<PagingData<Notice>>, openUrl: (String) -> Unit) {
    val lazyNotices: LazyPagingItems<Notice> = notices.collectAsLazyPagingItems()
    LazyColumn {
        lazyNotices.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        CenteredView(modifier = Modifier.fillParentMaxSize()) {
                            CircularProgressIndicator()
                        }
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    val e = lazyNotices.loadState.refresh as LoadState.Error
                    item {
                        CenteredView(modifier = Modifier.fillParentMaxSize()) {
                            ServerConnectionError(
                                errorText = e.error.message ?: "",
                                onRetryClick = ::retry
                            )
                        }
                    }
                }

                loadState.append is LoadState.Loading -> {
                    item {
                        Row(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                loadState.append is LoadState.Error -> {
                    val e = lazyNotices.loadState.append as LoadState.Error
                    item {
                        AppendError(
                            errorMessage = e.error.message ?: "",
                            onRetryClick = ::retry,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        items(lazyNotices) { notice ->
            if (notice != null) {
                NoticeItem(
                    notice = notice,
                    onAttachmentClick = { openUrl(notice.attachment) },
                    onMeetLinkClick = { openUrl(notice.meetLink) }
                )
            }
        }
    }
}

@Composable
fun AppendError(modifier: Modifier = Modifier, errorMessage: String, onRetryClick: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
            text = errorMessage,
            color = colorResource(id = R.color.colorTextPrimary),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetryClick) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Composable
fun EventBadge(modifier: Modifier = Modifier) {
    Text(
        text = "Event",
        style = MaterialTheme.typography.caption,
        color = colorResource(id = R.color.colorTextPrimary),
        modifier = modifier
            .background(
                color = colorResource(id = R.color.colorAccent),
                shape = RoundedCornerShape(
                    corner = CornerSize(24.dp)
                )
            )
            .padding(vertical = 2.dp, horizontal = 4.dp)
    )
}

@Composable
fun NoticePostedBy(
    postedByIconUrl: String,
    postedBy: String,
    timePosted: Long,
    isEvent: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (postedByIconUrl.isNotEmpty() && postedByIconUrl != 0.toString()) {
                Image(
                    painter = rememberImagePainter(
                        data = postedByIconUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            crossfade(true)
                        }
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        .size(24.dp),
                    contentDescription = null
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }

            val postedDate = remember {
                DateUtils.getRelativeTimeSpanString(
                    timePosted * 1000,
                    Calendar.getInstance().timeInMillis,
                    DateUtils.MINUTE_IN_MILLIS
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Text(
                    text = postedBy,
                    style = MaterialTheme.typography.body2,
                    color = colorResource(id = R.color.colorTextPrimary)
                )
                Text(
                    text = "Posted $postedDate",
                    style = MaterialTheme.typography.caption,
                    color = colorResource(id = R.color.colorTextSecondary)
                )
            }
        }

        if (isEvent) {
            EventBadge(modifier = Modifier.padding(end = 16.dp))
        }
    }
}

@Composable
fun NoticeEventTime(
    eventTime: Long
) {
    val eventDate = remember {
        DateFormatter().getFormattedDate(
            eventTime * 1000,
            FORMAT_HALF_DAY_NAME_HALF_MONTH_DAY_TIME
        )
    }
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = colorResource(id = R.color.colorTextPrimary)
        )
        Text(
            text = "Scheduled for $eventDate",
            color = colorResource(id = R.color.colorTextPrimary),
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@ExperimentalCoilApi
@Composable
fun NoticeBody(
    title: String,
    body: String,
    imageUrl: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = colorResource(id = R.color.colorTextPrimary),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = body,
            color = colorResource(id = R.color.colorTextSecondary),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (imageUrl.isNotEmpty() && imageUrl != 0.toString()) {
            Spacer(modifier = Modifier.size(8.dp))
            Image(
                painter = rememberImagePainter(
                    data = imageUrl,
                    builder = {
                        crossfade(true)
                        error(R.drawable.outline_error_outline_24)
                    }),
                modifier = Modifier
                    .size(width = 400.dp, height = 200.dp)
                    .padding(start = 16.dp, end = 16.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = null
            )
        }
    }
}

@Composable
fun NoticeActions(
    attachment: String,
    onAttachmentClick: () -> Unit,
    meetLink: String,
    onMeetLinkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (attachment.isNotEmpty() && attachment != 0.toString()) {
            Button(onClick = onAttachmentClick) {
                Icon(
                    Icons.Outlined.Attachment,
                    contentDescription = null,
                    tint = colorResource(id = R.color.colorTextPrimary)
                )
                Text(
                    text = "View file",
                    color = colorResource(id = R.color.colorTextPrimary),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        if (meetLink.isNotEmpty() && meetLink != 0.toString())
            Button(onClick = onMeetLinkClick) {
                Icon(
                    Icons.Outlined.Groups,
                    contentDescription = null,
                    tint = colorResource(id = R.color.colorTextPrimary)
                )
                Text(
                    text = "Open meet",
                    color = colorResource(id = R.color.colorTextPrimary),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
    }
}

@ExperimentalCoilApi
@Composable
fun NoticeItem(
    notice: Notice,
    onAttachmentClick: () -> Unit,
    onMeetLinkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        backgroundColor = colorResource(id = R.color.colorSecondaryBackground),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            NoticePostedBy(
                postedByIconUrl = notice.postedByIcon,
                postedBy = notice.postedBy,
                timePosted = notice.timePosted,
                isEvent = notice.isEvent == 1
            )
            Spacer(modifier = Modifier.size(16.dp))
            NoticeBody(
                title = notice.title,
                body = notice.body,
                imageUrl = notice.image
            )
            Spacer(modifier = Modifier.size(16.dp))
            if (notice.isEvent == 1) {
                NoticeEventTime(eventTime = notice.eventTime)
                Spacer(modifier = Modifier.size(16.dp))
            }
            NoticeActions(
                attachment = notice.attachment,
                onAttachmentClick = onAttachmentClick,
                meetLink = notice.meetLink,
                onMeetLinkClick = onMeetLinkClick
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@ExperimentalCoilApi
@Preview
@Composable
fun PreviewNoticeItem() {
    MdcTheme {
        NoticeItem(
            notice = Notice(
                noticeId = 0,
                title = "This is the title for a preview notice",
                body = "This is the body for a preview notice\nwith multiple lines\nThank you",
                isEvent = 1,
                image = "asdasda",
                attachment = "",
                timePosted = 1000000000,
                meetLink = "asdsa",
                eventTime = 0,
                postedBy = "SWD Nucleus",
                postedByIcon = "",
            ),
            onAttachmentClick = {},
            onMeetLinkClick = {}
        )
    }
}
