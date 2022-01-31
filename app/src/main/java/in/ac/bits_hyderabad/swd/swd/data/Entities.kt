package `in`.ac.bits_hyderabad.swd.swd.data

import android.content.Intent
import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat
import java.util.*

data class DataResponse<T>(
    @field:Json(name = "err") val errorOccurred: Boolean,
    @field:Json(name = "msg") val errorMessage: String,
    @field:Json(name = "data") val data: T
)

data class NoDataResponse(
    @field:Json(name = "err") val errorOccurred: Boolean,
    @field:Json(name = "msg") val errorMessage: String
) {
    companion object {
        const val FIELD_ERROR_OCCURRED = "err"
        const val FIELD_ERROR_MESSAGE = "msg"
    }
}

data class SignInResult(
    @field:Json(name = "isComplete") val isComplete: Int = PROFILE_INCOMPLETE
) {
    companion object {
        const val PROFILE_INCOMPLETE = 0
        const val PROFILE_COMPLETED = 1
    }
}

data class NewToken(
    @field:Json(name = "token") val token: String = ""
)

data class ImportantLink(
    val imageResource: Int,
    val text: String,
    val onClickIntent: Intent
)

data class Profile(
    @field:Json(name = "profile") val profileData: ProfileData,
    @field:Json(name = "hostels") val hostels: List<Hostel>
) {
    fun getHostelNamesList(): List<String> {
        val names = mutableListOf<String>()
        hostels.forEach {
            names.add(it.name)
        }
        return names
    }

    fun getHostelKey(hostelName: String): String? {
        hostels.forEach {
            if (hostelName == it.name)
                return it.key
        }
        return null
    }

    fun getHostelName(key: String): String {
        hostels.forEach {
            if (key == it.key)
                return it.name
        }
        return "NA"
    }
}

data class Hostel(
    @field:Json(name = "key") val key: String,
    @field:Json(name = "name") val name: String
)

data class ProfileData(
    @field:Json(name = "uid") val uid: String = "",
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "id") val id: String = "",
    @field:Json(name = "hostel") val hostel: String = "",
    @field:Json(name = "room") val room: String = "",
    @field:Json(name = "gender") val gender: String = "",
    @field:Json(name = "phone") val phoneNumber: String = "",
    @field:Json(name = "alt_phone") val alternatePhoneNumber: String = "",
    @field:Json(name = "email") val email: String = "",
    @field:Json(name = "dob") val dateOfBirth: String = "",
    @field:Json(name = "aadhaar") val aadhaarCardNumber: String = "",
    @field:Json(name = "pan_card") val panCardNumber: String = "",
    @field:Json(name = "category") val category: String = "",
    @field:Json(name = "father") val fatherName: String = "",
    @field:Json(name = "mother") val motherName: String = "",
    @field:Json(name = "fmail") val fatherEmailAddress: String = "",
    @field:Json(name = "fphone") val fatherPhoneNumber: String = "",
    @field:Json(name = "foccup") val fatherOccupation: String = "",
    @field:Json(name = "fcomp") val fatherCompanyName: String = "",
    @field:Json(name = "fdesg") val fatherDesignation: String = "",
    @field:Json(name = "mmail") val motherEmailAddress: String = "",
    @field:Json(name = "moccup") val motherOccupation: String = "",
    @field:Json(name = "mcomp") val motherCompanyName: String = "",
    @field:Json(name = "mdesg") val motherDesignation: String = "",
    @field:Json(name = "hphone") val housePhoneNumber: String = "",
    @field:Json(name = "homeadd") val homeAddress: String = "",
    @field:Json(name = "dist") val district: String = "",
    @field:Json(name = "city") val city: String = "",
    @field:Json(name = "state") val state: String = "",
    @field:Json(name = "pin_code") val pinCode: String = "",
    @field:Json(name = "guardian") val guardianName: String = "",
    @field:Json(name = "gphone") val guardianPhoneNumber: String = "",
    @field:Json(name = "localadd") val guardianAddress: String = "",
    @field:Json(name = "nation") val nationality: String = "",
    @field:Json(name = "blood") val bloodGroup: String = "",
    @field:Json(name = "med_history") val medicalHistory: String = "",
    @field:Json(name = "current_med") val currentMedications: String = "",
    @field:Json(name = "bank") val bankName: String = "",
    @field:Json(name = "acno") val bankAccountNumber: String = "",
    @field:Json(name = "ifsc") val bankIfscCode: String = "",
    @field:Json(name = "income") val householdIncome: String = "",
    @field:Json(name = "uploadImage") val needsToUploadImage: Int = 0,
)

