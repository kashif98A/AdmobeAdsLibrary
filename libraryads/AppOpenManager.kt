package com.lib.admoblib

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenManager(private val application: Application, private val adId: Int) :
    LifecycleObserver,
    Application.ActivityLifecycleCallbacks {
    private val TAG = "AppOpenManager"
    var appOpenAd: AppOpenAd? = null
    private val adRequest: AdRequest by lazy { AdRequest.Builder().build() }

    //    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null
    private var currentActivity: Activity? = null
//    private var loadTime: Long = 0

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
            application,
            application.getString(adId),
            adRequest,
            loadCallback as AppOpenAd.AppOpenAdLoadCallback
        )
    }

    private fun showAdIfAvailable() {
        if (appOpenAd != null) {
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
            Log.d(TAG, "Can not show ad.")
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
}