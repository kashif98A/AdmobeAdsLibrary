package com.lib.admoblib.nativeAds

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.lib.admoblib.R


class AdmobNativeAdAdapter(private val param: Param) : RecyclerViewAdapterWrapper(param.adapter) {

    companion object {
        private const val TYPE_FB_NATIVE_ADS = 900
        private const val DEFAULT_AD_ITEM_INTERVAL = 4

        fun isValidPhoneNumber(target: CharSequence): Boolean {
            return target.length == 10 && android.util.Patterns.PHONE.matcher(target).matches()
        }
    }

//    private val context: Context = param.adapter.context

    init {
        assertConfig()
        setSpanAds()
    }

    private fun assertConfig() {
        param.gridLayoutManager?.let {
            val nCol = it.spanCount
            if (param.adItemInterval % nCol != 0) {
                throw IllegalArgumentException("The adItemInterval (${param.adItemInterval}) is not divisible by number of columns in GridLayoutManager ($nCol)")
            }
        }
    }

    private fun convertAdPosition2OrgPosition(position: Int): Int {
        return position - (position + 1) / (param.adItemInterval + 1)
    }

    override fun getItemCount(): Int {
        val realCount = super.getItemCount()
        return realCount + realCount / param.adItemInterval
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) {
            TYPE_FB_NATIVE_ADS
        } else {
            super.getItemViewType(convertAdPosition2OrgPosition(position))
        }
    }

    private fun isAdPosition(position: Int): Boolean {
        return (position + 1) % (param.adItemInterval + 1) == 0
    }

    private fun onBindAdViewHolder(holder: RecyclerView.ViewHolder) {
        val adHolder = holder as AdViewHolder
        Handler().postDelayed({
            adHolder.nativeShimmer.visibility = View.GONE
        }, 3000)

        if (param.forceReloadAdOnBind || !adHolder.loaded) {
            val adLoader = AdLoader.Builder(adHolder.context, param.admobNativeId)
                .forNativeAd { nativeAd ->
                    val builder = NativeTemplateStyle.Builder()
                        .withPrimaryTextSize(11f)
                        .withSecondaryTextSize(10f)
                        .withTertiaryTextSize(6f)
                        .withCallToActionTextSize(11f)

                    when (param.layout) {
                        0 -> {
                            adHolder.templateSmall.visibility = View.VISIBLE
                            adHolder.templateSmall.setStyles(builder.build())
                            adHolder.templateSmall.setNativeAd(nativeAd)
                            adHolder.nativeShimmer.visibility = View.GONE
                        }
                        // Add more cases if needed
                    }

                    adHolder.loaded = true
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: LoadAdError) {
                        Log.e("admobnative", "error: $errorCode")
                        adHolder.adContainer.visibility = View.GONE
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder().build())
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindAdViewHolder(holder)
        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position))
        }
    }

    private fun onCreateAdViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val adLayoutOutline = inflater.inflate(param.itemContainerLayoutRes, parent, false)
        val vg = adLayoutOutline.findViewById<ViewGroup>(param.itemContainerId)

        val adLayoutContent = inflater.inflate(R.layout.item_admob_native_ad, parent, false) as LinearLayout
        vg.addView(adLayoutContent)
        return AdViewHolder(adLayoutOutline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FB_NATIVE_ADS) {
            onCreateAdViewHolder(parent)
        } else {
            super.onCreateViewHolder(parent, viewType)
        }
    }

    private fun setSpanAds() {
        param.gridLayoutManager?.let {
            val spl = it.spanSizeLookup
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isAdPosition(position)) {
                        spl.getSpanSize(position)
                    } else {
                        1
                    }
                }
            }
        }
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo
                ni?.isConnected == true && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
            } else {
                val n = cm.activeNetwork
                val nc = cm.getNetworkCapabilities(n)
                nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true || nc?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true || nc?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true || nc?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true || nc?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true
            }
        } else {
            false
        }
    }

    class Param {
        lateinit var admobNativeId: String
        lateinit var adapter: RecyclerView.Adapter<*>
        var adItemInterval: Int = DEFAULT_AD_ITEM_INTERVAL
        var forceReloadAdOnBind: Boolean = true
        var layout: Int = 0

        @LayoutRes
        var itemContainerLayoutRes: Int = R.layout.item_admob_native_ad_outline

        @IdRes
        var itemContainerId: Int = R.id.ad_container

        var gridLayoutManager: GridLayoutManager? = null
    }

    class Builder private constructor(private val param: Param) {
        companion object {
            fun with(placementId: String, wrapped: RecyclerView.Adapter<*>, layout: String): Builder {
                val param = Param()
                param.admobNativeId = placementId
                param.adapter = wrapped
                param.layout = when (layout.toLowerCase()) {
                    "small" -> 0
                    "medium" -> 1
                    else -> 2
                }
                return Builder(param)
            }
        }

        fun adItemInterval(interval: Int) = apply { param.adItemInterval = interval }

        fun adLayout(@LayoutRes layoutContainerRes: Int, @IdRes itemContainerId: Int) = apply {
            param.itemContainerLayoutRes = layoutContainerRes
            param.itemContainerId = itemContainerId
        }

        fun build(): AdmobNativeAdAdapter = AdmobNativeAdAdapter(param)

        fun enableSpanRow(layoutManager: GridLayoutManager) = apply {
            param.gridLayoutManager = layoutManager
        }


        fun forceReloadAdOnBind(forced: Boolean) = apply {
            param.forceReloadAdOnBind = forced
        }
    }

    private class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val templateSmall: TemplateView = view.findViewById(R.id.my_template)
        val nativeShimmer: ShimmerFrameLayout = view.findViewById(R.id.load_native)
        val layNative: RelativeLayout = view.findViewById(R.id.Laynative)
        var loaded = false
        val adContainer: LinearLayout = view.findViewById(R.id.native_ad_container)

        val context: Context
            get() = adContainer.context
    }
}
