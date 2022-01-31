package `in`.ac.bits_hyderabad.swd.swd.data

import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState

class NoticesPagingSource(
    val appDataSource: AppDataSource
) : PagingSource<Int, Notice>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notice> {
        val nextPage = params.key ?: PAGING_START_INDEX

        return when (val response = appDataSource.getNotices(nextPage, PAGING_LIMIT)) {
            is OperationResult.Success -> {
                LoadResult.Page(
                    data = response.data,
                    prevKey = null, // Only paging forward.
                    nextKey = if (response.data.isEmpty() || response.data.size < PAGING_LIMIT) null else nextPage + PAGING_LIMIT
                )
            }

            is OperationResult.Error -> {
                LoadResult.Error(Exception(response.getErrorMessage()))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Notice>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(PAGING_LIMIT)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(PAGING_LIMIT)
        }
    }

    companion object {
        private const val PAGING_START_INDEX = 0
        const val PAGING_LIMIT = 10
    }
}