package com.android.safepay.network

import com.android.safepay.network.model.address.FetchAddressResponse
import com.android.safepay.network.model.address.GetAddressResponse
import com.android.safepay.network.model.payment.AuthorizationRequest
import com.android.safepay.network.model.payment.AuthorizationResponse
import com.android.safepay.network.model.payment.EnrollmentRequest
import com.android.safepay.network.model.payment.EnrollmentResponse
import com.android.safepay.network.model.payment.PayerAuthenticationRequest
import com.android.safepay.network.model.payment.PayerAuthenticationResponse
import com.android.safepay.network.model.payment.UnEnrollmentCardAuthorizationRequest
import com.android.safepay.network.model.paymentmethod.PaymentMethodListResponse
import com.android.safepay.network.model.token.ShopperTokenGenerateRequest
import com.android.safepay.network.model.token.TokenGenerateRequest
import com.android.safepay.network.model.token.TokenResponse
import com.android.safepay.network.model.tracker.TrackerFetchResponse
import com.android.safepay.network.model.user.UserExistsResponse
import com.android.safepay.network.model.usershopper.ShopperUserRequest
import com.android.safepay.network.model.usershopper.ShopperUserResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface APIService
{
    //https://sandbox.api.getsafepay.com/user/v1/guest/ TOEKN API JWT

    //https://sandbox.api.getsafepay.com/order/payments/v3/track_fabda958-1f90-4ba5-8963-295708a2c7b0 PAYER AUTHENTICATION API

    //https://sandbox.api.getsafepay.com/order/payments/v3/track_1a46dc70-91f9-4892-8e25-0bb5205b69c7 ENROLLNMENT API

    //https://sandbox.api.getsafepay.com/order/payments/v3/ PAYMENT API


    @GET("user/v2/exists")
    suspend fun checkUserExists(@Query("email") email: String): Response<UserExistsResponse>

    @GET("user/meta/v2/country")
    suspend fun getAddress(@Query("cc") country: String): Response<GetAddressResponse>

    @GET
    suspend fun fetchAddress(@Url url: String,@Header("Authorization") timeBaseToken: String): Response<FetchAddressResponse>

    @GET("user/wallets/v1/")
    suspend fun getListPaymentMethod(@Header("Authorization") token: String): Response<PaymentMethodListResponse>

    @GET
    suspend fun fetchTracker(@Url url: String,@Header("Authorization") timeBaseToken: String): Response<TrackerFetchResponse>

    @Headers("Content-Type: application/json")
    @POST("user/v1/guest/")
    suspend fun generateGuestToken(@Body tokenGenerateRequest: TokenGenerateRequest): Response<TokenResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/v2/user/login")
    suspend fun generateShopperToken(@Body tokenGenerateRequest: ShopperTokenGenerateRequest): Response<TokenResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun payerAuthenticationSetup(@Url url: String, @Header("Authorization") token: String,@Body payerAuthenticationRequest: PayerAuthenticationRequest): Response<PayerAuthenticationResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun enrollment(@Url url: String,@Header("Authorization") token: String,@Body enrollmentRequest: EnrollmentRequest): Response<EnrollmentResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun authorization(@Url url: String,@Header("Authorization") token: String,@Body authorization: AuthorizationRequest): Response<AuthorizationResponse>


    @Headers("Content-Type: application/json")
    @POST
    suspend fun unEnrollCardAuthorization(@Url url: String, @Header("Authorization") token: String, @Body unEnrollmentCardAuthorizationRequest: UnEnrollmentCardAuthorizationRequest): Response<AuthorizationResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun capture(@Url url: String, @Header("Authorization") token: String, @Body requestBody: RequestBody): Response<AuthorizationResponse>

    @Headers("Content-Type: application/json")
    @POST("user/v2/")
    suspend fun createShopper(@Body shopperUserRequest: ShopperUserRequest): Response<ShopperUserResponse>
}