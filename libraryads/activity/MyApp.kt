package com.lib.admoblib.activity

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.lib.admobeadslib.R
import com.lib.admoblib.AppOpenManager

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        initializeMobileAdsSdk()
    }

    private fun initializeMobileAdsSdk() {
        MobileAds.initialize(this) {}
        AppOpenManager(this, R.string.AppOpen)
    }
}