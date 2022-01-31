package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.ImportantLink
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val timetableDivisionUrl = "https://td.bits-hyderabad.ac.in"
    private val erpUrl = "https://sis.erp.bits-pilani.ac.in/sisprd/signon.html"
    private val libraryOpacUrl = "http://libraryopac.bits-hyderabad.ac.in"
    private val cmsPlayStoreUrl = "https://play.google.com/store/apps/details?id=crux.bphc.cms"
    private val cmsPackageName = "crux.bphc.cms"

    private fun getTimetableDivisionIntent(): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(timetableDivisionUrl)
        return intent
    }

    private fun getLibraryOpacIntent(): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(libraryOpacUrl)
        return intent
    }

    private fun getErpIntent(): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(erpUrl)
        return intent
    }

    private val _packageManager = application.packageManager
    private fun getCmsIntent(): Intent {
        var intent: Intent? = _packageManager.getLaunchIntentForPackage(cmsPackageName)
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)

        if (intent == null) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(cmsPlayStoreUrl)
        }

        return intent
    }

    val cardsList = listOf(
        ImportantLink(R.drawable.outline_erp_24, "ERP", getErpIntent()),
        ImportantLink(R.drawable.logo_td, "TD", getTimetableDivisionIntent()),
        ImportantLink(R.drawable.outline_library_24, "Library OPAC", getLibraryOpacIntent()),
        ImportantLink(R.drawable.outline_assignment_24, "CMS app", getCmsIntent()),
    )
}