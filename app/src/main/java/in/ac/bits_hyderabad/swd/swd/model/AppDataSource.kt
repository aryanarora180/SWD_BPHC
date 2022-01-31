package `in`.ac.bits_hyderabad.swd.swd.model

import `in`.ac.bits_hyderabad.swd.swd.data.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface AppDataSource {

    suspend fun signInWithGoogle(
        idToken: String,
        notificationToken: String
    ): OperationResult<SignInResult>

    suspend fun signInWithCredentials(
        uid: String,
        password: String,
        notificationToken: String
    ): OperationResult<SignInResult>

    suspend fun registerForNotifications(
        notificationToken: String
    ): OperationResult<Unit>

    suspend fun sendPasswordResetRequest(uid: String): OperationResult<Unit>

    suspend fun changePassword(
        uid: String,
        currentPassword: String,
        newPassword: String
    ): OperationResult<NewToken>

    suspend fun getProfile(): OperationResult<Profile>

    suspend fun getGoodies(): OperationResult<List<Goodie>>

    suspend fun placeGoodieOrder(
        goodieId: String,
        sizeDetails: Map<String, Int> = GoodiesSizes().getSizeMap(),
        netQuantity: Int = 0,
        totalAmount: Int = 0,
    ): OperationResult<Unit>

    suspend fun getGoodieSales(g_id: Int): OperationResult<GoodieSales>

    suspend fun getMessGraces(): OperationResult<List<Grace>>

    suspend fun getMessMenu(): OperationResult<Mess>

    suspend fun sendMessGraceRequest(date: String): OperationResult<Unit>

    suspend fun sendProfileDetails(details: Map<String, String>): OperationResult<Unit>

    suspend fun getDeductions(): OperationResult<List<Deduction>>

    suspend fun downloadMcnFiles(url: String): OperationResult<ResponseBody>

    suspend fun cancelOrder(transactionId: Int): OperationResult<Unit>

    suspend fun getStudentConnect(): OperationResult<StudentConnect>

    suspend fun getOfficialsConnect(): OperationResult<List<OfficialConnectGroup>>

    suspend fun getDocumentsAvailable(): OperationResult<List<Document>>

    suspend fun downloadDocument(key: String): OperationResult<ResponseBody>

    suspend fun getOutstations(): OperationResult<List<Outstation>>

    suspend fun requestOutstation(
        fromDate: String,
        toDate: String,
        reason: String,
        location: String
    ): OperationResult<Unit>

    suspend fun cancelOutstation(outstationId: Int): OperationResult<Unit>

    suspend fun applyForMcn(
        fatherSalary: Int,
        motherSalary: Int,
        category: String,
        file: MultipartBody.Part,
        documentsSubmitted: String,
        isLoan: Int,
        cgpa: String,
    ): OperationResult<Unit>

    suspend fun getMcnPortalStatus(): OperationResult<Unit>

    suspend fun getMcnApplication(): OperationResult<McnApplication>

    suspend fun deleteCurrentMcnApplication(): OperationResult<Unit>

    suspend fun getCounsellorSlots(): OperationResult<List<CounsellorSlot>>

    suspend fun bookCounsellor(date: String, slot: Int): OperationResult<Unit>

    suspend fun getUserBookedCounsellorSlots(): OperationResult<List<CounsellorBooking>>

    suspend fun deleteCounsellorSlot(bookingId: Int): OperationResult<Unit>

    suspend fun getFaqs(): OperationResult<FaqsWrapper>

    suspend fun getKya(): OperationResult<Kya>

    suspend fun getNotices(
        start: Int,
        limit: Int
    ): OperationResult<List<Notice>>
}