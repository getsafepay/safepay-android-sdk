package com.android.safepay.network.model.address

import com.google.gson.annotations.SerializedName

data class FetchAddressResponse(
    @SerializedName("data") val data: Address?,
    @SerializedName("status") val status: Status?)

    data class Address(
        @SerializedName("token") val token: String?,
        @SerializedName("street1") val street1: String?,
        @SerializedName("street2") val street2: String?,
        @SerializedName("city") val city: String?,
        @SerializedName("state") val state: String?,
        @SerializedName("postal_code") val postalCode: String?,
        @SerializedName("country") val country: String?,
        @SerializedName("is_default") val isDefault: Boolean?,
        @SerializedName("created_at") val createdAt: Timestamp?,
        @SerializedName("updated_at") val updatedAt: Timestamp?
    )

    data class Timestamp(
        @SerializedName("seconds") val seconds: Long?
    )

    data class Status(
        @SerializedName("errors") val errors: List<String>?,
        @SerializedName("message") val message: String?
    )
