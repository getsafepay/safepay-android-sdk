package com.android.safepay.network.ui

class SafePayConfiguration(val trackerToken: String, val timeBaseToken:String, val addressToken:String? =null,
                           val sandBox:Boolean=false)

{
    fun validateConfiguration(): Boolean {
        if (trackerToken.isEmpty() || timeBaseToken.isEmpty()) {
            return false // Tokens are invalid
        }
        return true // Tokens are valid
    }
}