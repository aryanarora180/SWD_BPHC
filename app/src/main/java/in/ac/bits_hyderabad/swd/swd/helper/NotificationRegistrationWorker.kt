package `in`.ac.bits_hyderabad.swd.swd.helper

import `in`.ac.bits_hyderabad.swd.swd.SWDFirebaseMessagingService.Companion.KEY_NEW_TOKEN
import `in`.ac.bits_hyderabad.swd.swd.model.AppRepository
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope

class NotificationRegistrationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = AppRepository(context)
    private val dataStoreUtils = DataStoreUtils(context)

    override suspend fun doWork(): Result = coroutineScope {
        try {
            if (!(dataStoreUtils.getAuthToken()
                    .isNullOrEmpty())
            ) {
                repository.registerForNotifications(inputData.getString(KEY_NEW_TOKEN) ?: "null")
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}