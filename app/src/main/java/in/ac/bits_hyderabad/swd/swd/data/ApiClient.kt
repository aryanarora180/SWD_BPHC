package `in`.ac.bits_hyderabad.swd.swd.data

import android.content.Context
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

object ApiClient {

    private const val BASE_URL = "https://swd.bits-hyderabad.ac.in/api/"

    private fun okHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    private lateinit var apiService: ApiService
    fun build(context: Context): ApiService {
        if (!(ApiClient::apiService.isInitialized)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient(context))
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }

    interface ApiService {

        @FormUrlEncoded
        @POST("auth/google_oauth")
        suspend fun getLoginTokenGoogle(
            @Field("id_token") idToken: String,
        ): DataResponse<SignInResult>

        @FormUrlEncoded
        @POST("auth")
        suspend fun getLoginTokenCredentials(
            @Field("uid") uid: String,
            @Field("password") password: String,
            @Field("type") type: Int = 1,
        ): DataResponse<SignInResult>

        @FormUrlEncoded
        @POST("notifications/register")
        suspend fun registerForNotifications(
            @Field("device_token") notificationToken: String,
            @Field("platform") platform: String = "android",
        ): NoDataResponse

        @GET("auth/reset")
        suspend fun sendPasswordResetRequest(@Query("uid") uid: String): NoDataResponse

        @FormUrlEncoded
        @POST("auth/change")
        suspend fun changePassword(
            @Field("uid") uid: String,
            @Field("cpassword") currentPassword: String,
            @Field("npassword") newPassword: String
        ): DataResponse<NewToken>

        @GET("usr/profile")
        suspend fun getProfile(): DataResponse<Profile>

        @GET("goodies")
        suspend fun getGoodies(): DataResponse<List<Goodie>>

        @FormUrlEncoded
        @POST("goodies")
        suspend fun buyGoodieWithOtherAdvances(
            @Field("g_id") goodieId: String,
            @FieldMap sizeDetails: Map<String, Int>,
            @Field("net_quantity") netQuantity: Int,
            @Field("total_amount") totalAmount: Int,
        ): NoDataResponse

        @GET("goodies/info")
        suspend fun getGoodieSales(@Query("g_id") g_id: Int): DataResponse<GoodieSales>

        @GET("mess/grace")
        suspend fun getMessGraces(): DataResponse<List<Grace>>

        @GET("mess/menu")
        suspend fun getMessMenu(): DataResponse<Mess>

        @FormUrlEncoded
        @POST("mess/grace")
        suspend fun sendMessGraceRequest(@Field("date") date: String): NoDataResponse

        @FormUrlEncoded
        @POST("usr/profile")
        suspend fun sendProfileDetails(@FieldMap details: Map<String, String>): NoDataResponse

        @GET("deductions")
        suspend fun getDeductions(): DataResponse<List<Deduction>>

        @FormUrlEncoded
        @POST("deductions/cancel")
        suspend fun cancelOrder(@Field("transaction_id") transactionId: Int): NoDataResponse

        @GET("con/resb")
        suspend fun getStudentConnect(): DataResponse<StudentConnect>

        @GET("con/office")
        suspend fun getOfficialConnect(): DataResponse<List<OfficialConnectGroup>>

        @GET("doc/list")
        suspend fun getDocumentsAvailable(): DataResponse<List<Document>>

        @GET("doc")
        suspend fun downloadDocument(@Query("key") key: String): ResponseBody

        @GET
        suspend fun downloadMcnFiles(@Url url: String): ResponseBody

        @GET("outstation")
        suspend fun getOutstations(): DataResponse<List<Outstation>>

        @FormUrlEncoded
        @POST("outstation")
        suspend fun requestOutstation(
            @Field("from") fromDate: String,
            @Field("to") toDate: String,
            @Field("reason") reason: String,
            @Field("location") location: String
        ): NoDataResponse

        @FormUrlEncoded
        @POST("outstation/cancel")
        suspend fun cancelOutstation(@Field("outstation_id") outstationId: Int): NoDataResponse

        @Multipart
        @POST("mcn")
        suspend fun applyForMcn(
            @Part("fsalary") fatherSalary: RequestBody,
            @Part("msalary") motherSalary: RequestBody,
            @Part("categ") category: RequestBody,
            @Part("attached") documentsSubmitted: RequestBody,
            @Part("loan") isLoanTaken: RequestBody,
            @Part("cgpa") cgpa: RequestBody,
            @Part file: MultipartBody.Part,
        ): NoDataResponse

        @GET("counsellor")
        suspend fun getCounsellorSlots(): DataResponse<List<CounsellorSlot>>

        @GET("mcn/portal")
        suspend fun getMcnPortalStatus(): NoDataResponse

        @GET("mcn/get")
        suspend fun getMcnApplication(): DataResponse<McnApplication>

        @POST("mcn/delete")
        suspend fun deleteCurrentMcnApplication(): NoDataResponse

        @FormUrlEncoded
        @POST("counsellor")
        suspend fun bookCounsellor(
            @Field("date") date: String,
            @Field("slot") slot: Int
        ): NoDataResponse

        @GET("counsellor/bookings")
        suspend fun getUserBookedCounsellorSlots(): DataResponse<List<CounsellorBooking>>

        @FormUrlEncoded
        @POST("counsellor/delete")
        suspend fun deleteCounsellorSlot(
            @Field("booking_id") bookingId: Int
        ): NoDataResponse

        @GET("faq")
        suspend fun getFaqs(): DataResponse<Faqs>

        @GET("kya")
        suspend fun getKya(): DataResponse<Kya>

        @GET("notices/feed")
        suspend fun getNotices(
            @Query("start") start: Int,
            @Query("limit") limit: Int
        ): DataResponse<List<Notice>>
    }
}