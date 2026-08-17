package com.lib.admoblib

import com.google.android.gms.ads.AdError

/**
 * Callback for ad lifecycle events. Every method has a default (empty) body, so you only
 * override the ones you care about:
 *
 * ```
 * binding.nativeLarge.nativeAdsCallback(object : AdsCallBack {
 *     override fun onAdLoading() { showLoader() }
 *     override fun onAdLoaded() { hideLoader() }
 *     override fun onFailedToLoad(error: AdError?) { hideLoader() }
 *     override fun onAdClicked() { }
 * })
 * ```
 *
 * These map to the underlying Google Mobile Ads events plus a couple of library-specific
 * hooks ([onAdLoading] and [onAdShownFromCache]).
 */
interface AdsCallBack {

    /** Fired right before a load request is started (good place to show a shimmer/loader). */
    fun onAdLoading() {}

    /** Fired when the ad finished loading successfully. */
    fun onAdLoaded() {}

    /** Fired when the ad failed to load. [error] carries the reason (may be null). */
    fun onFailedToLoad(error: AdError?) {}

    /** Fired when an instantly-shown ad was served from the preload cache (no network wait). */
    fun onAdShownFromCache() {}

    /** Fired when the ad records an impression (it became visible to the user). */
    fun onAdImpression() {}

    /** Fired when the user clicks/taps the ad. */
    fun onAdClicked() {}

    /** Fired when the ad opens an overlay that covers the screen. */
    fun onAdOpened() {}

    /** Fired when the ad overlay is closed and control returns to the app. */
    fun onAdClosed() {}

    /**
     * Optional custom hook used by some flows to continue to the next action
     * (e.g. navigate) once the ad interaction is finished.
     */
    fun onNextAction() {}
}
