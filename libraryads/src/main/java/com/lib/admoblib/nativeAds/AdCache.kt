package com.lib.admoblib.nativeAds

import com.google.android.gms.ads.nativead.NativeAd


object AdCache {
    private var preloadedNativeAd: NativeAd? = null

    fun setNativeAd(nativeAd: NativeAd) {
        // Clean up any previously cached ad
        preloadedNativeAd?.destroy()
        preloadedNativeAd = nativeAd
        println("AdCache: Native ad has been cached.")
    }

    fun getCachedAd(): NativeAd? {
        return preloadedNativeAd
    }

    fun clear() {
        preloadedNativeAd?.destroy()
        preloadedNativeAd = null
        println("AdCache: Native ad cache has been cleared.")
    }
}

