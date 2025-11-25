package com.android.safepay.network.interfaces

interface ApiResultCallback
{
    fun onSuccess(response: Any?)  // Adjust 'Any' to your specific data type
    fun onFailure(error: String?)
}