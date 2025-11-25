package com.android.safepay

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyModuleApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}