package com.lib.admoblib.appOpen

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
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

class AppOpenControl(private val application: Application, private val adId: String) :
    LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private val TAG = "AppOpenControl"
    var appOpenAd: AppOpenAd? = null
    private val adRequest: AdRequest by lazy { AdRequest.Builder().build() }

    private var currentActivity: Activity? = null
    // Default is true to show ads

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

        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                Log.d(TAG, "Ad Loaded")
                appOpenAd = ad
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                appOpenAd = null
                Log.d(TAG, "failed to load " + loadAdError.message)
            }
        }

        AppOpenAd.load(
            application, adId, adRequest, loadCallback
        )
    }

    private fun showAdIfAvailable() {
        if (appOpenAd != null && currentActivity != null && shouldShowAd) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "onAdDismissedFullScreenContent: ")
                    appOpenAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    loadAd()
                    Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "onAdShowedFullScreenContent: ")
                }
            }
            currentActivity?.let { appOpenAd?.show(currentActivity!!) }
        } else {
            Log.d(TAG, "Can not show ad or flag is false.")
            loadAd()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
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
        currentActivity = null
    }

    companion object {
        var shouldShowAd: Boolean = true
        fun setAdsOpenFragment(fragment: Fragment?, fragmentname: String?) {
            // Disable ads for the specified fragment
            if (fragment != null && fragmentname != null && fragment::class == fragmentname::class) {
                shouldShowAd = false // Disable ads for the specified fragment
            } else {
                shouldShowAd = true // Enable ads for other fragments
            }
        }

        //
        fun setAppOpenActivity(fragment: Activity?, fragmentname: String) {
            // Disable ads for the specified fragment
            if (fragment != null && fragmentname != null && fragment::class == fragmentname::class) {
                shouldShowAd = false // Disable ads for the specified fragment
            } else {
                shouldShowAd = true // Enable ads for other fragments
            }
        }
    }

}
