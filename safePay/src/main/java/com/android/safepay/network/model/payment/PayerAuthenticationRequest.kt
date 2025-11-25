package com.android.safepay.network.model.payment

import com.google.gson.annotations.SerializedName

data class PayerAuthenticationRequest(
    @SerializedName("entry_mode") val entryMode: String? = null, // Optional for dynamic case
    @SerializedName("payload") val payload: Payload,
    @SerializedName("action") val action: String? = null // Optional if not needed in all cases,
) {

    data class Payload(
        @SerializedName("is_mobile") val isMobile: Boolean? = null, // Optional for dynamic case
        @SerializedName("payment_method") val paymentMethod: PaymentMethod
    )

    data class PaymentMethod(
        @SerializedName("card") val card: Card? = null, // Nullable to handle different cases
        @SerializedName("tokenized_card") val tokenizedCard: TokenizedCard? = null // New optional field
    )

    data class Card(
        @SerializedName("card_number") val cardNumber: String,
        @SerializedName("expiration_month") val expirationMonth: String,
        @SerializedName("expiration_year") val expirationYear: String,
        @SerializedName("cvv") val cvv: String
    )

    data class TokenizedCard(
        @SerializedName("token") val token: String
    )
}
