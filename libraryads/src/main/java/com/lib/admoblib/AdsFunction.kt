package com.lib.admoblib

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lib.admoblib.appOpen.AppOpenAd
import kotlin.system.exitProcess


fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager.activeNetwork?.let { network ->
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
            } ?: false
        }
    } else {
        connectivityManager.activeNetworkInfo?.let { networkInfo ->
            return networkInfo.isConnected
        }
    }
    return false
}

 fun showBottomSheetDialog(context: Activity,bottomSheetDialog: BottomSheetDialog) {

    val ExitBtn = bottomSheetDialog.findViewById<CardView>(R.id.exitCard)
    ExitBtn?.setOnClickListener {
        context.finishAffinity()
        exitProcess(0)
        bottomSheetDialog.dismiss()
    }
    bottomSheetDialog!!.show()
}



