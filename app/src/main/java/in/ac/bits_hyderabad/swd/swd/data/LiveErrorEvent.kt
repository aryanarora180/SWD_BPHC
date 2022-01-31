package `in`.ac.bits_hyderabad.swd.swd.data

class LiveErrorEvent(private val content: String) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): String? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}