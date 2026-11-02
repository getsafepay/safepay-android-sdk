package com.android.safepay.network.ui.card

import com.android.safepay.network.model.payment.PayerAuthenticationRequest
import com.android.safepay.network.model.payment.PayerAuthenticationResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

internal interface CardCaptureApiService {
    @Headers("Content-Type: application/json")
    @POST
    suspend fun payerAuthenticationSetup(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body payerAuthenticationRequest: PayerAuthenticationRequest,
    ): Response<PayerAuthenticationResponse>

    companion object {
        // No HTTP body logging: requests carry the raw card number and CVV.
        fun create(baseUrl: String): CardCaptureApiService =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CardCaptureApiService::class.java)
    }
}
