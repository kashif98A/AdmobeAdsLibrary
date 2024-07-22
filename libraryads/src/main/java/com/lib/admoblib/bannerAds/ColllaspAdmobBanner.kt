package com.lib.admoblib.bannerAds

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.lib.admoblib.databinding.AdmobBannerLayoutBinding
import com.lib.admoblib.isNetworkConnected
import com.lib.admoblib.utiliz.Tools


class ColllaspAdmobBanner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
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
        adContainerView = binding.adContainerView
        footer = binding.footer.shimmerContainerBanner
        laybanner = binding.laybanner
    }

    //
    fun loadBannerCollasibile(
        context: Activity,
        bannerId: String,
        status: Boolean,
    ) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    Tools.hideNavigationBar(context)
                    val adView = AdView(context)
                    adContainerView?.visibility = View.VISIBLE
                    adContainerView?.removeAllViews()
                    val extras = Bundle()
                    extras.putString("collapsible", "bottom")
                    val adRequest =
                        AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                            .build()
                    adView.adUnitId = bannerId
//            adView.loadAd(adRequest)

                    val adSize: AdSize = getAdSize(
                        context, adContainerView!!
                    )
                    adView.setAdSize(adSize)
                    adView.loadAd(adRequest)
                    adView.adListener = object : AdListener() {
                        override fun onAdImpression() {
                            super.onAdImpression()
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            //findViewById(R.id.include).setVisibility(View.INVISIBLE);
                            footer?.visibility = View.GONE
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            footer?.visibility = View.GONE
                        }
                    }

                    //adContainerView.addView(adView);
                    val adContainerParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    adContainerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    adContainerView?.addView(adView, adContainerParams)
                }

                else -> {
                    Tools.showNavigationBar(context)
                    laybanner?.visibility = View.GONE
                }
            }
        } else {
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
