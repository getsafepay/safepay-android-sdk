package com.android.safepay.network.model

data class PaymentRequest(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val amount: Double
)

data class PaymentResponse(
    val success: Boolean,
    val message: String
)