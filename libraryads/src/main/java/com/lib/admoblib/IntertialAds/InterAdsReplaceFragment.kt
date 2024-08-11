package com.lib.admoblib.IntertialAds

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.lib.admoblib.isNetworkConnected


object InterAdsReplaceFragment {
    @SuppressLint("SuspiciousIndentation")
    fun startLoadAdFagment(
        context: Context,
        actionid: String,
        mBundle: Bundle,
        idAds: String,
        status: Boolean
    ) {
        if (context.isNetworkConnected()) {
            when {
                status -> {
                    ConstantAds.currentTime = System.currentTimeMillis()
                    if (ConstantAds.currentTime - ConstantAds.lastClickTime > 8000) {
                        ConstantAds.lastClickTime = ConstantAds.currentTime
                        ConstantAds.interstitialid = idAds
                        val mIntent = Intent(context, LoadAdsReplaceFragment::class.java)
                        mIntent.putExtra("actionid", actionid)
                        mIntent.putExtra("mBundle", mBundle)
                        context.startActivity(mIntent)
                        Log.d("checkSide", "startLoadAdActivity: ads phase")
                    } else {
                        try {
                            nextActivity(actionid!!,0, mBundle)
                            Log.d("checkSide", "startLoadAdActivity: ads phasing")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                else -> {
                    try {
                        nextActivity(actionid,0, mBundle)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        } else {
            try {
                nextActivity(actionid,0, mBundle)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun nextActivity(nextClassNameInString: String, intValue: Int, strValue: Bundle?) {
        val myData = MyData2(nextClassNameInString, intValue, strValue!!)
        LoadAdsReplaceFragment.mutableLiveData!!.postValue(myData)

    }
}
