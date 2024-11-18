package com.lib.admoblib.appOpen

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.multidex.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.lib.admoblib.R
import com.lib.admoblib.isNetworkConnected
import com.lib.admoblib.utiliz.AdsConstant
import com.lib.admoblib.utiliz.GoogleMobileAdsConsentManager
import java.util.Date

/** Application class that initializes, loads and show ads when activities change states. */
class SplashAppOpenAd(
    val context: Context,
    private val onShowAdCompleteListener: OnShowAdCompleteListener
) : DefaultLifecycleObserver {
    private var AD_UNIT_ID = ""
    private var appOpenAdManager: AppOpenAdManager? = null
    private var currentActivity: Activity? = null
    private var showOnStart = true

    fun appOpenInit(remoteValue: Boolean, adID: String) {
        if (!BuildConfig.DEBUG && adID.contains(context.getString(R.string.AppOpen))) {
//            Log.e(AdsConstant.TAG, "splashOpenApp: test AD_ID found($adID)")
            return
        }
        if (!remoteValue) {
//            Log.e(AdsConstant.TAG, "splashOpenApp: remote false AD_ID($adID)")
            onShowAdCompleteListener.onShowAdComplete()
            return
        }
        if (context.isNetworkConnected()) {
//            Log.e(AdsConstant.TAG, "splashOpenApp: no internet connection")
            return
        }
        AD_UNIT_ID = adID
        appOpenAdManager = AppOpenAdManager()
        appOpenAdManager?.showAdIfAvailable(currentActivity!!, onShowAdCompleteListener)
    }

    override fun onCreate(owner: LifecycleOwner) {}

    override fun onStart(owner: LifecycleOwner) {
        if (owner is Fragment) {
            currentActivity = owner.activity
            appOpenAdManager?.showAdIfAvailable(currentActivity!!, onShowAdCompleteListener)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        if (owner is Fragment && currentActivity != null) {
            appOpenAdManager?.showAdIfAvailable(currentActivity!!, onShowAdCompleteListener)
        }
    }

    override fun onPause(owner: LifecycleOwner) {}

    override fun onStop(owner: LifecycleOwner) {}

    override fun onDestroy(owner: LifecycleOwner) {}

    /** Inner class that loads and shows app open ads. */
    private inner class AppOpenAdManager {

        private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(context)

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        fun loadAd(context: Context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (AdsConstant.isSplashOpenAppLoadingAd || isAdAvailable()) {
                return
            }
            if (!showOnStart) return
            AdsConstant.isSplashOpenAppLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                AD_UNIT_ID,
                request,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    override fun onAdLoaded(ad: AppOpenAd) {
                        AdsConstant.splashAppOpenAd = ad
                        AdsConstant.isSplashOpenAppLoadingAd = false
                        loadTime = Date().time
                        Log.d(AdsConstant.TAG, "onAdLoaded.")
                        if (showOnStart) {
                            showAdIfAvailable(currentActivity ?: return, onShowAdCompleteListener)
                        }
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        AdsConstant.isSplashOpenAppLoadingAd = false
                        showOnStart = false
                        Log.d(AdsConstant.TAG, "onAdFailedToLoad: " + loadAdError.message)
                    }
                },
            )
        }

        /** Check if ad was loaded more than n hours ago. */
        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        /** Check if ad exists and can be shown. */
        private fun isAdAvailable(): Boolean {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return AdsConstant.splashAppOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener
        ) {
            // If the app open ad is already showing, do not show the ad again.
            if (AdsConstant.isSplashOpenShowingAd) {
                Log.d(AdsConstant.TAG, "The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback.
            if (!showOnStart) return

            if (!isAdAvailable()) {
                if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
                }
                return
            }
            Log.d(AdsConstant.TAG, "Will show ad.")

            AdsConstant.splashAppOpenAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        AdsConstant.splashAppOpenAd = null
                        AdsConstant.isSplashOpenShowingAd = false
                        showOnStart = false
                        Log.d(AdsConstant.TAG, "onAdDismissedFullScreenContent.")
                        onShowAdCompleteListener.onShowAdComplete()
                    }

                    /** Called when fullscreen content failed to show. */
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        AdsConstant.splashAppOpenAd = null
                        AdsConstant.isSplashOpenShowingAd = false
                        showOnStart = false
                        Log.d(AdsConstant.TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                        onShowAdCompleteListener.onShowAdComplete()
                    }

                    /** Called when fullscreen content is shown. */
                    override fun onAdShowedFullScreenContent() {
                        showOnStart = false
                        Log.d(AdsConstant.TAG, "onAdShowedFullScreenContent.")
                    }
                }
            if (showOnStart) {
                AdsConstant.isSplashOpenShowingAd = true
                AdsConstant.splashAppOpenAd?.show(activity)
            } else {
                AdsConstant.splashAppOpenAd = null
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete (i.e.
     * dismissed or fails to show).
     */
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }
}