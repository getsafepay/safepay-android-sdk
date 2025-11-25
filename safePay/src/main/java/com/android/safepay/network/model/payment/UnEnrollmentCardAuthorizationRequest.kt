package com.android.safepay.network.model.payment

import com.google.gson.annotations.SerializedName

data class UnEnrollmentCardAuthorizationRequest(

    @SerializedName("payload")
    val payload: Payload)
{
    data class Payload(
        @SerializedName("authorization")
        val authorization: Authorization,

        @SerializedName("is_mobile")
        var isMobile:Boolean
    )

    data class Authorization(
        @SerializedName("do_capture")
        val do_capture: Boolean
    )
}