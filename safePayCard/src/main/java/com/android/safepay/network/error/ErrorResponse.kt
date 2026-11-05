package com.android.safepay.network.error

data class ErrorResponse(
    val data: Any?, // Can be nullable since "data" is null in your example
    val status: Status
)

data class Status(
    val errors: List<String>?,
    val message: String
)
