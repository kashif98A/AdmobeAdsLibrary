package com.lib.admoblib.appOpen

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
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

    // Welcome dialog fields
    private var welcomeDialog: Dialog? = null
    private var isShowingWelcomeDialog = false
    private var isFirstLaunch = true
    private val handler = Handler(Looper.getMainLooper())

    /** Maximum time (ms) to wait for ad to load before dismissing dialog */
    var welcomeDialogTimeout: Long = 10000L

    /** Custom welcome dialog layout resource (optional) */
    var welcomeDialogLayout: Int = R.layout.dialog_welcome_loading

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
        if (!AdsConstant.isMainOpenShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (!AdsConstant.isMainOpenShowingAd) {
            currentActivity = activity
        }

        // Skip first launch - only show when user comes BACK to app
        if (isFirstLaunch) {
            isFirstLaunch = false
            Log.d(LOG_TAG, "First launch, skipping welcome dialog")
            return
        }

        appOpenAdManager?.showWelcomeAndLoadAd(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
        dismissWelcomeDialog()
    }

    private fun showWelcomeDialog(activity: Activity) {
        try {
            if (activity.isFinishing || activity.isDestroyed) return

            welcomeDialog = Dialog(activity, android.R.style.Theme_NoTitleBar_Fullscreen).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(welcomeDialogLayout)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            }
            welcomeDialog?.show()
            isShowingWelcomeDialog = true
            Log.d(LOG_TAG, "Welcome dialog shown")
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error showing welcome dialog: ${e.message}")
            isShowingWelcomeDialog = false
        }
    }

    private fun dismissWelcomeDialog() {
        try {
            if (welcomeDialog != null && welcomeDialog!!.isShowing) {
                welcomeDialog?.dismiss()
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error dismissing welcome dialog: ${e.message}")
        }
        welcomeDialog = null
        isShowingWelcomeDialog = false
        handler.removeCallbacksAndMessages(null)
    }

    /** Inner class that loads and shows app open ads. */
    private inner class AppOpenAdManager {
        private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(context)

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

        fun loadAd(context: Context) {
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
                    override fun onAdLoaded(ad: AppOpenAd) {
                        AdsConstant.mainAppOpenAd = ad
                        AdsConstant.isMainOpenAppLoadingAd = false
                        loadTime = Date().time
                        Log.d(LOG_TAG, "onAdLoaded.")
                        // Ad loaded while welcome dialog is showing -> show ad
                        if (isShowingWelcomeDialog && currentActivity != null) {
                            handler.post {
                                dismissWelcomeDialog()
                                showAdDirectly(currentActivity!!)
                            }
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        AdsConstant.isMainOpenAppLoadingAd = false
                        Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                        // Ad failed -> dismiss welcome dialog
                        if (isShowingWelcomeDialog) {
                            handler.post { dismissWelcomeDialog() }
                        }
                    }
                },
            )
        }

        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        private fun isAdAvailable(): Boolean {
            return AdsConstant.mainAppOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        fun showWelcomeAndLoadAd(activity: Activity) {
            // All the existing skip conditions
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
            if (AdsConstant.isMainOpenShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.")
                return
            }
            if (activity.isFinishing || activity.isDestroyed) return
            if (isShowingWelcomeDialog) return

            // Show counter logic
            showCounter++
            if (showCounter >= 2) {
                showCounter = 0
            } else {
                return
            }

            // Always show welcome dialog first
            showWelcomeDialog(activity)

            if (isAdAvailable()) {
                // Ad already loaded -> show dialog briefly then show ad
                handler.postDelayed({
                    if (isShowingWelcomeDialog) {
                        dismissWelcomeDialog()
                        showAdDirectly(activity)
                    }
                }, 1500)
            } else {
                // Ad not loaded -> load it now, dialog will dismiss when ad loads
                if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
                }

                // Timeout: dismiss dialog if ad doesn't load in time
                handler.postDelayed({
                    if (isShowingWelcomeDialog) {
                        Log.d(LOG_TAG, "Welcome dialog timeout, dismissing")
                        dismissWelcomeDialog()
                    }
                }, welcomeDialogTimeout)
            }
        }

        private fun showAdDirectly(activity: Activity) {
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "Ad not available to show")
                if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
                }
                return
            }

            Log.d(LOG_TAG, "Will show ad.")
            AdsConstant.mainAppOpenAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        AdsConstant.mainAppOpenAd = null
                        AdsConstant.isMainOpenShowingAd = false
                        Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                        if (googleMobileAdsConsentManager.canRequestAds) {
                            loadAd(activity)
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        AdsConstant.mainAppOpenAd = null
                        AdsConstant.isMainOpenShowingAd = false
                        Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                        if (googleMobileAdsConsentManager.canRequestAds) {
                            loadAd(activity)
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
                    }
                }
            AdsConstant.isMainOpenShowingAd = true
            AdsConstant.mainAppOpenAd?.show(activity)
        }
    }
}
