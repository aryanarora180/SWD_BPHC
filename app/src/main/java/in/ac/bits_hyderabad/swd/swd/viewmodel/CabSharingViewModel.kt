package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CabSharingViewModel @Inject constructor(
    private val repository: AppDataSource,
    application: Application
) : AndroidViewModel(application) {


}