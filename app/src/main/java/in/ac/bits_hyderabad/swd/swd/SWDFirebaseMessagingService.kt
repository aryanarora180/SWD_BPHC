package `in`.ac.bits_hyderabad.swd.swd

import `in`.ac.bits_hyderabad.swd.swd.helper.NotificationRegistrationWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService

class SWDFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val KEY_NEW_TOKEN = "new-token"
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        val registerTokenRequest = OneTimeWorkRequestBuilder<NotificationRegistrationWorker>()
            .setInputData(createTokenData(newToken))
            .build()
        WorkManager.getInstance(application).enqueue(registerTokenRequest)
    }

    private fun createTokenData(newToken: String) = Data.Builder().apply {
        putString(KEY_NEW_TOKEN, newToken)
    }.build()

}