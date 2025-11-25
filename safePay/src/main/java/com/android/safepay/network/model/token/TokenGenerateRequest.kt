package com.android.safepay.network.model.token

import com.google.gson.annotations.SerializedName

data class TokenGenerateRequest(
    @SerializedName("first_name") val firstName:String,
    @SerializedName("last_name") val lastName:String,
    @SerializedName("email")
    val email:String,
    @SerializedName("phone")
    val phone:String,
    @SerializedName("country")
    val country:String)