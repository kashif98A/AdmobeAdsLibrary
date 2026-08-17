package com.kashifali.admobadslibrary.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.kashifali.admobadslibrary.R
import com.kashifali.admobadslibrary.databinding.ActivityFullNativeAdBinding
import com.lib.admoblib.AdsCallBack

/**
 * A full-screen native ad screen. It fills the whole screen with a native ad
 * loaded through the ads library's [com.lib.admoblib.nativeAds.NativeFullScreen] view.
 */
class FullNativeAdActivity : AppCompatActivity() {

    private val TAG = "FullNativeAdActivity"
    lateinit var binding: ActivityFullNativeAdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullNativeAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Close the screen when the user taps the close (X) button.
        binding.nativeFullScreen.setOnCloseClickListener { finish() }

        // Full example of the ad lifecycle callbacks. Override only the ones you need.
        binding.nativeFullScreen.nativeAdsCallback(object : AdsCallBack {
            override fun onAdLoading() {
                Log.d(TAG, "onAdLoading: requesting a fresh ad")
            }

            override fun onAdShownFromCache() {
                Log.d(TAG, "onAdShownFromCache: served instantly from preload pool")
            }

            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ad is on screen")
            }

            override fun onFailedToLoad(error: AdError?) {
                Log.d(TAG, "onFailedToLoad: ${error?.message}")
                // Don't leave the user on a blank screen.
                finish()
            }

            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
            }

            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed")
            }
        })

        // Show the full-screen native ad instantly from the preload pool
        // (falls back to a fresh load if nothing was preloaded yet).
        binding.nativeFullScreen.showNativeAd(
            this, getString(R.string.NativeMain), true
        )
    }

    override fun onResume() {
        super.onResume()
        binding.nativeFullScreen.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.nativeFullScreen.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.nativeFullScreen.onDestroy()
    }
}
