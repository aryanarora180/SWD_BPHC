package `in`.ac.bits_hyderabad.swd.swd.model

import `in`.ac.bits_hyderabad.swd.swd.data.*
import `in`.ac.bits_hyderabad.swd.swd.helper.DataStoreUtils
import android.content.Context
import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class AppRepository(application: Context) : AppDataSource {

    @Inject
    lateinit var dataStoreUtils: DataStoreUtils

    private val apiClient = ApiClient.build(application)

    override suspend fun signInWithGoogle(
        idToken: String,
        notificationToken: String
    ): OperationResult<SignInResult> {
        return try {
            val result = apiClient.getLoginTokenGoogle(idToken)
            apiClient.registerForNotifications(notificationToken)
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun signInWithCredentials(
        uid: String,
        password: String,
        notificationToken: String
    ) = try {
        val result = apiClient.getLoginTokenCredentials(uid, password)
        apiClient.registerForNotifications(notificationToken)
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
    }

    override suspend fun registerForNotifications(
        notificationToken: String
    ): OperationResult<Unit> {
        return try {
            apiClient.registerForNotifications(notificationToken)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun sendPasswordResetRequest(uid: String): OperationResult<Unit> {
        return try {
            apiClient.sendPasswordResetRequest(uid)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun changePassword(
        uid: String,
        currentPassword: String,
        newPassword: String
    ): OperationResult<NewToken> {
        return try {
            val result = apiClient.changePassword(
                uid,
                currentPassword,
                newPassword
            )
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun downloadMcnFiles(url: String): OperationResult<ResponseBody> {
        return try {
            val result = apiClient.downloadMcnFiles(url)
            OperationResult.Success(result)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getProfile(): OperationResult<Profile> {
        return try {
            val result = apiClient.getProfile()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getGoodies(): OperationResult<List<Goodie>> {
        return try {
            val result = apiClient.getGoodies()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun placeGoodieOrder(
        goodieId: String,
        sizeDetails: Map<String, Int>,
        netQuantity: Int,
        totalAmount: Int
    ): OperationResult<Unit> {
        return try {
            apiClient.buyGoodieWithOtherAdvances(
                goodieId,
                sizeDetails,
                netQuantity,
                totalAmount
            )
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getGoodieSales(g_id: Int): OperationResult<GoodieSales> {
        return try {
            val result = apiClient.getGoodieSales(g_id)
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getMessGraces(): OperationResult<List<Grace>> {
        return try {
            val result = apiClient.getMessGraces()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getMessMenu(): OperationResult<Mess> {
        return try {
            val result = apiClient.getMessMenu()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun sendMessGraceRequest(date: String): OperationResult<Unit> {
        return try {
            apiClient.sendMessGraceRequest(date)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun sendProfileDetails(details: Map<String, String>): OperationResult<Unit> {
        return try {
            apiClient.sendProfileDetails(details)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getDeductions(): OperationResult<List<Deduction>> {
        return try {
            val result = apiClient.getDeductions()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun cancelOrder(transactionId: Int): OperationResult<Unit> {
        return try {
            apiClient.cancelOrder(transactionId)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getStudentConnect(): OperationResult<StudentConnect> {
        return try {
            val result = apiClient.getStudentConnect()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getOfficialsConnect(): OperationResult<List<OfficialConnectGroup>> {
        return try {
            val result = apiClient.getOfficialConnect()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getDocumentsAvailable(): OperationResult<List<Document>> {
        return try {
            val result = apiClient.getDocumentsAvailable()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun downloadDocument(
        key: String
    ): OperationResult<ResponseBody> {
        return try {
            val result = apiClient.downloadDocument(key)
            OperationResult.Success(result)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getOutstations(): OperationResult<List<Outstation>> {
        return try {
            val result = apiClient.getOutstations()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun requestOutstation(
        fromDate: String,
        toDate: String,
        reason: String,
        location: String
    ): OperationResult<Unit> {
        return try {
            apiClient.requestOutstation(
                fromDate,
                toDate,
                reason,
                location
            )
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun cancelOutstation(outstationId: Int): OperationResult<Unit> {
        return try {
            apiClient.cancelOutstation(outstationId)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun applyForMcn(
        fatherSalary: Int,
        motherSalary: Int,
        category: String,
        file: MultipartBody.Part,
        documentsSubmitted: String,
        isLoan: Int,
        cgpa: String,
    ): OperationResult<Unit> {

        return try {
            apiClient.applyForMcn(
                RequestBody.create(MediaType.parse("text/plain"), fatherSalary.toString()),
                RequestBody.create(MediaType.parse("text/plain"), motherSalary.toString()),
                RequestBody.create(MediaType.parse("text/plain"), category),
                RequestBody.create(MediaType.parse("text/plain"), documentsSubmitted),
                RequestBody.create(MediaType.parse("text/plain"), isLoan.toString()),
                RequestBody.create(MediaType.parse("text/plain"), cgpa),
                file,
            )
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            e.printStackTrace()
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getMcnPortalStatus(): OperationResult<Unit> {
        return try {
            apiClient.getMcnPortalStatus()
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getMcnApplication(): OperationResult<McnApplication> {
        return try {
            val result = apiClient.getMcnApplication()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun deleteCurrentMcnApplication(): OperationResult<Unit> {
        return try {
            apiClient.deleteCurrentMcnApplication()
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getCounsellorSlots(): OperationResult<List<CounsellorSlot>> {
        return try {
            val result = apiClient.getCounsellorSlots()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun bookCounsellor(date: String, slot: Int): OperationResult<Unit> {
        return try {
            apiClient.bookCounsellor(date, slot)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getUserBookedCounsellorSlots(): OperationResult<List<CounsellorBooking>> {
        return try {
            val result = apiClient.getUserBookedCounsellorSlots()
            OperationResult.Success(result.data)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun deleteCounsellorSlot(bookingId: Int): OperationResult<Unit> {
        return try {
            apiClient.deleteCounsellorSlot(bookingId)
            OperationResult.Success(Unit)
        } catch (e: HttpException) {
            getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
        } catch (e: Exception) {
            OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
        }
    }

    override suspend fun getFaqs(): OperationResult<FaqsWrapper> = try {
        val result = apiClient.getFaqs()

        val wrappedList = mutableListOf<FaqGroupWrapper>()
        result.data.faqs.forEachIndexed { index, faqGroup ->
            val groupName = faqGroup.groupName

            wrappedList.add(
                FaqGroupWrapper(
                    groupName,
                    FaqGroupWrapper.getMainTopicsList(faqGroup.data),
                    FaqGroupWrapper.getFormattedFaqsList(index, groupName, faqGroup.data)
                )
            )
        }

        val toSearchList = mutableListOf<FaqQuestionWrapper>()
        wrappedList.forEach { faqGroupWrapper ->
            faqGroupWrapper.questions.forEach {
                toSearchList.add(
                    FaqQuestionWrapper(
                        it.groupName,
                        it.groupIndex,
                        it.topicName,
                        it.question,
                        it.questionIndexInGroup
                    )
                )
            }
        }
        OperationResult.Success(FaqsWrapper(result.data.lastUpdated, wrappedList, toSearchList))
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
    }

    override suspend fun getKya() = try {
        val result = apiClient.getKya()
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
    }

    override suspend fun getNotices(start: Int, limit: Int) = try {
        val result = apiClient.getNotices(start, limit)
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.ERROR_CODE_UNDETERMINED, null)
    }

    private fun getParsedErrorBody(status: Int, errorBody: String?): OperationResult.Error {
        return if (errorBody != null) {
            try {
                val jsonObject = JSONObject(errorBody)
                if (jsonObject.getString(NoDataResponse.FIELD_ERROR_OCCURRED) == "true") {
                    OperationResult.Error(
                        status,
                        jsonObject.getString(NoDataResponse.FIELD_ERROR_MESSAGE)
                    )
                } else {
                    OperationResult.Error(status, null)
                }
            } catch (e: Exception) {
                OperationResult.Error(status, null)
            }
        } else {
            OperationResult.Error(status, null)
        }
    }
}