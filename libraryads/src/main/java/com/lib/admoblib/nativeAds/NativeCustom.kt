package com.lib.admoblib.nativeAds

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.lib.admoblib.AdsCallBack
import com.lib.admoblib.databinding.CustomNativeLayoutBinding
import com.lib.admoblib.isNetworkConnected

/**
 * A compact, media-on-the-left native ad card (headline + body on the right and a
 * full-width "Learn more" style call-to-action underneath).
 *
 * Usage:
 *   <com.lib.admoblib.nativeAds.NativeCustom
 *       android:id="@+id/nativeCustom"
 *       android:layout_width="match_parent"
 *       android:layout_height="wrap_content" />
 *
 *   binding.nativeCustom.loadNativeCustom(this, getString(R.string.NativeMain), true)
 *   // or, for an instant preloaded ad:
 *   binding.nativeCustom.showNativeAd(this, getString(R.string.NativeMain), true)
 */
class NativeCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var binding: CustomNativeLayoutBinding
    private lateinit var nativetemplate: TemplateView
    private lateinit var NativeShimmer: ShimmerFrameLayout
    private lateinit var Laynative: RelativeLayout
    var adscallback: AdsCallBack? = null

    init {
        initAdmob()
    }

    private fun initAdmob() {
        val inflater = LayoutInflater.from(context)
        binding = CustomNativeLayoutBinding.inflate(inflater, this, true)
        nativetemplate = binding.myTemplate
        NativeShimmer = binding.footer.shimmerContainerNative
        Laynative = binding.Laynative
    }

    fun loadNativeCustom(
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
     * Show a native ad instantly from the preload pool ([NativeAdPreloader]).
     * If a preloaded ad is ready it is bound immediately (no shimmer wait); otherwise
     * it falls back to a normal fresh load. Either way a fresh ad is preloaded for the
     * next screen so it stays "on-demand fast".
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
            loadNativeCustom(activity, admobNativeIds, status)
        }
        // Keep the pool warm for the next screen.
        NativeAdPreloader.preload(activity, admobNativeIds, true)
    }

    fun nativeAdsCallback(callback: AdsCallBack?) {
        adscallback = callback
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
