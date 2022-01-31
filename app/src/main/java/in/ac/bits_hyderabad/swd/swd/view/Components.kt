package `in`.ac.bits_hyderabad.swd.swd.view

import `in`.ac.bits_hyderabad.swd.swd.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun HeaderText(modifier: Modifier, text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5,
        color = colorResource(id = R.color.colorTextPrimary),
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun SubHeaderText(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.h6,
        color = colorResource(id = R.color.colorTextSecondary),
    )
}

@Composable
fun ServerConnectionError(
    modifier: Modifier = Modifier,
    errorText: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            painter = painterResource(id = R.drawable.ic_internet_error),
            contentScale = ContentScale.FillWidth,
            contentDescription = "Internet error doodle"
        )
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
            text = errorText,
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
fun EmptyList(
    modifier: Modifier = Modifier,
    text: String,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
            text = text,
            color = colorResource(id = R.color.colorTextPrimary),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
        Image(
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = R.drawable.ic_empty_list),
            contentDescription = "Empty list doodle"
        )
    }
}

@Composable
fun CenteredView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Preview
@Composable
fun PreviewServerConnectionError() {
    MdcTheme {
        EmptyList(text = "There are no documents available currently")
    }
}