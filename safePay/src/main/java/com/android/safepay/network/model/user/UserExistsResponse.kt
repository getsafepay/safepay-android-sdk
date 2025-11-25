package com.android.safepay.network.model.user

import com.google.gson.annotations.SerializedName

data class UserExistsResponse (

    @SerializedName("data")
    val data: Data
)

data class Data(
    @SerializedName("exists") val exists: Boolean,
    @SerializedName("isLocal") val isLocal: Boolean,
    @SerializedName("hasPassword") val hasPassword: Boolean,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String
)