@Parcelize
data class GoodiesSizes(
    @field:Json(name = "xs") var xs: Int = 0,
    @field:Json(name = "s") var s: Int = 0,
    @field:Json(name = "m") var m: Int = 0,
    @field:Json(name = "l") var l: Int = 0,
    @field:Json(name = "xl") var xl: Int = 0,
    @field:Json(name = "xxl") var xxl: Int = 0,
    @field:Json(name = "xxxl") var xxxl: Int = 0,
) : Parcelable {

    fun getSizeMap() = mutableMapOf(
        SIZE_XS to xs,
        SIZE_S to s,
        SIZE_M to m,
        SIZE_L to l,
        SIZE_XL to xl,
        SIZE_XXL to xxl,
        SIZE_XXXL to xxxl,
    )

    fun getQuantity() = xs + s + m + l + xl + xxl + xxxl

    fun getSizesString(): String {
        val sizesSelected = getSizeMap()
        var numberOfUniqueSizes = 0
        var sizeString = ""
        sizesSelected.forEach {
            if (it.value > 0)
                numberOfUniqueSizes += 1
        }
        if (numberOfUniqueSizes == 0)
            sizeString = "Select sizes"
        else {
            sizesSelected.forEach {
                if (it.value > 0) {
                    sizeString += "${it.value} ${it.key.toUpperCase(Locale.ROOT)}  "
                }
            }
        }
        return sizeString.trim()
    }

    companion object {
        const val SIZE_XS = "xs"
        const val SIZE_S = "s"
        const val SIZE_M = "m"
        const val SIZE_L = "l"
        const val SIZE_XL = "xl"
        const val SIZE_XXL = "xxl"
        const val SIZE_XXXL = "xxxl"
    }
}

@Parcelize
data class Goodie(
    @field:Json(name = "description") val description: String = "",
    @field:Json(name = "img") val images: Array<String> = arrayOf(),
    @field:Json(name = "size_chart") val sizeChart: String = "",
    @field:Json(name = "sizes") val sizesAvailable: List<String> = listOf(),
    @field:Json(name = "cancellable") val cancellable: Int = 0,
    @field:Json(name = "min_amount") val minAmount: Int = 0,
    @field:Json(name = "max_amount") val maxAmount: Int = 0,
    @field:Json(name = "limit") val limit: Int = 0,
    @field:Json(name = "closing_time") val closingTime: Long = 0L,
    @field:Json(name = "_id") val id: String = "",
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "type") val type: Int = GOODIE_TYPE_TSHIRT,
    @field:Json(name = "price") val price: Int = 0,
    @field:Json(name = "host_org") val hostOrganization: String = "",
    @field:Json(name = "host_name") val hostName: String = "",
    @field:Json(name = "host_uid") val hostUid: String = "",
    @field:Json(name = "host_mobile") val hostMobile: String = "",
) : Parcelable {
    fun getDisplayPrice() = when (type) {
        GOODIE_TYPE_FUNDRAISER -> "${formatAmount(minAmount)} to ${formatAmount(maxAmount)}"
        else -> formatAmount(price)
    }

    private fun formatAmount(amount: Int): String {
        return "₹${NumberFormat.getNumberInstance(Locale.getDefault()).format(amount)}"
    }

    companion object {
        const val GOODIE_TYPE_TSHIRT = 0
        const val GOODIE_TYPE_TICKET = 1
        const val GOODIE_TYPE_FUNDRAISER = 2
    }
}

