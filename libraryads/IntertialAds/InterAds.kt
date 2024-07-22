package com.lib.admoblib.IntertialAds

import android.content.Context
import android.content.Intent
import android.util.Log
import com.lib.admoblib.isNetworkConnected


object InterAds {
    fun startLoadAdActivity(
        context: Context,
        nextClassName: String?,
        intertialAdsIds: String?,
        value: String?,
        KeyTwo: Int,
        status: Boolean
    ) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    ConstantAds.currentTime = System.currentTimeMillis()
                    if (ConstantAds.currentTime - ConstantAds.lastClickTime > 8000) {
                        ConstantAds.lastClickTime = ConstantAds.currentTime
                        val mIntent = Intent(context, LoadAdsActivity::class.java).apply {
                            putExtra(ConstantAds.nextClassName, nextClassName)
                            putExtra(ConstantAds.interstitialid, intertialAdsIds)
                            putExtra(ConstantAds.KeyTwo, KeyTwo)
                            putExtra(ConstantAds.KeyOne, value)
                        }
                        context.startActivity(mIntent)
                        Log.d("checkSide", "startLoadAdActivity: ads phase")
                    } else {
                        try {
                            val mIntent = Intent(context, Class.forName(nextClassName)).apply {
                                putExtra(ConstantAds.KeyTwo, KeyTwo)
                                putExtra(ConstantAds.KeyOne, value)
                            }
                            context.startActivity(mIntent)
                            Log.d("checkSide", "startLoadAdActivity: ads phasing")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                else -> {
                    try {
                        val mIntent = Intent(context, Class.forName(nextClassName)).apply {
                            putExtra(ConstantAds.KeyTwo, KeyTwo)
                            putExtra(ConstantAds.KeyOne, value)
                        }
                        context.startActivity(mIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        } else {
            try {
                val mIntent = Intent(context, Class.forName(nextClassName))
                mIntent.putExtra(ConstantAds.KeyTwo, KeyTwo)
                mIntent.putExtra(ConstantAds.KeyOne, value)
                context.startActivity(mIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
