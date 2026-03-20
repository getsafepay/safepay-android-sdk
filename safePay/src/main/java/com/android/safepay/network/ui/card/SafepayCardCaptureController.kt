package com.android.safepay.network.ui.card

import android.content.Context
import com.android.safepay.network.APIService
import com.android.safepay.network.error.ErrorResponse
import com.android.safepay.network.error.LoginErrorResponse
import com.android.safepay.network.error.PaymentError
import com.android.safepay.network.model.payment.PayerAuthenticationRequest
import com.android.safepay.network.model.payment.PayerAuthenticationResponse
import com.android.safepay.network.network.RetrofitInstance
import com.android.safepay.network.utils.Constants
import com.google.gson.Gson
import retrofit2.Response

enum class SafepayCardCaptureEnvironment(val baseUrl: String) {
    DEVELOPMENT(Constants.DEV_BASE_URL),
    SANDBOX(Constants.SANDBOX_BASE_URL),
    PRODUCTION(Constants.PRODUCTION_BASE_URL),
}

data class SafepayCardCaptureConfiguration(
    val environment: SafepayCardCaptureEnvironment,
    val authToken: String,
    val tracker: String,
)

data class SafepayCardCapturePaymentMethod(
    val token: String,
    val expirationMonth: String,
    val expirationYear: String,
    val cardTypeCode: String,
    val cardType: String,
    val binNumber: String,
    val lastFour: String,
)

data class SafepayCardCaptureResult(
    val accessToken: String,
    val deviceDataCollectionURL: String,
    val paymentMethod: SafepayCardCapturePaymentMethod,
    val cardinalJWT: String?,
)

class SafepayCardCaptureController(
    val configuration: SafepayCardCaptureConfiguration,
    val cardFieldView: SafepayCardFieldView,
) {
    constructor(
        context: Context,
        configuration: SafepayCardCaptureConfiguration,
    ) : this(configuration, SafepayCardFieldView(context))

    private val apiService: APIService by lazy {
        RetrofitInstance
            .createRetrofit(configuration.environment.baseUrl)
            .create(APIService::class.java)
    }

    fun validate(): Boolean {
        return cardFieldView.currentCardDetails().complete
    }

    suspend fun fetchValidity(): Boolean {
        return validate()
    }

    fun focus() {
        cardFieldView.focus()
    }

    fun blur() {
        cardFieldView.blur()
    }

    fun clear() {
        cardFieldView.clear()
    }

    suspend fun submit(): SafepayCardCaptureResult {
        val request = buildRequest()
        val response =
            apiService.payerAuthenticationSetup(
                buildUrl(),
                "Bearer ${configuration.authToken}",
                request,
            )

        if (!response.isSuccessful || response.body() == null) {
            throw buildApiError(response)
        }

        return mapResult(response.body()!!)
    }

    private fun buildRequest(): PayerAuthenticationRequest {
        val details = cardFieldView.currentCardDetails(includeSensitiveData = true)

        if (!details.complete) {
            throw PaymentError.invalidCardDetails()
        }

        val cardNumber = details.number
        val cvc = details.cvc
        val expiryMonth = details.expiryMonth
        val expiryYear = details.expiryYear

        if (cardNumber.isNullOrBlank() || cvc.isNullOrBlank() || expiryMonth == null || expiryYear == null) {
            throw PaymentError.invalidCardDetails()
        }

        return PayerAuthenticationRequest(
            entryMode = "raw",
            action = "PAYER_AUTH_SETUP",
            payload =
                PayerAuthenticationRequest.Payload(
                    isMobile = true,
                    paymentMethod =
                        PayerAuthenticationRequest.PaymentMethod(
                            card =
                                PayerAuthenticationRequest.Card(
                                    cardNumber = cardNumber,
                                    expirationMonth = expiryMonth.toString(),
                                    expirationYear = expiryYear.toString(),
                                    cvv = cvc,
                                ),
                        ),
                ),
        )
    }

    private fun mapResult(response: PayerAuthenticationResponse): SafepayCardCaptureResult {
        val paymentMethod = response.data.action.payment_method
        val setup = response.data.action.payer_authentication_setup

        return SafepayCardCaptureResult(
            accessToken = setup.access_token,
            deviceDataCollectionURL = setup.device_data_collection_url,
            paymentMethod =
                SafepayCardCapturePaymentMethod(
                    token = paymentMethod.token,
                    expirationMonth = paymentMethod.expiration_month,
                    expirationYear = paymentMethod.expiration_year,
                    cardTypeCode = paymentMethod.card_type_code,
                    cardType = paymentMethod.card_type,
                    binNumber = paymentMethod.bin_number,
                    lastFour = paymentMethod.last_four,
                ),
            cardinalJWT = setup.cardinalJWT,
        )
    }

    private fun buildUrl(): String {
        return "${configuration.environment.baseUrl}order/payments/v3/${configuration.tracker}"
    }

    private fun buildApiError(response: Response<*>): PaymentError {
        val errorBody = response.errorBody()?.string()
        val parsedMessage =
            errorBody?.let { body ->
                try {
                    Gson().fromJson(body, ErrorResponse::class.java)?.status?.errors?.firstOrNull()
                } catch (_: Exception) {
                    null
                } ?: try {
                    Gson().fromJson(body, LoginErrorResponse::class.java)?.message
                } catch (_: Exception) {
                    null
                }
            }

        return PaymentError(
            code = response.code().toString(),
            msg = parsedMessage ?: PaymentError.generalPaymentError().msg,
        )
    }
}
