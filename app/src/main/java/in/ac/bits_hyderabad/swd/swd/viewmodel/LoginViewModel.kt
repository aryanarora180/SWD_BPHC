package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult
import `in`.ac.bits_hyderabad.swd.swd.data.OperationResult.Companion.ERROR_CODE_UNABLE_TO_GET_NOTIFICATION_TOKEN
import `in`.ac.bits_hyderabad.swd.swd.data.SignInResult
import `in`.ac.bits_hyderabad.swd.swd.data.SingleLiveEvent
import `in`.ac.bits_hyderabad.swd.swd.helper.DataStoreUtils
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AppDataSource,
    private val dataStoreUtils: DataStoreUtils,
    application: Application
) : AndroidViewModel(application) {

    val googleSignInOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(application.getString(R.string.web_client_id))
            .build()

    var isGoogleSigningIn = mutableStateOf(false)
    var isCredentialsSigningIn = mutableStateOf(false)

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean>
        get() = _isAuthenticated

    private val _needsProfileCompletion = MutableLiveData<Boolean>()
    val needsProfileCompletion: LiveData<Boolean>
        get() = _needsProfileCompletion

    private val _onMessage = MutableLiveData<SingleLiveEvent<String>>()
    val onMessage: LiveData<SingleLiveEvent<String>>
        get() = _onMessage

    fun signInWithGoogle(idToken: String) {
        isGoogleSigningIn.value = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val notificationToken = task.result ?: "null"
                Log.e("Notifications", "Token: $notificationToken")
                viewModelScope.launch(Dispatchers.IO) {
                    when (val result =
                        repository.signInWithGoogle(idToken, notificationToken)) {
                        is OperationResult.Success -> {
                            if (result.data.isComplete == SignInResult.PROFILE_COMPLETED) {
                                _isAuthenticated.postValue(true)
                            } else {
                                _needsProfileCompletion.postValue(true)
                            }
                        }
                        is OperationResult.Error -> {
                            _onMessage.postValue(
                                SingleLiveEvent(
                                    result.message ?: OperationResult.getErrorMessage(
                                        result.status
                                    )
                                )
                            )
                            isGoogleSigningIn.value = false
                        }
                    }
                }
            } else {
                _onMessage.postValue(
                    SingleLiveEvent(
                        OperationResult.getErrorMessage(
                            ERROR_CODE_UNABLE_TO_GET_NOTIFICATION_TOKEN
                        )
                    )
                )
                isGoogleSigningIn.value = false
            }
        }
    }

    fun signInWithCredentials(uid: String, password: String) {
        isCredentialsSigningIn.value = true
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val notificationToken = task.result ?: "null"
                viewModelScope.launch(Dispatchers.IO) {
                    when (val result =
                        repository.signInWithCredentials(uid, password, notificationToken)) {
                        is OperationResult.Success -> {
                            if (result.data.isComplete == SignInResult.PROFILE_COMPLETED) {
                                _isAuthenticated.postValue(true)
                            } else {
                                _needsProfileCompletion.postValue(true)
                            }
                        }
                        is OperationResult.Error -> {
                            _onMessage.postValue(
                                SingleLiveEvent(
                                    result.message ?: OperationResult.getErrorMessage(
                                        result.status
                                    )
                                )
                            )
                        }
                    }
                    isCredentialsSigningIn.value = false
                }
            } else {
                _onMessage.postValue(
                    SingleLiveEvent(
                        OperationResult.getErrorMessage(
                            ERROR_CODE_UNABLE_TO_GET_NOTIFICATION_TOKEN
                        )
                    )
                )
                isCredentialsSigningIn.value = false
            }
        }
    }

    var showForgotPasswordDialog = mutableStateOf(false)
    fun showForgotPasswordDialog() {
        showForgotPasswordDialog.value = true
    }

    fun dismissForgotPasswordDialog() {
        showForgotPasswordDialog.value = false
    }

    var sendingPasswordResetRequest = mutableStateOf(false)

    fun sendPasswordResetRequest(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendingPasswordResetRequest.value = true
            when (val result =
                repository.sendPasswordResetRequest(uid)) {
                is OperationResult.Success -> {
                    dismissForgotPasswordDialog()
                    sendingPasswordResetRequest.value = false
                    _onMessage.postValue(
                        SingleLiveEvent("Password reset link successfully sent.")
                    )
                }
                is OperationResult.Error -> {
                    sendingPasswordResetRequest.value = false
                    _onMessage.postValue(
                        SingleLiveEvent(
                            result.message ?: OperationResult.getErrorMessage(
                                result.status
                            )
                        )
                    )
                }
            }
        }
    }
}