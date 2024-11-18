package com.lib.admoblib.utiliz

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd

object AdsConstant {
    const val TAG = "AD_SDK"
    var splashAppOpenAd:AppOpenAd? = null
    var mainAppOpenAd:AppOpenAd? = null
    var mInterstitialAd:InterstitialAd? = null
    var className = ""
    var isShowPermissionDialog = false

    var isSplashOpenAppLoadingAd = false
    var isSplashOpenShowingAd = false
    var isMainOpenAppLoadingAd = false
    var isMainOpenShowingAd = false
    var isInterstitialAdShowing = false


    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    }
}