package `in`.ac.bits_hyderabad.swd.swd.view

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.ImportantLink
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.HomeViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            HomeScreen(viewModel)
        }
    }
}

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    MdcTheme {
        HomeScreenView(cardsList = homeViewModel.cardsList)
    }
}

@Composable
fun HomeScreenView(cardsList: List<ImportantLink>) {
    Surface(color = colorResource(id = R.color.colorBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderText(
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
                text = stringResource(id = R.string.title_home)
            )
            SubHeaderText(
                text = "Quick access",
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )
            LinkCards(cardsList)
        }
    }
}

@Composable
fun LinkCards(cardsList: List<ImportantLink>) {
    Column(modifier = Modifier.fillMaxSize()) {
        CardsRow(firstLink = cardsList[0], secondLink = cardsList[1])
        Spacer(modifier = Modifier.size(16.dp))
        CardsRow(firstLink = cardsList[2], secondLink = cardsList[3])
    }
}

@Composable
fun CardsRow(firstLink: ImportantLink, secondLink: ImportantLink) {
    Row {
        LinkCard(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp),
            link = firstLink
        )
        LinkCard(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp, start = 8.dp),
            link = secondLink
        )
    }
}

@Composable
fun LinkCard(modifier: Modifier = Modifier, link: ImportantLink) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { context.startActivity(link.onClickIntent) },
        backgroundColor = colorResource(id = R.color.colorSecondaryBackground)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = link.imageResource),
                contentDescription = link.text,
                modifier = Modifier
                    .padding(start = 16.dp, top = 24.dp, bottom = 24.dp)
                    .size(24.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.colorIconsTint))
            )
            Text(
                text = link.text,
                style = MaterialTheme.typography.body1,
                color = colorResource(id = R.color.colorTextPrimary),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    MdcTheme {
        HomeScreenView(
            listOf(
                ImportantLink(R.drawable.outline_erp_24, "ERP", Intent()),
                ImportantLink(R.drawable.logo_td, "TD", Intent()),
                ImportantLink(
                    R.drawable.outline_library_24,
                    "Library OPAC",
                    Intent()
                ),
                ImportantLink(R.drawable.outline_assignment_24, "CMS app", Intent()),
            )
        )
    }
}

