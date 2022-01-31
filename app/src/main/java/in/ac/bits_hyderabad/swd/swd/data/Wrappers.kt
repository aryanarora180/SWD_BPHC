package `in`.ac.bits_hyderabad.swd.swd.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class FaqsWrapper(
    val lastUpdated: Long,
    val faqs: List<FaqGroupWrapper>,
    val formattedFaqsToSearch: List<FaqQuestionWrapper>,
)

data class FaqGroupWrapper(
    val groupName: String,
    val topicNames: String,
    val questions: List<FaqQuestionWrapper>,
) {
    companion object {
        fun getMainTopicsList(topicsList: List<MainTopic>): String {
            var topics = ""
            topicsList.forEach {
                topics += "\u2022 ${it.mainTopic}\n"
            }
            if (topics.isEmpty()) {
                return ""
            }
            return topics.substring(0, topics.length - 1)
        }

        fun getFormattedFaqsList(
            groupIndex: Int,
            groupName: String,
            topics: List<MainTopic>
        ): List<FaqQuestionWrapper> {
            val formattedFaqs = mutableListOf<FaqQuestionWrapper>()

            var index = 0
            topics.forEach { mainTopic ->
                mainTopic.questions.forEach { qa ->
                    formattedFaqs.add(
                        FaqQuestionWrapper(
                            groupName,
                            groupIndex,
                            mainTopic.mainTopic,
                            qa,
                            index,
                        )
                    )
                    index++
                }
            }
            return formattedFaqs
        }
    }
}

data class FaqQuestionWrapper(
    val groupName: String,
    val groupIndex: Int,
    val topicName: String,
    val question: QuestionAnswer,
    val questionIndexInGroup: Int,
)

data class FileDetails(
    val fileName: String,
    val fileSize: String,
    val sizeExceeded: Boolean
)

data class McnFileWrapper(
    var state: Long,
    var fileLocation: String? = null
) {
    companion object {
        const val STATE_NOT_DOWNLOADED = 100L
        const val STATE_DOWNLOADING = 101L
        const val STATE_DOWNLOAD_FAIL = 102L
        const val STATE_DOWNLOADED = 103L
    }
}

data class DocumentWrapper(
    val documentDetails: Document,
    var state: Long,
    var fileLocation: String? = null
) {
    companion object {
        const val STATE_NOT_DOWNLOADED = 100L
        const val STATE_DOWNLOADING = 101L
        const val STATE_DOWNLOAD_FAIL = 102L
        const val STATE_DOWNLOADED = 103L
    }
}

@Parcelize
data class GoodieOrderDetails(
    val goodieId: String,
    val sizes: GoodiesSizes,
    val netQuantity: Int,
    val totalAmount: Int
) : Parcelable

@Parcelize
data class OtherAdvancesPaymentDetails(
    val isSuccessful: Boolean,
    val error: String?,
    val paymentAmount: Int
) : Parcelable