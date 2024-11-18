package com.lib.admoblib.appOpen

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.multidex.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.lib.admoblib.R
import com.lib.admoblib.utiliz.AdsConstant
import com.lib.admoblib.utiliz.AdsConstant.className
import com.lib.admoblib.utiliz.GoogleMobileAdsConsentManager
import java.util.Date

/** Application class that initializes, loads and show ads when activities change states. */
class AppOpenAd(val context: Context) : Application.ActivityLifecycleCallbacks {
    private var showCounter = 0
    private var AD_UNIT_ID = ""
    private val LOG_TAG = "AppOpenAd"

    private var appOpenAdManager: AppOpenAdManager? = null
    private var currentActivity: Activity? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun appOpenInit(remoteValue: Boolean, adID: String) {
        if (!BuildConfig.DEBUG && adID.contains(context.getString(R.string.AppId))) {
            Log.e(LOG_TAG, "openApp: test AD_ID found($adID)")
            return
        }
        if (!remoteValue) {
            Log.e(LOG_TAG, "openApp: remote false AD_ID($adID)")
            return
        }
        if (!AdsConstant.isNetworkAvailable(context)) {
            Log.e(LOG_TAG, "openApp: no internet connection")
            return
        }
        AD_UNIT_ID = adID
        appOpenAdManager = AppOpenAdManager()
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!AdsConstant.isMainOpenShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        appOpenAdManager?.showAdIfAvailable(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

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
            if (AdsConstant.isMainOpenAppLoadingAd || isAdAvailable()) {
                return
            }
            AdsConstant.isMainOpenAppLoadingAd = true
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
                        AdsConstant.mainAppOpenAd = ad
                        AdsConstant.isMainOpenAppLoadingAd = false
                        loadTime = Date().time
                        Log.d(LOG_TAG, "onAdLoaded.")
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        AdsConstant.isMainOpenAppLoadingAd = false
                        Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
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
            return AdsConstant.mainAppOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        fun showAdIfAvailable(
            activity: Activity,
            onShowAdComplete: (() -> Unit?)? = null
        ) {
            if (AdsConstant.splashAppOpenAd != null ||
                AdsConstant.isSplashOpenAppLoadingAd ||
                AdsConstant.isSplashOpenShowingAd ||
                AdsConstant.isInterstitialAdShowing ||
                AdsConstant.isShowPermissionDialog
            ) {
                return
            }
            if (className.contains("splash", ignoreCase = true) || className.isEmpty()) {
                return
            }
            // If the app open ad is already showing, do not show the ad again.
            if (AdsConstant.isMainOpenShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.")
                return
            }

            // If the app open ad is not available yet, invoke the callback.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.")
                onShowAdComplete?.invoke()
                if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
                }
                return
            }
            showCounter++
            if (showCounter >= 2) {
                showCounter = 0
            } else {
                return
            }
            Log.d(LOG_TAG, "Will show ad.")

            AdsConstant.mainAppOpenAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        AdsConstant.mainAppOpenAd = null
                        AdsConstant.isMainOpenShowingAd = false
                        Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                        onShowAdComplete?.invoke()
                        if (googleMobileAdsConsentManager.canRequestAds) {
                            loadAd(activity)
                        }
                    }

                    /** Called when fullscreen content failed to show. */
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        AdsConstant.mainAppOpenAd = null
                        AdsConstant.isMainOpenShowingAd = false
                        Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                        onShowAdComplete?.invoke()
                        if (googleMobileAdsConsentManager.canRequestAds) {
                            loadAd(activity)
                        }
                    }

                    /** Called when fullscreen content is shown. */
                    override fun onAdShowedFullScreenContent() {
                        Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
                    }
                }
            AdsConstant.isMainOpenShowingAd = true
            AdsConstant.mainAppOpenAd?.show(activity)
        }
    }
}