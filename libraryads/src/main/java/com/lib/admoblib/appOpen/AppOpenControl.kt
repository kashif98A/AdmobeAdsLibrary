package com.lib.admoblib.appOpen

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.lib.admoblib.R

class AppOpenControl(private val application: Application, private val adId: String) :
    LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private val TAG = "AppOpenControl"
    var appOpenAd: AppOpenAd? = null
    private val adRequest: AdRequest by lazy { AdRequest.Builder().build() }

    private var currentActivity: Activity? = null
    private var welcomeDialog: Dialog? = null
    private var isShowingWelcomeDialog = false
    private var isAdLoadingInProgress = false
    private var isFirstLaunch = true
    private val handler = Handler(Looper.getMainLooper())

    /** Maximum time (ms) to wait for ad to load before dismissing dialog */
    var welcomeDialogTimeout: Long = 10000L

    /** Custom welcome dialog layout resource (optional) */
    var welcomeDialogLayout: Int = R.layout.dialog_welcome_loading

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadAd()
    }

    private fun loadAd() {
        if (appOpenAd != null) {
            Log.d(TAG, "Ad is already loaded")
            return
        }
        if (isAdLoadingInProgress) {
            Log.d(TAG, "Ad is already loading")
            return
        }

        isAdLoadingInProgress = true
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                Log.d(TAG, "Ad Loaded")
                isAdLoadingInProgress = false
                appOpenAd = ad
                // Ad loaded while welcome dialog is showing -> show ad immediately
                if (isShowingWelcomeDialog) {
                    handler.post { showAdFromDialog() }
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isAdLoadingInProgress = false
                appOpenAd = null
                Log.d(TAG, "failed to load " + loadAdError.message)
                // Ad failed to load -> dismiss welcome dialog
                if (isShowingWelcomeDialog) {
                    handler.post { dismissWelcomeDialog() }
                }
            }
        }

        AppOpenAd.load(
            application, adId, adRequest, loadCallback
        )
    }

    private fun showWelcomeAndLoadAd() {
        val activity = currentActivity ?: return
        if (!shouldShowAd) {
            Log.d(TAG, "shouldShowAd is false, skipping")
            return
        }
        if (activity.isFinishing || activity.isDestroyed) return
        if (isShowingWelcomeDialog) return

        // Always show welcome dialog first
        showWelcomeDialog(activity)

        if (appOpenAd != null) {
            // Ad already loaded -> show dialog briefly then show ad
            handler.postDelayed({
                if (isShowingWelcomeDialog) {
                    showAdFromDialog()
                }
            }, 1500)
        } else {
            // Ad not loaded -> load it now, dialog will dismiss when ad loads
            loadAd()

            // Timeout: dismiss dialog if ad doesn't load in time
            handler.postDelayed({
                if (isShowingWelcomeDialog) {
                    Log.d(TAG, "Welcome dialog timeout, dismissing")
                    dismissWelcomeDialog()
                }
            }, welcomeDialogTimeout)
        }
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
            Log.d(TAG, "Welcome dialog shown")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing welcome dialog: ${e.message}")
            isShowingWelcomeDialog = false
        }
    }

    private fun showAdFromDialog() {
        dismissWelcomeDialog()
        showAdDirectly()
    }

    private fun showAdDirectly() {
        if (appOpenAd != null && currentActivity != null) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "onAdDismissedFullScreenContent")
                    appOpenAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    loadAd()
                    Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "onAdShowedFullScreenContent")
                }
            }
            currentActivity?.let { appOpenAd?.show(it) }
        } else {
            Log.d(TAG, "Can not show ad")
            loadAd()
        }
    }

    private fun dismissWelcomeDialog() {
        try {
            if (welcomeDialog != null && welcomeDialog!!.isShowing) {
                welcomeDialog?.dismiss()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error dismissing welcome dialog: ${e.message}")
        }
        welcomeDialog = null
        isShowingWelcomeDialog = false
        handler.removeCallbacksAndMessages(null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        // Skip first launch - only show when user comes BACK to app
        if (isFirstLaunch) {
            isFirstLaunch = false
            Log.d(TAG, "First launch, skipping welcome dialog")
            return
        }
        showWelcomeAndLoadAd()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
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

    companion object {
        var shouldShowAd: Boolean = true
        fun setAdsOpenFragment(fragment: Fragment?, fragmentname: String?) {
            if (fragment != null && fragmentname != null && fragment::class == fragmentname::class) {
                shouldShowAd = false
            } else {
                shouldShowAd = true
            }
        }

        fun setAppOpenActivity(fragment: Activity?, fragmentname: String) {
            if (fragment != null && fragmentname != null && fragment::class == fragmentname::class) {
                shouldShowAd = false
            } else {
                shouldShowAd = true
            }
        }
    }
}