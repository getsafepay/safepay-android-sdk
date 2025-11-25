package com.android.safepay.network.model.payment

import com.google.gson.annotations.SerializedName

data class EnrollmentRequest(
    @SerializedName("payload") val payload: Payload
)

data class Payload(
    @SerializedName("billing") val billing: Billing,
    @SerializedName("authorization") val authorization: Authorization,
    @SerializedName("authentication_setup") var authenticationSetup: AuthenticationSetup
)

data class Billing(
    @SerializedName("street_1") val street1: String,
    @SerializedName("street_2") val street2: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("country") val country: String
)

data class Authorization(
    @SerializedName("do_capture") val doCapture: Boolean,
    @SerializedName("do_card_on_file") val doCardOnFile:Boolean=false
)

data class AuthenticationSetup(
    @SerializedName("success_url") val successUrl: String,
    @SerializedName("failure_url") val failureUrl: String,
    @SerializedName("device_fingerprint_session_id") var deviceFingerSessionId:String,
    @SerializedName("sdk_reference_id") var sdkReferenceId:String
)
