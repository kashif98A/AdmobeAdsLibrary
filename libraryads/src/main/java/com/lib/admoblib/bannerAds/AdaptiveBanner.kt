
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
                val adView = AdView(activity)
                adView.adUnitId = bannerId
                adContainerView?.removeAllViews()
                adContainerView?.addView(adView)
                val adSize: AdSize = getAdSize(activity, adContainerView!!)
                adView.setAdSize(adSize)
                val adRequest: AdRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                adView.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        footer?.visibility = View.GONE
                        footer?.stopShimmer()
                        adContainerView?.visibility = View.VISIBLE
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        footer?.visibility = View.GONE
                        adContainerView?.visibility = View.GONE
                    }
                }
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


}
