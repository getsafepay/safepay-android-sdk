package com.android.safepay.network.repository

import android.content.Context
import android.util.Log
import com.android.safepay.R
import com.android.safepay.network.error.ErrorResponse
import com.android.safepay.network.error.LoginErrorResponse
import com.android.safepay.network.error.PaymentError
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
import com.android.safepay.network.network.RetrofitInstance
import com.android.safepay.network.utils.Constants
import com.android.safepay.network.utils.StatusCode
import com.android.safepay.network.utils.StatusCode.CODE_200
import com.android.safepay.network.utils.StatusCode.CODE_401
import com.google.gson.Gson
import okhttp3.RequestBody

class SafePayRepository(private val mContext: Context)
{
    private var accessToken:String?=null

    class ApiException(val code: Int, message: String?) : Exception(message)

    suspend fun checkUserExists(email: String): Result<UserExistsResponse?> {
        val response = RetrofitInstance.api.checkUserExists(email)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorResponse = try {
                response.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, LoginErrorResponse::class.java)
                }
            } catch (e: Exception) {
                null // In case of parsing failure
            }
            val errorMessage = errorResponse?.message ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun getAddress(countryCode: String): Result<GetAddressResponse?> {
        val response = RetrofitInstance.api.getAddress(countryCode)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorResponse = try {
                response.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, ErrorResponse::class.java)
                }
            } catch (e: Exception) {
                null // In case of parsing failure
            }
            val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun fetchAddress(addressToken: String,timeBaseToken: String): Result<FetchAddressResponse?> {
        val token = "Bearer $timeBaseToken"
        val url = "user/address/v2/$addressToken"
        val response = RetrofitInstance.api.fetchAddress(url,token)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorResponse = try {
                response.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, ErrorResponse::class.java)
                }
            } catch (e: Exception) {
                null // In case of parsing failure
            }
            val errorMessage = if (response.code() == CODE_401) {
                PaymentError.timeBasedTokenExpired().msg }
            else
                errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg

            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun createShopperUser(shopperUserRequest: ShopperUserRequest): Result<ShopperUserResponse?> {
        val response = RetrofitInstance.api.createShopper(shopperUserRequest)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorBody = response.errorBody()?.string()
            val errorMessage = errorBody?.let { json ->
                val parsedErrorResponse = try {
                    Gson().fromJson(json, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                parsedErrorResponse?.status?.errors?.firstOrNull() ?: run {
                    try {
                        Gson().fromJson(json, LoginErrorResponse::class.java)?.message
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: PaymentError.generalPaymentError().msg
            //val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun generateToken(tokenGenerateRequest: TokenGenerateRequest):Result<TokenResponse?>
    {
        val response = RetrofitInstance.api.generateGuestToken(tokenGenerateRequest)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
        {
            accessToken=response.body()?.data?.session
            Log.d("Access Token : " , accessToken.toString())
            return Result.success(response.body())
        }

        else
        {
            // Parse the error response
            val errorResponse = try {
                response.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, ErrorResponse::class.java)
                }
            } catch (e: Exception) {
                null // In case of parsing failure
            }
            val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun generateShopperToken(shopperTokenGenerateRequest: ShopperTokenGenerateRequest):Result<TokenResponse?>
    {
        val response = RetrofitInstance.api.generateShopperToken(shopperTokenGenerateRequest)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
        {
            accessToken=response.body()?.data?.session
            Log.d("Access Token : " , accessToken.toString())
            return Result.success(response.body())
        }

        else
        {
            // Parse the error response
            val errorResponse = try {
                response.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, LoginErrorResponse::class.java)
                }
            } catch (e: Exception) {
                null // In case of parsing failure
            }
            val errorMessage = errorResponse?.message ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun payerAuthenticationSetup(trackerToken:String,payerAuthenticationRequest: PayerAuthenticationRequest):Result<PayerAuthenticationResponse?>
    {
        val token = "Bearer $accessToken"
        val url= Constants.DEV_BASE_URL + "order/payments/v3/$trackerToken"
        val response = RetrofitInstance.api.payerAuthenticationSetup(url,token,payerAuthenticationRequest)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorBody = response.errorBody()?.string()
            val errorMessage = errorBody?.let { json ->
                val parsedErrorResponse = try {
                    Gson().fromJson(json, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                parsedErrorResponse?.status?.errors?.firstOrNull() ?: run {
                    try {
                        Gson().fromJson(json, LoginErrorResponse::class.java)?.message
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: PaymentError.generalPaymentError().msg

            //val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }


    suspend fun enrollment(trackerToken:String,enrollmentRequest: EnrollmentRequest):Result<EnrollmentResponse?>
    {
        val token = "Bearer $accessToken"
        val url= Constants.DEV_BASE_URL + "order/payments/v3/$trackerToken"

        val response = RetrofitInstance.api.enrollment(url,token,enrollmentRequest)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorBody = response.errorBody()?.string()
            val errorMessage = errorBody?.let { json ->
                val parsedErrorResponse = try {
                    Gson().fromJson(json, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                parsedErrorResponse?.status?.errors?.firstOrNull() ?: run {
                    try {
                        Gson().fromJson(json, LoginErrorResponse::class.java)?.message
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: PaymentError.generalPaymentError().msg
            //val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun paymentMethodList():Result<PaymentMethodListResponse?>
    {
        val token = "Bearer $accessToken"

        val response = RetrofitInstance.api.getListPaymentMethod(token)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorBody = response.errorBody()?.string()
            val errorMessage = errorBody?.let { json ->
                val parsedErrorResponse = try {
                    Gson().fromJson(json, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                parsedErrorResponse?.status?.errors?.firstOrNull() ?: run {
                    try {
                        Gson().fromJson(json, LoginErrorResponse::class.java)?.message
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: PaymentError.generalPaymentError().msg
            //val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }
    }

    suspend fun authorization(trackerToken:String,authorizationRequest: AuthorizationRequest):Result<AuthorizationResponse?>
    {
        val token = "Bearer $accessToken"
        val url= Constants.DEV_BASE_URL + "order/payments/v3/$trackerToken"

        val response = RetrofitInstance.api.authorization(url,token,authorizationRequest)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorBody = response.errorBody()?.string()
            val errorMessage = errorBody?.let { json ->
                val parsedErrorResponse = try {
                    Gson().fromJson(json, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                parsedErrorResponse?.status?.errors?.firstOrNull() ?: run {
                    try {
                        Gson().fromJson(json, LoginErrorResponse::class.java)?.message
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: PaymentError.generalPaymentError().msg
            //val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }

    }

    suspend fun unEnrollAuthorization(trackerToken:String, unEnrollmentCardAuthorizationRequest: UnEnrollmentCardAuthorizationRequest):Result<AuthorizationResponse?>
    {
        val token = "Bearer $accessToken"
        val url= Constants.DEV_BASE_URL + "order/payments/v3/$trackerToken"

        val response = RetrofitInstance.api.unEnrollCardAuthorization(url,token,unEnrollmentCardAuthorizationRequest)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorBody = response.errorBody()?.string()
            val errorMessage = errorBody?.let { json ->
                val parsedErrorResponse = try {
                    Gson().fromJson(json, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                parsedErrorResponse?.status?.errors?.firstOrNull() ?: run {
                    try {
                        Gson().fromJson(json, LoginErrorResponse::class.java)?.message
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: PaymentError.generalPaymentError().msg
            //val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }

    }

    suspend fun capture(trackerToken:String, requestBody: RequestBody):Result<AuthorizationResponse?>
    {
        val token = "Bearer $accessToken"
        val url= Constants.DEV_BASE_URL + "order/payments/v3/$trackerToken"

        val response = RetrofitInstance.api.capture(url,token,requestBody)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorBody = response.errorBody()?.string()
            val errorMessage = errorBody?.let { json ->
                val parsedErrorResponse = try {
                    Gson().fromJson(json, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                parsedErrorResponse?.status?.errors?.firstOrNull() ?: run {
                    try {
                        Gson().fromJson(json, LoginErrorResponse::class.java)?.message
                    } catch (e: Exception) {
                        null
                    }
                }
            } ?: PaymentError.generalPaymentError().msg
            //val errorMessage = errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg
            return Result.failure(ApiException(response.code(), errorMessage))
        }

    }

    suspend fun fetchTracker(trackerToken:String, timeBaseToken: String):Result<TrackerFetchResponse?>
    {

        val token = "Bearer $timeBaseToken"
        val url= Constants.DEV_BASE_URL + "reporter/api/v1/payments/$trackerToken"

        val response = RetrofitInstance.api.fetchTracker(url,token)
        if(response.isSuccessful && response.body()!=null && response.code() == CODE_200)
            return Result.success(response.body())
        else
        {
            // Parse the error response
            val errorResponse = try {
                response.errorBody()?.string()?.let { json ->
                    Gson().fromJson(json, ErrorResponse::class.java)
                }
            } catch (e: Exception) {
                null // In case of parsing failure
            }
            val errorMessage = if (response.code() == CODE_401) {
                PaymentError.timeBasedTokenExpired().msg }
            else
                errorResponse?.status?.errors?.firstOrNull() ?: PaymentError.generalPaymentError().msg

            return Result.failure(ApiException(response.code(),errorMessage
            ))
        }
    }
}