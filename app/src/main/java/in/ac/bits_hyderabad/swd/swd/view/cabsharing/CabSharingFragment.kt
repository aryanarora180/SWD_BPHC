package `in`.ac.bits_hyderabad.swd.swd.view.cabsharing

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.CabSharingGroup
import `in`.ac.bits_hyderabad.swd.swd.data.Route
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter
import `in`.ac.bits_hyderabad.swd.swd.helper.DateFormatter.Companion.FORMAT_HALF_DAY_NAME_HALF_MONTH_DAY_TIME
import `in`.ac.bits_hyderabad.swd.swd.view.HeaderText
import `in`.ac.bits_hyderabad.swd.swd.view.SubHeaderText
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.CabSharingViewModel
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Today
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CabSharingFragment : Fragment() {

    private val viewModel by viewModels<CabSharingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            // TODO: Remove after API is ready
            PreviewCabSharingBody()
        }
    }
}

@Composable
fun CabSharingContent(viewModel: CabSharingViewModel = viewModel()) {
    MdcTheme {

    }
}

@Composable
fun CabSharingHomeBody(
    routes: List<Route>,
    groups: List<CabSharingGroup>,
    onUserRequestTap: (CabSharingGroup) -> Unit
) {
    val context = LocalContext.current

    var selectedRoute by remember { mutableStateOf(routes[0]) }

    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    datePicker.addOnPositiveButtonClickListener { date ->
        selectedDate = "March 8, 2021"
    }

    val (spaceFor, setSpaceFor) = remember {
        mutableStateOf("1")
    }

    Surface(color = colorResource(id = R.color.colorBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderText(
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
                text = "Cab sharing"
            )
            CabSharingSearch(
                selectedRoute = selectedRoute,
                onRouteSelected = {
                    selectedRoute = it
                },
                routes = routes,
                date = selectedDate,
                onSelectDate = {
//                    datePicker.show(context.supportFragmentManager, "date")
                },
                time = selectedTime,
                onSelectTime = {

                },
                spaceFor = spaceFor,
                onSpaceForChange = setSpaceFor
            )
            UserRequests(
                modifier = Modifier.padding(top = 32.dp),
                groups = groups,
                onUserRequestTap = onUserRequestTap
            )
        }
    }
}

@Composable
fun CabSharingSearch(
    selectedRoute: Route,
    onRouteSelected: (Route) -> Unit,
    routes: List<Route>,
    date: String,
    onSelectDate: () -> Unit,
    time: String,
    onSelectTime: () -> Unit,
    spaceFor: String,
    onSpaceForChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SelectRouteDropdown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            routes = routes,
            selectedRoute = selectedRoute,
            onSelectRoute = onRouteSelected
        )
        SelectDateTimeFields(
            modifier = Modifier.padding(top = 12.dp),
            date = date,
            onSelectDate = onSelectDate,
            time = time,
            onSelectTime = onSelectTime
        )
        OutlinedTextField(
            value = spaceFor,
            onValueChange = onSpaceForChange,
            label = {
                Text(text = "Space for")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Button(onClick = { /*TODO: Start search*/ }, modifier = Modifier.padding(end = 16.dp)) {
                Text(text = "Search")
            }
        }
    }
}

@Composable
fun SelectRouteDropdown(
    modifier: Modifier = Modifier,
    routes: List<Route>,
    selectedRoute: Route,
    onSelectRoute: (Route) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedRoute.getDisplayRoute(),
        onValueChange = {},
        readOnly = true,
        label = {
            Text(text = "Route")
        },
        trailingIcon = {
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart)
            ) {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Outlined.ArrowDropDown,
                        contentDescription = "Select route"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    routes.forEach { route ->
                        DropdownMenuItem(onClick = {
                            onSelectRoute(route)
                            expanded = false
                        }) {
                            Text(
                                text = route.getDisplayRoute()
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun SelectDateTimeFields(
    modifier: Modifier = Modifier,
    date: String,
    onSelectDate: () -> Unit,
    time: String,
    onSelectTime: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(text = "Date")
            },
            trailingIcon = {
                IconButton(onClick = onSelectDate) {
                    Icon(
                        imageVector = Icons.Outlined.Today,
                        contentDescription = "Select a date"
                    )
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp)
        )

        OutlinedTextField(
            value = time,
            label = {
                Text(text = "Time")
            },
            trailingIcon = {
                IconButton(onClick = onSelectTime) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Select a time"
                    )
                }
            },
            readOnly = true,
            onValueChange = {},
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 16.dp)
        )
    }
}

@Composable
fun UserRequests(
    modifier: Modifier = Modifier,
    groups: List<CabSharingGroup>,
    onUserRequestTap: (CabSharingGroup) -> Unit
) {
    if (groups.isNotEmpty()) {
        Column(modifier = modifier) {
            SubHeaderText(text = "Your requests/rides", modifier = Modifier.padding(start = 16.dp))

            groups.forEach { group ->
                RequestCard(
                    modifier = Modifier.fillMaxWidth(),
                    group = group,
                    onTap = { onUserRequestTap(group) }
                )
            }
        }
    }
}


@Composable
fun RequestCard(
    modifier: Modifier = Modifier,
    group: CabSharingGroup,
    onTap: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onTap() },
        backgroundColor = colorResource(id = R.color.colorSecondaryBackground)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = group.route.getDisplayRoute(),
                    color = colorResource(id = R.color.colorTextPrimary),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )

                val date = remember {
                    DateFormatter().getFormattedDate(
                        date = group.date.time,
                        type = FORMAT_HALF_DAY_NAME_HALF_MONTH_DAY_TIME
                    )
                }
                Text(
                    text = date,
                    color = colorResource(id = R.color.colorTextSecondary),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 1.dp, bottom = 16.dp)
                )
            }
            if (group.isHost) {
                HostTag(
                    Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}

@Composable
fun HostTag(
    modifier: Modifier = Modifier,
    text: String = "Host"
) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption,
        modifier = modifier
            .background(
                color = colorResource(id = R.color.colorAccent),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewCabSharingBody() {
    MdcTheme {
        CabSharingHomeBody(
            routes = listOf(
                Route(0, "Campus", "Airport"),
                Route(1, "Airport", "Campus"),
                Route(2, "Campus", "Railway station"),
                Route(3, "Railway station", "Campus"),
            ),
            groups = listOf(
                CabSharingGroup(
                    groupId = 213,
                    hostUid = "f20191204",
                    isHost = true,
                    date = Date(),
                    spaceFor = 2,
                    spaceTaken = 1,
                    route = Route(1, "Airport", "Campus"),
                    fare = 1400,
                ),
                CabSharingGroup(
                    groupId = 213,
                    hostUid = "f20191205",
                    isHost = false,
                    date = Date(),
                    spaceFor = 2,
                    spaceTaken = 1,
                    route = Route(0, "Campus", "Airport"),
                    fare = 1400,
                ),
            ),
            onUserRequestTap = {}
        )
    }
}

