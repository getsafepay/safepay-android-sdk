package com.android.safepay.network.model.token

import com.google.gson.annotations.SerializedName

data class ShopperTokenGenerateRequest(
    @SerializedName("type") val type:String?,
    @SerializedName("email") val email:String?,
    @SerializedName("password") val password:String?)