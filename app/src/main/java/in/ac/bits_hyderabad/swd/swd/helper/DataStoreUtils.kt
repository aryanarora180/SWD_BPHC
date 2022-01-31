package `in`.ac.bits_hyderabad.swd.swd.helper

import android.content.Context

class DataStoreUtils(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        "new_api_details", Context.MODE_PRIVATE
    )

    private val KEY_AUTH_TOKEN = "auth_token"

    fun getUid() = "uid"

    fun getAuthToken(): String? = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    fun storeAuthToken(newToken: String) {
        with(sharedPreferences.edit()) {
            this.putString(KEY_AUTH_TOKEN, newToken)
            apply()
        }
    }

    fun signOut() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}