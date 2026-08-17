
package com.lib.admoblib.bannerAds
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.lib.admoblib.AdsCallBack
import com.lib.admoblib.databinding.AdmobBannerLayoutBinding
import com.lib.admoblib.isNetworkConnected
import com.lib.admoblib.utiliz.Tools


class AdaptiveBanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    lateinit var binding: AdmobBannerLayoutBinding
    private var adContainerView: FrameLayout? = null
    private var footer: ShimmerFrameLayout? = null
    private var laybanner: RelativeLayout? = null
    var adscallback: AdsCallBack? = null

    private var adView: AdView? = null
    init {
        initAdmob()
    }
    private fun initAdmob() {
        val inflater = LayoutInflater.from(context)
        binding = AdmobBannerLayoutBinding.inflate(inflater, this, true)
        adContainerView=binding.adContainerView
        footer=binding.footer.shimmerContainerBanner
        laybanner=binding.laybanner
    }
    fun loadAdaptiveBanner(activity: Activity, bannerId: String, status: Boolean) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    Tools.hideNavigationBar(activity)
                 adView = AdView(activity)
                adView?.adUnitId = bannerId
                adContainerView?.removeAllViews()
                adContainerView?.addView(adView)
                val adSize: AdSize = getAdSize(activity, adContainerView!!)
                adView?.setAdSize(adSize)
                val adRequest: AdRequest = AdRequest.Builder().build()
                adView?.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        footer?.visibility = View.GONE
                        footer?.stopShimmer()
                        adContainerView?.visibility = View.VISIBLE
                        adscallback?.onAdLoaded()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        footer?.visibility = View.GONE
                        adContainerView?.visibility = View.GONE
                        adscallback?.onFailedToLoad(loadAdError)
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
                }
                adscallback?.onAdLoading()
                adView?.loadAd(adRequest)
            }     else -> {
                laybanner?.visibility = View.GONE
                Tools.showNavigationBar(activity)
            }
        }
        }else {
            laybanner?.visibility = View.GONE
        }
    }

    private fun getAdSize(activity: Activity, adContainerView: FrameLayout): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = adContainerView.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }


    /**
     * Show an adaptive banner instantly from the preload pool ([BannerAdPreloader]).
     * If a preloaded banner is ready it is attached immediately (no shimmer wait);
     * otherwise it falls back to a normal fresh load. Either way a fresh banner is
     * preloaded for the next screen so it stays "on-demand fast".
     */
    fun showBanner(activity: Activity, bannerId: String, status: Boolean) {
        if (!status || !context.isNetworkConnected()) {
            laybanner?.visibility = View.GONE
            Tools.showNavigationBar(activity)
            return
        }
        val preloaded = BannerAdPreloader.poll(bannerId)
        if (preloaded != null) {
            Tools.hideNavigationBar(activity)
            adView = preloaded
            adContainerView?.removeAllViews()
            adContainerView?.addView(preloaded)
            footer?.visibility = View.GONE
            footer?.stopShimmer()
            adContainerView?.visibility = View.VISIBLE
            laybanner?.visibility = View.VISIBLE
            adscallback?.onAdShownFromCache()
            adscallback?.onAdLoaded()
        } else {
            loadAdaptiveBanner(activity, bannerId, status)
        }
        // Keep the pool warm for the next screen.
        BannerAdPreloader.preload(activity, bannerId)
    }

    fun  bannerAdsCallback(callback: AdsCallBack?) {
        adscallback = callback
    }

    // Add the lifecycle methods
    fun resumeAdView() {
        adView?.resume()
    }

    fun pauseAdView() {
        adView?.pause()
    }

    fun destroyAdView() {
        adView?.destroy()
        adView = null  // Set to null to release reference and prevent memory leaks
    }

}
