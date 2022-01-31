package `in`.ac.bits_hyderabad.swd.swd.data

sealed class OperationResult<out T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Error(val status: Int, val message: String?) : OperationResult<Nothing>() {
        fun getErrorMessage() = message ?: getErrorMessage(status)
    }

    companion object {
        const val ERROR_CODE_UNABLE_TO_GET_NOTIFICATION_TOKEN = 999
        const val ERROR_CODE_UNDETERMINED = 1000

        fun getErrorMessage(errorCode: Int): String {
            return when (errorCode) {
                400 -> "Unexplained bad request"
                401 -> "Your password has changed. Please sign in again"
                413 -> "File size too large"
                422 -> "Invalid details"
                500 -> "A server error occurred. Please try again later"
                502 -> "Bad gateway"
                ERROR_CODE_UNABLE_TO_GET_NOTIFICATION_TOKEN -> "Unable to get device token for notifications. Please try again later"
                else -> "Unable to connect to our servers"
            }
        }
    }
}