package `in`.ac.bits_hyderabad.swd.swd.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel

@SuppressLint("NullSafeMutableLiveData")
class MessRegistrationViewModel(application: Application) : AndroidViewModel(application) {

//    private val myApplication = application as MyApplication
//    private val uid = myApplication.sharedPrefs.getUid()
//    private val token = myApplication.sharedPrefs.getToken()
//
//    var registrationStatusIsLoading: Boolean = false
//    var registrationStatusError: Int = Entities.STATUS_CUSTOM_LOADING
//    private var registrationStatus: MutableLiveData<MessRegCurrentState> = MutableLiveData()
//    fun getRegistrationStatus(): LiveData<MessRegCurrentState> {
//        registrationStatusIsLoading = true
//        MessApi.messRetrofitService.getMessRegDetails().enqueue(object :
//            Callback<MessRegCurrentState> {
//            override fun onFailure(call: Call<MessRegCurrentState>, t: Throwable) {
//                registrationStatusIsLoading = false
//                registrationStatusError = Entities.UNABLE_TO_CONNECT
//                registrationStatus.value = null
//            }
//
//            override fun onResponse(
//                call: Call<MessRegCurrentState>,
//                response: Response<MessRegCurrentState>
//            ) {
//                registrationStatusIsLoading = false
//                registrationStatusError = response.code()
//                registrationStatus.value = response.body()
//            }
//        })
//        return registrationStatus
//    }
//
//    fun getRegistrationStatusErrorMessageStringId(): Int {
//        return when (registrationStatusError) {
//            Entities.STATUS_422_UNPROCESSABLE_ENTITY -> R.string.error_invalid_details
//            Entities.STATUS_500_INTERNAL_SERVER_ERROR -> R.string.error_server
//            else -> R.string.error_unable_to_connect
//        }
//    }
//
//    var registeringLoading: Boolean = false
//    var registeringError: Int = Entities.STATUS_CUSTOM_LOADING
//    private var messRegistrationResponse: MutableLiveData<MessRegistrationResponse> =
//        MutableLiveData()
//
//    fun registerToMess(messToRegister: Int): LiveData<MessRegistrationResponse> {
//        registrationStatusIsLoading = true
//        MessApi.messRetrofitService.registerForMess(uid, token, messToRegister).enqueue(object :
//            Callback<MessRegistrationResponse> {
//            override fun onFailure(call: Call<MessRegistrationResponse>, t: Throwable) {
//                registeringLoading = false
//                registeringError = Entities.UNABLE_TO_CONNECT
//                messRegistrationResponse.value = null
//            }
//
//            override fun onResponse(
//                call: Call<MessRegistrationResponse>,
//                response: Response<MessRegistrationResponse>
//            ) {
//                registeringLoading = false
//                registeringError = response.code()
//                messRegistrationResponse.value = response.body()
//            }
//        })
//        return messRegistrationResponse
//    }
//
//    fun getMessRegistrationStatusErrorMessageStringId(): Int {
//        return when (registrationStatusError) {
//            Entities.STATUS_422_UNPROCESSABLE_ENTITY -> R.string.error_invalid_details
//            Entities.STATUS_500_INTERNAL_SERVER_ERROR -> R.string.error_server
//            else -> R.string.error_unable_to_connect
//        }
//    }

}