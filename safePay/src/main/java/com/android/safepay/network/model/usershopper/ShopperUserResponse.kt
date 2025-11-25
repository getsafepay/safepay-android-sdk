package com.android.safepay.network.model.usershopper

import com.google.gson.annotations.SerializedName

data class ShopperUserResponse(
    @SerializedName("data") val data: UserData
)

data class UserData(
    @SerializedName("token") val token: String,
    @SerializedName("contacts") val contacts: List<Contact>,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("suspended") val suspended: Int,
    @SerializedName("suspend_reason") val suspendReason: String,
    @SerializedName("verified") val verified: Int,
    @SerializedName("verification") val verification: Verification,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class Contact(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("is_default") val isDefault: Boolean,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Verification(
    @SerializedName("user_id") val userId: String,
    @SerializedName("code") val code: String,
    @SerializedName("verification_type") val verificationType: Int,
    @SerializedName("expires_at") val expiresAt: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class Timestamp(
    @SerializedName("seconds") val seconds: Long,
    @SerializedName("nanos") val nanos: Int
)
