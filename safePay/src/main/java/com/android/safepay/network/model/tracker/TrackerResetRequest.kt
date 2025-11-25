package com.android.safepay.network.model.tracker

import com.google.gson.annotations.SerializedName

data class TrackerResetRequest(
    @SerializedName("action") val action:String,
    @SerializedName("payload") val resetPayload: TrackerResetPayload)
{
    data class TrackerResetPayload(val data: String = "")
}
