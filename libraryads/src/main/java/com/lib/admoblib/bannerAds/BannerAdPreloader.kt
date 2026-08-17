package com.lib.admoblib.bannerAds

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.lib.admoblib.isNetworkConnected

/**
 * Keeps adaptive banner [AdView]s loaded **before** a screen needs them, so the banner
 * can be attached and shown instantly (no shimmer wait) on demand.
 *
 * Typical usage:
 *  1. Warm it up on the previous screen (needs an Activity for adaptive sizing):
 *        BannerAdPreloader.preload(activity, adUnitId)                 // standard
 *        BannerAdPreloader.preload(activity, adUnitId, collapsible = true) // collapsible
 *  2. When the screen appears, show it instantly:
 *        binding.adaptiveBanner.showBanner(this, adUnitId, true)
 *
 * The pool holds one ready banner per ad-unit id. The AdView is created with the
 * application context so it never leaks the Activity it was preloaded from.
 */
object BannerAdPreloader {
    private const val TAG = "BannerAdPreloader"

    private val cache = HashMap<String, AdView>()
    private val ready = HashSet<String>()
    private val loading = HashSet<String>()

    /** Load a banner ahead of time and keep it ready for [adUnitId]. */
    @JvmStatic
    @JvmOverloads
    fun preload(
        activity: Activity,
        adUnitId: String,
        collapsible: Boolean = false,
        status: Boolean = true
    ) {
        if (!status) return
        if (!activity.isNetworkConnected()) return
        if (ready.contains(adUnitId) || loading.contains(adUnitId)) {
            Log.d(TAG, "preload skipped, already ready/loading: $adUnitId")
            return
        }

        loading.add(adUnitId)
        val adView = AdView(activity.applicationContext)
        adView.adUnitId = adUnitId
        adView.setAdSize(adaptiveSize(activity))
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                ready.add(adUnitId)
                loading.remove(adUnitId)
                Log.d(TAG, "preloaded banner ready: $adUnitId")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loading.remove(adUnitId)
                cache.remove(adUnitId)
                Log.d(TAG, "preload failed: $adUnitId -> ${error.message}")
            }
        }
        cache[adUnitId] = adView

        val request = if (collapsible) {
            val extras = Bundle().apply { putString("collapsible", "bottom") }
            AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
        } else {
            AdRequest.Builder().build()
        }
        adView.loadAd(request)
    }

    /** True when a preloaded, loaded banner is ready to be shown for [adUnitId]. */
    @JvmStatic
    fun isReady(adUnitId: String): Boolean = ready.contains(adUnitId) && cache[adUnitId] != null

    /**
     * Take (and remove) the ready banner for [adUnitId], detached from any parent,
     * or null if none is ready. The caller becomes the owner and must destroy it.
     */
    @JvmStatic
    fun poll(adUnitId: String): AdView? {
        if (!ready.contains(adUnitId)) return null
        val view = cache.remove(adUnitId) ?: return null
        ready.remove(adUnitId)
        (view.parent as? ViewGroup)?.removeView(view)
        return view
    }

    /** Destroy and drop the ready banner for [adUnitId]. */
    @JvmStatic
    fun clear(adUnitId: String) {
        cache.remove(adUnitId)?.destroy()
        ready.remove(adUnitId)
        loading.remove(adUnitId)
    }

    /** Destroy and drop every cached banner. */
    @JvmStatic
    fun clearAll() {
        cache.values.forEach { it.destroy() }
        cache.clear()
        ready.clear()
        loading.clear()
    }

    private fun adaptiveSize(activity: Activity): AdSize {
        val metrics = activity.resources.displayMetrics
        val adWidth = (metrics.widthPixels / metrics.density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}
