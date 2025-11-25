package com.android.safepay.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.safepay.network.interfaces.ApiResultCallback
import com.android.safepay.network.model.address.FetchAddressResponse
import com.android.safepay.network.model.payment.AuthorizationRequest
import com.android.safepay.network.model.payment.EnrollmentRequest
import com.android.safepay.network.model.payment.PayerAuthenticationRequest
import com.android.safepay.network.model.payment.UnEnrollmentCardAuthorizationRequest
import com.android.safepay.network.model.token.ShopperTokenGenerateRequest
import com.android.safepay.network.model.token.TokenGenerateRequest
import com.android.safepay.network.model.usershopper.ShopperUserRequest
import com.android.safepay.network.repository.SafePayRepository
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Call
import java.util.Locale

class SafePayViewModel(private val repository: SafePayRepository) : ViewModel() {

    var consumerSessionId:String?=""
    var sdkOnValidateJWT:String?=""

    var isCheckedSavedCard:Boolean=false

    var savedCardToken:String=""
    var isPaymentMethodClicked=false

    var isPaymentSuccess=false

    var payment:String="cancel"

    private val apiCalls = mutableListOf<Call<*>>() // Store ongoing calls

    private var response: FetchAddressResponse? = null
    var defaultCountry:String=""

    fun setResponse(response: FetchAddressResponse?) {
        this.response = response
    }

    // Method to get the response
    fun getResponse(): FetchAddressResponse? {
        return response
    }

    fun checkUser(email: String,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.checkUserExists(email)

                result.onSuccess { userExistsResponse ->
                    callback.onSuccess(userExistsResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun getAddress(countryCode: String,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.getAddress(countryCode)

                result.onSuccess { getAddressResponse ->
                    callback.onSuccess(getAddressResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        //callback.onFailure(exception.message)
                    } else {
                        //callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun fetchAddress(addressToken: String,timeBaseToken: String,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.fetchAddress(addressToken,timeBaseToken)

                result.onSuccess { getAddressResponse ->
                    callback.onSuccess(getAddressResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        //callback.onFailure(exception.message)
                    } else {
                        //callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun generateToken(tokenGenerateRequest: TokenGenerateRequest,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.generateToken(tokenGenerateRequest)

                result.onSuccess { tokenGenerateResponse ->
                    callback.onSuccess(tokenGenerateResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun generateShopperToken(shopperTokenGenerateRequest: ShopperTokenGenerateRequest,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.generateShopperToken(shopperTokenGenerateRequest)

                result.onSuccess { tokenGenerateResponse ->
                    callback.onSuccess(tokenGenerateResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun payerAuthenticationSetup(tracker:String,payerAuthenticationRequest: PayerAuthenticationRequest,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.payerAuthenticationSetup(tracker,payerAuthenticationRequest)

                result.onSuccess { payerAuthenticationRequest ->
                    callback.onSuccess(payerAuthenticationRequest)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun enrollment(tracker:String,enrollmentRequest: EnrollmentRequest,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.enrollment(tracker,enrollmentRequest)

                result.onSuccess { enrollmentResponse ->
                    callback.onSuccess(enrollmentResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun authorization(tracker:String,authorizationRequest: AuthorizationRequest,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.authorization(tracker,authorizationRequest)

                result.onSuccess { authorizationResponse ->
                    callback.onSuccess(authorizationResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun unEnrollAuthorization(tracker:String,authorizationRequest: UnEnrollmentCardAuthorizationRequest,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.unEnrollAuthorization(tracker,authorizationRequest)

                result.onSuccess { authorizationResponse ->
                    callback.onSuccess(authorizationResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun capture(tracker:String,requestBody: RequestBody,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.capture(tracker,requestBody)

                result.onSuccess { authorizationResponse ->
                    callback.onSuccess(authorizationResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun createShopperUser(shopperUserRequest: ShopperUserRequest,callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.createShopperUser(shopperUserRequest)

                result.onSuccess { shopperUserResponse ->
                    callback.onSuccess(shopperUserResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun getPaymentMethodList(callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.paymentMethodList()

                result.onSuccess { shopperUserResponse ->
                    callback.onSuccess(shopperUserResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun fetchTracker(tracker:String, timeBaseToken: String, callback: ApiResultCallback) {
        viewModelScope.launch {
            try {
                val result = repository.fetchTracker(tracker,timeBaseToken)

                result.onSuccess { fetchTrackerResponse ->
                    callback.onSuccess(fetchTrackerResponse)
                }.onFailure { exception ->
                    // Handle the exception
                    if (exception is SafePayRepository.ApiException) {
                        callback.onFailure(exception.message)
                    } else {
                        callback.onFailure(exception.message)
                    }
                }

            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    fun getCountryByLocale():String?
    {
        return Locale.getDefault().country
    }

    // Add a call to the list
    fun addApiCall(call: Call<*>) {
        synchronized(apiCalls) {
            apiCalls.add(call)
        }
    }

    // Cancel all ongoing API calls
    fun cancelApiCalls() {
        synchronized(apiCalls) {
            apiCalls.forEach { call ->
                if (!call.isCanceled) {
                    call.cancel()
                }
            }
            apiCalls.clear() // Clear the list
        }
    }
}