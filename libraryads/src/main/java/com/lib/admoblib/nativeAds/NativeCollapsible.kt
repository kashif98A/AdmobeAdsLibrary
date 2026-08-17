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
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.lib.admoblib.AdsCallBack
import com.lib.admoblib.databinding.CollapsibleNativeLayoutBinding
import com.lib.admoblib.isNetworkConnected

/**
 * A custom, collapsible native ad card:
 *  - Expanded: a chevron + a large media area on top, then icon + headline + body +
 *    "Ad" badge, and a full-width gradient "Install" call-to-action at the bottom.
 *  - Tapping the chevron collapses the media area and hides the chevron, leaving the
 *    compact icon + text + Install card.
 *
 * Usage:
 *   binding.nativeCollapsible.loadNativeCollapsible(this, getString(R.string.NativeMain), true)
 *   // or instant, from the preload pool:
 *   binding.nativeCollapsible.showNativeAd(this, getString(R.string.NativeMain), true)
 */
class NativeCollapsible @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var binding: CollapsibleNativeLayoutBinding
    private lateinit var nativeAdView: NativeAdView
    private lateinit var NativeShimmer: ShimmerFrameLayout
    private lateinit var Laynative: RelativeLayout
    private var nativeAd: NativeAd? = null
    private var mediaExpanded = true
    var adscallback: AdsCallBack? = null

    init {
        initAdmob()
    }

    private fun initAdmob() {
        val inflater = LayoutInflater.from(context)
        binding = CollapsibleNativeLayoutBinding.inflate(inflater, this, true)
        nativeAdView = binding.nativeAdView
        NativeShimmer = binding.footer.shimmerContainerNative
        Laynative = binding.Laynative

        // Chevron toggles the media area (expand / collapse with animation).
        binding.btnCollapse.setOnClickListener { toggleMedia() }
    }

    fun loadNativeCollapsible(
        activity: Context, admobNativeIds: String, status: Boolean
    ) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    // Keep AdChoices out of the chevron's corner.
                    val adOptions = NativeAdOptions.Builder()
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
                        .build()
                    val adLoader = AdLoader.Builder(activity, admobNativeIds)
                        .forNativeAd { ad -> bindNativeAd(ad) }
                        .withNativeAdOptions(adOptions)
                        .withAdListener(object : AdListener() {
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
                                NativeShimmer.stopShimmer()
                                nativeAdView.visibility = View.VISIBLE
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
                        })
                        .build()

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
     * Falls back to a fresh load if nothing is preloaded, and warms the pool for next time.
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
            bindNativeAd(preloaded)
            NativeShimmer.visibility = View.GONE
            NativeShimmer.stopShimmer()
            nativeAdView.visibility = View.VISIBLE
            Laynative.visibility = View.VISIBLE
            adscallback?.onAdShownFromCache()
            adscallback?.onAdLoaded()
        } else {
            loadNativeCollapsible(activity, admobNativeIds, status)
        }
        NativeAdPreloader.preload(activity, admobNativeIds, true)
    }

    private fun bindNativeAd(ad: NativeAd) {
        // Release any previously bound ad.
        nativeAd?.takeIf { it !== ad }?.destroy()
        nativeAd = ad

        nativeAdView.mediaView = binding.mediaView
        nativeAdView.headlineView = binding.headline
        nativeAdView.bodyView = binding.body
        nativeAdView.iconView = binding.icon
        nativeAdView.callToActionView = binding.cta

        binding.headline.text = ad.headline
        val body = ad.body
        if (body.isNullOrEmpty()) {
            binding.body.visibility = View.GONE
        } else {
            binding.body.visibility = View.VISIBLE
            binding.body.text = body
        }
        ad.callToAction?.let { binding.cta.text = it }

        val icon = ad.icon
        if (icon != null) {
            binding.icon.setImageDrawable(icon.drawable)
            binding.icon.visibility = View.VISIBLE
        } else {
            binding.icon.visibility = View.GONE
        }

        nativeAdView.setNativeAd(ad)
    }

    private fun toggleMedia() {
        // Collapse hides the media area AND the chevron, leaving the compact
        // icon + text + Install card (matches the anchored/collapsed look).
        mediaExpanded = !mediaExpanded
        binding.mediaCard.visibility = if (mediaExpanded) View.VISIBLE else View.GONE
        binding.btnCollapse.visibility = if (mediaExpanded) View.VISIBLE else View.GONE
    }

    fun nativeAdsCallback(callback: AdsCallBack?) {
        adscallback = callback
    }

    // Lifecycle
    fun onResume() {
        NativeShimmer.startShimmer()
    }

    fun onPause() {
        NativeShimmer.stopShimmer()
    }

    fun onDestroy() {
        NativeShimmer.stopShimmer()
        nativeAd?.destroy()
        nativeAd = null
    }
}
