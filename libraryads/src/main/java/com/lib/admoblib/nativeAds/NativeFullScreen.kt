package com.lib.admoblib.nativeAds

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.lib.admoblib.AdsCallBack
import com.lib.admoblib.databinding.FullNativeLayoutBinding
import com.lib.admoblib.isNetworkConnected

/**
 * A full–screen native ad view. Drop it into any layout with
 * width/height = match_parent (typically as the only child of an Activity)
 * and call [loadNativeFullScreen] to fill the whole screen with a native ad.
 *
 * A close button is provided out of the box – use [setOnCloseClickListener]
 * to react to it (e.g. finish the hosting Activity).
 */
class NativeFullScreen @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var binding: FullNativeLayoutBinding
    private lateinit var nativetemplate: TemplateView
    private lateinit var NativeShimmer: ShimmerFrameLayout
    private lateinit var Laynative: RelativeLayout
    private lateinit var closeButton: ImageView
    var adscallback: AdsCallBack? = null

    init {
        initAdmob()
    }

    private fun initAdmob() {
        val inflater = LayoutInflater.from(context)
        binding = FullNativeLayoutBinding.inflate(inflater, this, true)
        nativetemplate = binding.myTemplate
        NativeShimmer = binding.footer.shimmerContainerNative
        Laynative = binding.Laynative
        closeButton = binding.btnClose
    }

    fun loadNativeFullScreen(
        activity: Context, admobNativeIds: String, status: Boolean
    ) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    val adLoader =
                        AdLoader.Builder(activity, admobNativeIds).forNativeAd { nativeAd ->
                            val styles = NativeTemplateStyle.Builder().build()
                            nativetemplate.setStyles(styles)
                            nativetemplate.setNativeAd(nativeAd)
                            NativeShimmer.visibility = View.GONE
                            NativeShimmer.stopShimmer()
                        }.withAdListener(object : AdListener() {
                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                Laynative.visibility = View.GONE
                                NativeShimmer.visibility = View.GONE
                                NativeShimmer.stopShimmer()
                                super.onAdFailedToLoad(loadAdError)
                                adscallback?.onFailedToLoad(loadAdError)
                            }

                            override fun onAdLoaded() {
                                Laynative.visibility = View.VISIBLE
                                NativeShimmer.visibility = View.GONE
                                nativetemplate.visibility = View.VISIBLE
                                super.onAdLoaded()
                                adscallback?.onAdLoaded()
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                                adscallback?.onAdImpression()
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                adscallback?.onAdClicked()
                            }

                            override fun onAdOpened() {
                                super.onAdOpened()
                                adscallback?.onAdOpened()
                            }

                            override fun onAdClosed() {
                                super.onAdClosed()
                                adscallback?.onAdClosed()
                            }
                        }).build()

                    adscallback?.onAdLoading()
                    adLoader.loadAd(AdRequest.Builder().build())
                }

                else -> {
                    Laynative.visibility = View.GONE
                }
            }
        } else {
            Laynative.visibility = View.GONE
        }
    }

    /**
     * Show the full-screen native ad instantly from the preload pool ([NativeAdPreloader]).
     * If a preloaded ad is ready it is bound immediately (no shimmer wait); otherwise
     * it falls back to a normal fresh load. Either way a fresh ad is preloaded for the
     * next time so it stays "on-demand fast".
     */
    fun showNativeAd(
        activity: Context, admobNativeIds: String, status: Boolean
    ) {
        if (!status || !context.isNetworkConnected()) {
            Laynative.visibility = View.GONE
            return
        }
        val preloaded = NativeAdPreloader.poll(admobNativeIds)
        if (preloaded != null) {
            val styles = NativeTemplateStyle.Builder().build()
            nativetemplate.setStyles(styles)
            nativetemplate.setNativeAd(preloaded)
            NativeShimmer.visibility = View.GONE
            NativeShimmer.stopShimmer()
            nativetemplate.visibility = View.VISIBLE
            Laynative.visibility = View.VISIBLE
            adscallback?.onAdShownFromCache()
            adscallback?.onAdLoaded()
        } else {
            loadNativeFullScreen(activity, admobNativeIds, status)
        }
        // Keep the pool warm for the next time.
        NativeAdPreloader.preload(activity, admobNativeIds, true)
    }

    fun nativeAdsCallback(callback: AdsCallBack?) {
        adscallback = callback
    }

    /** React to the built-in close button (e.g. finish the hosting Activity). */
    fun setOnCloseClickListener(listener: OnClickListener?) {
        closeButton.setOnClickListener(listener)
    }

    /** Show or hide the built-in close button. */
    fun setCloseButtonVisible(visible: Boolean) {
        closeButton.visibility = if (visible) View.VISIBLE else View.GONE
    }

    // Lifecycle management for the native ad view
    fun onResume() {
        NativeShimmer.startShimmer()
    }

    fun onPause() {
        NativeShimmer.stopShimmer()
    }

    fun onDestroy() {
        NativeShimmer.stopShimmer()
        nativetemplate.destroyNativeAd()
    }
}
