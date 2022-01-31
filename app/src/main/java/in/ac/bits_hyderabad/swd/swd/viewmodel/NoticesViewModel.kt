package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.data.NoticesPagingSource
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class NoticesViewModel @Inject constructor(
    private val repository: AppDataSource
) : ViewModel() {

    val noticesPagingFlow = Pager(PagingConfig(pageSize = NoticesPagingSource.PAGING_LIMIT)) {
        NoticesPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

}