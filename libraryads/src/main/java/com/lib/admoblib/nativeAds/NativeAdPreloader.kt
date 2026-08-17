package com.lib.admoblib.nativeAds

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.lib.admoblib.isNetworkConnected

/**
 * A tiny in-memory pool that keeps native ads ready **before** a screen needs them,
 * so they can be shown instantly (no shimmer wait) on demand.
 *
 * Typical usage:
 *  1. Warm it up early (e.g. in Application.onCreate right after MobileAds.initialize,
 *     or on the previous screen):
 *        NativeAdPreloader.preload(context, adUnitId)
 *  2. When the screen appears, show the cached ad instantly:
 *        binding.nativeLarge.showNativeAd(this, adUnitId, true)
 *
 * The pool holds one ready ad per ad-unit id. Consuming an ad automatically frees the
 * slot; call [preload] again (the show* helpers do this for you) to keep it warm.
 */
object NativeAdPreloader {
    private const val TAG = "NativeAdPreloader"

    private val cache = HashMap<String, NativeAd>()
    private val loading = HashSet<String>()

    /** Load a native ad ahead of time and keep it ready for [adUnitId]. */
    @JvmStatic
    @JvmOverloads
    fun preload(context: Context, adUnitId: String, status: Boolean = true) {
        if (!status) return
        if (!context.isNetworkConnected()) return
        if (cache.containsKey(adUnitId) || loading.contains(adUnitId)) {
            Log.d(TAG, "preload skipped, already ready/loading: $adUnitId")
            return
        }

        loading.add(adUnitId)
        // Use application context so a preloaded ad never leaks an Activity.
        val adLoader = AdLoader.Builder(context.applicationContext, adUnitId)
            .forNativeAd { nativeAd ->
                cache[adUnitId]?.destroy()
                cache[adUnitId] = nativeAd
                loading.remove(adUnitId)
                Log.d(TAG, "preloaded native ready: $adUnitId")
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading.remove(adUnitId)
                    Log.d(TAG, "preload failed: $adUnitId -> ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    /** True when a preloaded ad is ready to be shown for [adUnitId]. */
    @JvmStatic
    fun isReady(adUnitId: String): Boolean = cache[adUnitId] != null

    /**
     * Take (and remove) the ready ad for [adUnitId], or null if none is ready.
     * The caller becomes the owner and is responsible for destroying it.
     */
    @JvmStatic
    fun poll(adUnitId: String): NativeAd? = cache.remove(adUnitId)

    /** Destroy and drop the ready ad for [adUnitId]. */
    @JvmStatic
    fun clear(adUnitId: String) {
        cache.remove(adUnitId)?.destroy()
    }

    /** Destroy and drop every cached ad. */
    @JvmStatic
    fun clearAll() {
        cache.values.forEach { it.destroy() }
        cache.clear()
    }
}
