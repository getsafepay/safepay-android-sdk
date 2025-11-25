package com.android.safepay.network.model.paymentmethod

import com.google.gson.annotations.SerializedName

data class PaymentMethodListResponse(
    @SerializedName("data") var data: List<PaymentData>
)

data class PaymentData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: String,
    @SerializedName("payment_method_token") val paymentMethodToken: String,
    @SerializedName("deduplication_key") val deduplicationKey: String,
    @SerializedName("intent") val intent: String,
    @SerializedName("last_4") val last4: String,
    @SerializedName("instrument_type") val instrumentType: String,
    @SerializedName("expiry_month") val expiryMonth: String,
    @SerializedName("expiry_year") val expiryYear: String,
    @SerializedName("contact") val contact: Contact,
    @SerializedName("address") val address: Address,
    @SerializedName("cybersource") val cybersource: Cybersource,
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

data class Address(
    @SerializedName("token") val token: String,
    @SerializedName("owner") val owner: String,
    @SerializedName("street1") val street1: String,
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String,
    @SerializedName("is_default") val isDefault: Boolean,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Cybersource(
    @SerializedName("token") val token: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("scheme") val scheme: Int,
    @SerializedName("bin") val bin: String,
    @SerializedName("last_four") val lastFour: String,
    @SerializedName("expiry_month") val expiryMonth: String,
    @SerializedName("expiry_year") val expiryYear: String,
    @SerializedName("created_at") val createdAt: Timestamp,
    @SerializedName("updated_at") val updatedAt: Timestamp
)

data class Timestamp(
    @SerializedName("seconds") val seconds: Long
)