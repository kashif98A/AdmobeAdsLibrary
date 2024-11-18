package com.lib.admoblib.nativeAds

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

object NativeAdManager {
    var nativeAd: NativeAd? = null

    fun loadAd(activity: Activity, admobNativeId: String, onAdLoaded: (NativeAd) -> Unit) {
        val adLoader = AdLoader.Builder(activity, admobNativeId)
            .forNativeAd { ad ->
                nativeAd = ad
                onAdLoaded(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    nativeAd = null // Clear if loading fails
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun clearAd() {
        nativeAd?.destroy()
        nativeAd = null
    }
}
