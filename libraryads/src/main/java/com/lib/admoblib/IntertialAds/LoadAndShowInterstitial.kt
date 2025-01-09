package com.lib.admoblib.IntertialAds

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class LoadAndShowInterstitial {
    companion object{
        private var mInterstitialAd: InterstitialAd? = null
        private final var TAG = "Tag"
         fun loadInterstitialAd(context: Context,id:String){
             val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context,id, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                  }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    ConstantAds.isInterstitialAvailble=true
                    mInterstitialAd = interstitialAd
                   }
            })
        }
        fun showInterstitial(
            activity: Activity,
            onDismissed: () -> Unit,
            onAdShowed: () -> Unit,
            onAdFailed: () -> Unit
        ) {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        ConstantAds.isInterstitialAvailble = false
                        onDismissed() // Callback after ad dismissal
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        Log.d(TAG, "Ad failed to show.")
                        ConstantAds.isInterstitialAvailble = false
                        onAdFailed() // Callback if the ad fails to show
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        mInterstitialAd = null // Nullify ad after it is shown
                        onAdShowed() // Callback after ad successfully showed
                    }
                }
                mInterstitialAd?.show(activity) // Show the interstitial ad
            } else {
                // If no ad is available, proceed with dismiss behavior
                onDismissed()
            }
        }
    }
}