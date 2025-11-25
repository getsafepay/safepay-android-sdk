package com.android.safepay.network.model.token

import com.google.gson.annotations.SerializedName

data class TokenResponse(

    @SerializedName("data")
    val data: Data,

    @SerializedName("status")
    val status: Status)
{

    data class Data(
        @SerializedName("session")
        val session: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("refresh_token")
        val refreshToken:String
    )

    data class Status(
        @SerializedName("errors")
        val errors: List<String>,

        @SerializedName("message")
        val message: String
    )
}