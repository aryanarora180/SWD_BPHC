package `in`.ac.bits_hyderabad.swd.swd

import `in`.ac.bits_hyderabad.swd.swd.helper.DataStoreUtils
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStoreUtils: DataStoreUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            dataStoreUtils.getAuthToken().isNullOrEmpty()
        ) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}