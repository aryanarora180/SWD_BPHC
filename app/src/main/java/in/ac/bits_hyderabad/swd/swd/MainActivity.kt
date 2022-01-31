package `in`.ac.bits_hyderabad.swd.swd

import `in`.ac.bits_hyderabad.swd.swd.helper.DataStoreUtils
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStoreUtils: DataStoreUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_app_bar_nav)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottom_app_bar.setNavigationOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottom_app_bar_nav.setupWithNavController(navController)

        bottom_app_bar.setOnMenuItemClickListener { menuItem ->
            return@setOnMenuItemClickListener when (menuItem.itemId) {
                R.id.sign_out -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.sign_out_title)
                        .setMessage(R.string.sign_out_message)
                        .setPositiveButton(R.string.sign_out_positive) { _, _ ->
                            dataStoreUtils.signOut()

                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .requestIdToken(getString(R.string.web_client_id))
                                    .build()
                            GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }
                        .setNeutralButton(R.string.sign_out_neutral) { _, _ ->
                            //Do nothing
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
    }
}