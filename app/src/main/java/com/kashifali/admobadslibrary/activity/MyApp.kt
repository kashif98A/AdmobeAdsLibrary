package com.kashifali.admobadslibrary.activity

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.kashifali.admobadslibrary.R

import com.lib.admoblib.appOpen.AppOpenControl
import com.lib.admoblib.nativeAds.NativeAdPreloader

class MyApp:Application() {
    var appOpenManager: AppOpenControl?=null
    override fun onCreate() {
        super.onCreate()
        appOpenManager = AppOpenControl(this,  this.getString(R.string.AppOpen))

        // Warm up the Google Mobile Ads SDK at startup. Without this the very first
        // ad request has to cold-start the whole SDK, which is the main reason the
        // first native/banner feels slow. Once initialized, preload a native ad so
        // the first screen that needs it can show instantly (on demand).
        MobileAds.initialize(this) {
            NativeAdPreloader.preload(this, getString(R.string.NativeMain))
        }
    }
}