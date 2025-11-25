package com.android.safepay.network.model.payment

import com.android.safepay.network.error.PaymentError

sealed class PaymentResult {
    object Completed : PaymentResult()
    object Canceled : PaymentResult()
    data class Failed(val error: PaymentError) : PaymentResult()
}