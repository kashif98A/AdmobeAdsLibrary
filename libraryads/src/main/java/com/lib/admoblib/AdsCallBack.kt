package com.lib.admoblib

import com.google.android.gms.ads.AdError




interface AdsCallBack {

    fun onNextAction() {
        // Default implementation does nothing.
        // Override to provide custom behavior.
    }


    fun onFailedToLoad(error: AdError?) {}

    fun onAdLoaded() {}
}