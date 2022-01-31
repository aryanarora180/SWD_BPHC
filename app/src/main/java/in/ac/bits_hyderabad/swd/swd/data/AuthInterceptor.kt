package `in`.ac.bits_hyderabad.swd.swd.data

import `in`.ac.bits_hyderabad.swd.swd.helper.DataStoreUtils
import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val dataStoreUtils = DataStoreUtils(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        // Build the request with the header as the auth token, if it exists
        val mainRequest = chain.request().newBuilder()
        val authToken = dataStoreUtils.getAuthToken()
        authToken?.let {
            mainRequest.addHeader("Authorization", it)
        }

        // Proceed with the request now
        val mainResponse = chain.proceed(mainRequest.build())

        // Intercept the response and if there's an authorization token in the response, save it
        val newToken = mainResponse.header("Authorization", null)
        if (!newToken.isNullOrEmpty()) {
            dataStoreUtils.storeAuthToken(newToken)
        }

        return mainResponse
    }
}