data class GoodieOrder(
    @field:Json(name = "total_amount") val totalAmount: Int = 0,
    @field:Json(name = "order_id") val orderId: String = "",
)

data class GoodieSales(
    @field:Json(name = "xs") val xs: Int = 0,
    @field:Json(name = "s") val s: Int = 0,
    @field:Json(name = "m") val m: Int = 0,
    @field:Json(name = "l") val l: Int = 0,
    @field:Json(name = "xl") val xl: Int = 0,
    @field:Json(name = "xxl") val xxl: Int = 0,
    @field:Json(name = "xxxl") val xxxl: Int = 0,
    @field:Json(name = "net_quantity") val netQuantity: Int = 0,
    @field:Json(name = "total_amount") val totalAmount: Int = 0
)

data class Grace(
    @field:Json(name = "gr_id") val graceId: Int = 0,
    @field:Json(name = "uid") val uid: String = "",
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "id") val id: String = "",
    @field:Json(name = "date") val date: String = "",
    @field:Json(name = "requested_on") val requestedOn: Long = 0L,
    @field:Json(name = "outstation") val isOutstation: Int = 0
)

@Parcelize
data class MessMenu(
    @field:Json(name = "day") val day: String,
    @field:Json(name = "breakfast") val breakfast: String,
    @field:Json(name = "lunch") val lunch: String,
    @field:Json(name = "snacks") val snacks: String,
    @field:Json(name = "dinner") val dinner: String
) : Parcelable

data class Mess(
    @field:Json(name = "mess") val registeredMess: Int = 0,
    @field:Json(name = "menu") val messMenu: List<MessMenu>
)

@Parcelize
data class Deduction(
    @field:Json(name = "_id") val transactionId: String = "",
    @field:Json(name = "g_id") val goodieId: String = "",
    @field:Json(name = "g_type") val type: Int = 1,
    @field:Json(name = "g_name") val goodieName: String = "",
    @field:Json(name = "g_price") val goodiePrice: Int = 0,
    @field:Json(name = "host_org") val hostOrganization: String = "TEDx",
    @field:Json(name = "sizes") val sizes: GoodiesSizes,
    @field:Json(name = "total_amount") val totalAmount: Int = 0,
    @field:Json(name = "quantity") val quantity: Int = 0,
    @field:Json(name = "pending") val paymentPending: Int,
    @field:Json(name = "time") val purchaseDate: Long = 0L,
    @field:Json(name = "cancellable") val isCancellable: Int,
) : Parcelable {

    fun getDisplayPrice() = formatAmount(totalAmount)

    private fun formatAmount(amount: Int): String {
        return "₹${NumberFormat.getNumberInstance(Locale.getDefault()).format(amount)}"
    }

    companion object {
        const val DEDUCTION_NOT_CANCELLABLE = 0
        const val DEDUCTION_CANCELLABLE = 1
    }
}

data class StudentConnectPerson(
    @field:Json(name = "uid") val uid: String = "",
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "phone") val number: Long = 0L,
    @field:Json(name = "designation") val position: String = ""
)

data class StudentConnect(
    @field:Json(name = "swd") val swd: List<StudentConnectPerson> = listOf(),
    @field:Json(name = "suc") val suc: List<StudentConnectPerson> = listOf(),
    @field:Json(name = "crc") val crc: List<StudentConnectPerson> = listOf(),
    @field:Json(name = "smc") val smc: List<StudentConnectPerson> = listOf(),
    @field:Json(name = "ec") val ec: List<StudentConnectPerson> = listOf(),
    @field:Json(name = "pu") val pu: List<StudentConnectPerson> = listOf(),
)

