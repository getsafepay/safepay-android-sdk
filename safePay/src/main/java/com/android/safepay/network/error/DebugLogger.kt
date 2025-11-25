package com.android.safepay.network.error

import android.os.Build
import android.util.Log
import com.hbb20.BuildConfig

class DebugLogger
{
    companion object {
        // Check if the app is in debug mode
        private val isDebugMode: Boolean
            get() = BuildConfig.DEBUG

        private var TAG = "SAFEPAY Logs"

        fun d(message: String) {
            if (isDebugMode) {
                Log.d(TAG, message)
            }
        }

        fun e(message: String) {
            if (isDebugMode) {
                Log.e(TAG, message)
            }
        }

        fun i(message: String) {
            if (isDebugMode) {
                Log.i(TAG, message)
            }
        }

        fun w( message: String) {
            if (isDebugMode) {
                Log.w(TAG, message)
            }
        }

        fun v(message: String) {
            if (isDebugMode) {
                Log.v(TAG, message)
            }
        }

        fun logException(exception: Exception) {
            if (isDebugMode) {
                Log.e(TAG, exception.localizedMessage ?: "Exception occurred", exception)
            }
        }
    }
}