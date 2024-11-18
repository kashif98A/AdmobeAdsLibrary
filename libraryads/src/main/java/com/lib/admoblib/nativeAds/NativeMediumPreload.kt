package com.lib.admoblib.nativeAds

import android.app.Activity
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
import com.lib.admoblib.databinding.SmellNativeLayoutBinding
import com.lib.admoblib.isNetworkConnected


class NativeMediumPreload @JvmOverloads constructor(
context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var binding: SmellNativeLayoutBinding
    private lateinit var nativetemplate: TemplateView
    private lateinit var NativeShimmer: ShimmerFrameLayout
    private lateinit var Laynative: RelativeLayout
    var adscallback: AdsCallBack? = null

    init {
        initAdmob()
    }

    private fun initAdmob() {
        val inflater = LayoutInflater.from(context)
        binding = SmellNativeLayoutBinding.inflate(inflater, this, true)
        nativetemplate = binding.myTemplate
        NativeShimmer = binding.footer.shimmerContainerNative
        Laynative = binding.Laynative
    }

    fun preloadAd(activity: Activity, admobNativeIds: String, status: Boolean) {
        if (context.isNetworkConnected()) {
            when(status){
                true->{
                    val adLoader = AdLoader.Builder(activity, admobNativeIds)
                        .forNativeAd { nativeAd ->
                            AdCache.setNativeAd(nativeAd)
                            adscallback?.onAdLoaded()
                        }
                        .withAdListener(object : AdListener() {
                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                super.onAdFailedToLoad(loadAdError)
                                adscallback?.onFailedToLoad(loadAdError)
                            }
                        })
                        .build()

                    adLoader.loadAd(AdRequest.Builder().build())
                }
                false->{

                    NativeShimmer.visibility = View.GONE
                    Laynative.visibility = View.GONE
                }
            }

        }
    }

    fun showCachedAd() {
        val cachedAd = AdCache.getCachedAd()
        if (cachedAd != null) {
            val styles = NativeTemplateStyle.Builder().build()
            nativetemplate.setStyles(styles)
            nativetemplate.setNativeAd(cachedAd)

            NativeShimmer.visibility = View.GONE
            NativeShimmer.stopShimmer()
            nativetemplate.visibility = View.VISIBLE
            Laynative.visibility = View.VISIBLE
            println("NativeMedium: Cached ad is now displayed.")
        } else {
            Laynative.visibility = View.GONE
            println("NativeMedium: No cached ad available to display.")
        }
    }

    fun nativeAdsCallback(callback: AdsCallBack?) {
        adscallback = callback
    }

    // Lifecycle management
    fun onResume() {
        NativeShimmer.startShimmer()
    }

    fun onPause() {
        NativeShimmer.stopShimmer()
    }

    fun onDestroy() {
        NativeShimmer.stopShimmer()
        AdCache.clear()  // Clear cached ad on destroy
    }
}