data class OfficialConnectPerson(
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "email") val email: String = "",
    @field:Json(name = "designation") val position: String = "",
)

data class OfficialConnectGroup(
    @field:Json(name = "group") val group: String = "",
    @field:Json(name = "off") val contacts: List<OfficialConnectPerson> = emptyList(),
)

data class Document(
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "key") val key: String = ""
)

data class Outstation(
    @field:Json(name = "outstation_id") val outstationId: Int = 0,
    @field:Json(name = "uid") val uid: String = "",
    @field:Json(name = "from") val fromDate: String = "",
    @field:Json(name = "to") val toDate: String = "",
    @field:Json(name = "location") val location: String = "",
    @field:Json(name = "reason") val reason: String = "",
    @field:Json(name = "duration") val duration: Int = 0,
    @field:Json(name = "approved") val approved: Int = 0
)

data class CounsellorSlot(
    @field:Json(name = "date") val date: String = "",
    @field:Json(name = "slot") val slot: Int = 0
)

data class CounsellorBooking(
    @field:Json(name = "booking_id") val bookingId: Int = 0,
    @field:Json(name = "date") val date: String = "",
    @field:Json(name = "slot") val slot: Int = 0,
    @field:Json(name = "booking_time") val bookingTime: Long = 0
)

data class McnApplication(
    @field:Json(name = "name") val name: String = "",
    @field:Json(name = "fsalary") val fatherSalary: Int = 0,
    @field:Json(name = "msalary") val motherSalary: Int = 0,
    @field:Json(name = "categ") val category: String = "",
    @field:Json(name = "remark") val remark: String = "NA",
    @field:Json(name = "upload") val downloadLink: String = "",
    @field:Json(name = "attached") val documentsSubmitted: String = "",
    @field:Json(name = "loan") val isLoan: Int = 0,
    @field:Json(name = "cgpa") val cgpa: String = "",
    @field:Json(name = "status") val status: Int = 0
) {
    companion object {
        const val MCN_STATUS_APPLICATION_DENIED = -1
        const val MCN_STATUS_APPLICATION_PENDING = 0
        const val MCN_STATUS_APPLICATION_APPROVED = 1
    }
}

data class Faqs(
    @field:Json(name = "faqs") val faqs: List<FaqGroup>,
    @field:Json(name = "last_updated") val lastUpdated: Long
)

data class FaqGroup(
    @field:Json(name = "group_name") val groupName: String = "",
    @field:Json(name = "data") val data: List<MainTopic> = listOf()
)

data class MainTopic(
    @field:Json(name = "main_topic") val mainTopic: String = "",
    @field:Json(name = "qa") val questions: List<QuestionAnswer> = listOf()
)

data class QuestionAnswer(
    @field:Json(name = "q") val question: String = "",
    @field:Json(name = "a") val answer: String = ""
)

data class Kya(
    @field:Json(name = "kya") val kya: KyaData,
    @field:Json(name = "last_updated") val lastUpdated: Long,
) {
    fun hasNoData() =
        kya.generalElectives.isEmpty() && kya.minors.isEmpty() && kya.departments.isEmpty() && kya.courseGuides.isEmpty()
}

