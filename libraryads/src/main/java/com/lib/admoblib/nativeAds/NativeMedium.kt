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
import com.lib.admoblib.databinding.SmellNativeLayoutBinding
import com.lib.admoblib.isNetworkConnected
import com.lib.admoblib.utiliz.Tools


class NativeMedium @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    lateinit var binding: SmellNativeLayoutBinding
    private lateinit var nativetemplate: TemplateView
    private lateinit var NativeShimmer: ShimmerFrameLayout
    private lateinit var Laynative: RelativeLayout

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

    fun loadNativeMedium(
        activity: Activity, admobNativeIds: String, status: Boolean
    ) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    Tools.hideNavigationBar(activity)
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
                            }

                            override fun onAdLoaded() {
                                Laynative.visibility = View.VISIBLE
                                NativeShimmer.visibility = View.GONE
                                nativetemplate.visibility = View.VISIBLE
                                super.onAdLoaded()
                            }
                        }).build()

                    adLoader.loadAd(AdRequest.Builder().build())
                }

                else -> {
                    Tools.showNavigationBar(activity)
                    Laynative.visibility = View.GONE
                }
            }
        } else {
            Laynative.visibility = View.GONE
        }
    }

}
