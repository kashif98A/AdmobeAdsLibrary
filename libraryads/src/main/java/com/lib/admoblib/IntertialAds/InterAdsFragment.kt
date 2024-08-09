package com.lib.admoblib.IntertialAds

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.lib.admoblib.isNetworkConnected


object InterAdsFragment {
    @SuppressLint("SuspiciousIndentation")
    fun startLoadAdFagment(
        context: Context,
        actionid: Int,
        mBundle: Bundle,
        idAds: String,
        navController: androidx.navigation.NavController,
        status: Boolean
    ) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    ConstantAds.currentTime = System.currentTimeMillis()
                    if (ConstantAds.currentTime - ConstantAds.lastClickTime > 8000) {
                        ConstantAds.lastClickTime = ConstantAds.currentTime
                        ConstantAds.interstitialid = idAds
                        val mIntent = Intent(context, LoadAdsFragment::class.java)
                        mIntent.putExtra("actionid", actionid)
                        mIntent.putExtra("mBundle", mBundle)
                        context.startActivity(mIntent)
                        Log.d("checkSide", "startLoadAdActivity: ads phase")
                    } else {
                        try {
                            navController.navigate(actionid, mBundle)
                            Log.d("checkSide", "startLoadAdActivity: ads phasing")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                else -> {
                    try {
                        navController.navigate(actionid, mBundle)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        } else {
            try {
                navController.navigate(actionid, mBundle)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