data class KyaData(
    @field:Json(name = KYA_GEN_ELECTIVES) val generalElectives: List<GeneralElectives>,
    @field:Json(name = KYA_MINORS) val minors: List<Minor>,
    @field:Json(name = KYA_DEPTS) val departments: List<Department>,
    @field:Json(name = KYA_COURSE_GUIDES) val courseGuides: List<CourseGuide>,
) {
    fun getRecyclerFormattedMinors(): List<Pair<String, MinorData>> {
        val formattedFaqs = mutableListOf<Pair<String, MinorData>>()
        minors.forEach { mainTopic ->
            mainTopic.data.forEach { minorData ->
                formattedFaqs.add(mainTopic.mainTopic to minorData)
            }
        }
        return formattedFaqs
    }

    fun getDepartmentNames(): List<String> {
        val data = mutableListOf<String>()
        departments.forEach { dept ->
            data.add(dept.department)
        }
        return data
    }

    fun getDepartmentData(department: String): List<DepartmentData> {
        departments.forEach { guide ->
            if (guide.department == department)
                return guide.data
        }
        return emptyList()
    }

    fun getYears(): List<String> {
        val data = mutableListOf<String>()
        courseGuides.forEach { guide ->
            data.add(guide.year)
        }
        return data
    }

    fun getCourseGuideForYear(year: String): List<CourseGuideData> {
        courseGuides.forEach { guide ->
            if (guide.year == year)
                return guide.data
        }
        return emptyList()
    }

    companion object {
        const val KYA_GEN_ELECTIVES = "gen_elec"
        const val KYA_MINORS = "minors"
        const val KYA_DEPTS = "depts"
        const val KYA_COURSE_GUIDES = "course_guides"

        val kyaGroups = listOf(
            Pair(KYA_GEN_ELECTIVES, "General"),
            Pair(KYA_MINORS, "Minors and electives"),
            Pair(KYA_DEPTS, "Departments"),
            Pair(KYA_COURSE_GUIDES, "Course guides"),
        )
    }
}

data class GeneralElectives(
    @field:Json(name = "topic") val topic: String,
    @field:Json(name = "text") val text: String,
)

data class Minor(
    @field:Json(name = "main_topic") val mainTopic: String,
    @field:Json(name = "data") val data: List<MinorData>,
)

data class MinorData(
    @field:Json(name = "topic") val topic: String,
    @field:Json(name = "text") val text: String,
)

data class Department(
    @field:Json(name = "dept") val department: String,
    @field:Json(name = "data") val data: List<DepartmentData>,
)

data class DepartmentData(
    @field:Json(name = "question") val question: String,
    @field:Json(name = "ans") val answer: String,
)

data class CourseGuide(
    @field:Json(name = "year") val year: String,
    @field:Json(name = "data") val data: List<CourseGuideData>,
)

data class CourseGuideData(
    @field:Json(name = "course_no") val courseNumber: String,
    @field:Json(name = "advice") val advice: String,
)

data class Notice(
    @field:Json(name = "notice_id") val noticeId: Int = 0,
    @field:Json(name = "title") val title: String = "",
    @field:Json(name = "body") val body: String = "",
    @field:Json(name = "event") val isEvent: Int = 0,
    @field:Json(name = "image") val image: String = "",
    @field:Json(name = "attachment") val attachment: String = "",
    @field:Json(name = "time") val timePosted: Long = 0,
    @field:Json(name = "meet_link") val meetLink: String = "",
    @field:Json(name = "event_time") val eventTime: Long = 0,
    @field:Json(name = "name") val postedBy: String = "",
    @field:Json(name = "pimage") val postedByIcon: String = "",
)

data class Route(
    val routeId: Int,
    val from: String,
    val to: String
) {
    fun getDisplayRoute() = "$from to $to"
}

data class CabSharingGroup(
    val groupId: Int,
    val hostUid: String,
    val isHost: Boolean,
    val date: Date,
    val spaceFor: Int,
    val spaceTaken: Int,
    val route: Route,
    val fare: Int? = null,
    val groupLink: String? = null
)

data class CabSharingSearchResult(
    @field:Json(name = "date") val date: Long = 0L,
    @field:Json(name = "capacity") val capacity: Int = 0,
    @field:Json(name = "space") val space: Int = 0,
    @field:Json(name = "route_id") val routeId: Int = 0,
    @field:Json(name = "cab_booked") val cabBooked: Int = 0,
    @field:Json(name = "fare") val fare: Int? = null,
    @field:Json(name = "group_link") val groupLink: String? = null,
)