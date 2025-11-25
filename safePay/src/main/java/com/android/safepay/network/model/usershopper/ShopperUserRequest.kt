package com.android.safepay.network.model.usershopper

import com.google.gson.annotations.SerializedName

data class ShopperUserRequest(
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("password") val password: String?
)
