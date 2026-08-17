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
import com.lib.admoblib.AdsCallBack
import com.lib.admoblib.isNetworkConnected


class LoadAndShowInterstitial {
    companion object {
        private var mInterstitialAd: InterstitialAd? = null
        private const val TAG = "InterstitialAds"

        /** True when an interstitial is loaded and ready to show. */
        fun isInterstitialReady(): Boolean = mInterstitialAd != null

        /**
         * Load an interstitial ad. Fires the [callback] lifecycle events:
         * [AdsCallBack.onAdLoading] before the request, then [AdsCallBack.onAdLoaded]
         * or [AdsCallBack.onFailedToLoad].
         */
        @JvmStatic
        @JvmOverloads
        fun loadInterstitialAd(context: Context, id: String, callback: AdsCallBack? = null) {
            if (!context.isNetworkConnected()) {
                callback?.onFailedToLoad(null)
                return
            }
            callback?.onAdLoading()
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, id, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "onFailedToLoad: ${adError.message}")
                    mInterstitialAd = null
                    ConstantAds.isInterstitialAvailble = false
                    callback?.onFailedToLoad(adError)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "onAdLoaded")
                    ConstantAds.isInterstitialAvailble = true
                    mInterstitialAd = interstitialAd
                    callback?.onAdLoaded()
                }
            })
        }

        /**
         * Show the already-loaded interstitial and report every full-screen event through
         * [callback]: [AdsCallBack.onAdOpened] (shown), [AdsCallBack.onAdImpression],
         * [AdsCallBack.onAdClicked], [AdsCallBack.onAdClosed] (dismissed) and
         * [AdsCallBack.onFailedToLoad] (failed to show). [onDismissed] always runs when the
         * flow finishes (whether the ad showed, failed, or none was ready) — use it to
         * continue navigation.
         */
        @JvmStatic
        @JvmOverloads
        fun showInterstitial(
            activity: Activity,
            callback: AdsCallBack? = null,
            onDismissed: () -> Unit
        ) {
            val ad = mInterstitialAd
            if (ad != null) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "onAdShowedFullScreenContent")
                        mInterstitialAd = null
                        callback?.onAdOpened()
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "onAdImpression")
                        callback?.onAdImpression()
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "onAdClicked")
                        callback?.onAdClicked()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "onAdDismissedFullScreenContent")
                        ConstantAds.isInterstitialAvailble = false
                        callback?.onAdClosed()
                        onDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "onAdFailedToShow: ${adError.message}")
                        mInterstitialAd = null
                        ConstantAds.isInterstitialAvailble = false
                        callback?.onFailedToLoad(adError)
                        onDismissed()
                    }
                }
                ad.show(activity)
            } else {
                // No ad available -> just continue.
                onDismissed()
            }
        }

        /**
         * Convenience: load an interstitial and show it as soon as it is ready. Reports the
         * same [callback] events as [loadInterstitialAd] + [showInterstitial]. If loading
         * fails, [onDismissed] runs immediately so the app keeps flowing.
         */
        @JvmStatic
        @JvmOverloads
        fun loadAndShowInterstitial(
            activity: Activity,
            id: String,
            callback: AdsCallBack? = null,
            onDismissed: () -> Unit
        ) {
            if (!activity.isNetworkConnected()) {
                callback?.onFailedToLoad(null)
                onDismissed()
                return
            }
            callback?.onAdLoading()
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(activity, id, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "onAdLoaded")
                    ConstantAds.isInterstitialAvailble = true
                    mInterstitialAd = interstitialAd
                    callback?.onAdLoaded()
                    showInterstitial(activity, callback, onDismissed)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "onFailedToLoad: ${adError.message}")
                    mInterstitialAd = null
                    ConstantAds.isInterstitialAvailble = false
                    callback?.onFailedToLoad(adError)
                    onDismissed()
                }
            })
        }
    }
}
