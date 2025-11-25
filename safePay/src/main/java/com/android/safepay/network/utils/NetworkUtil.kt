package com.android.safepay.network.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil
{
    fun isInternetConnected(context: Context): Boolean {
        val cm = checkNotNull(
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}