package com.lib.admoblib.IntertialAds

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lib.admoblib.R

class LoadAdsFragment : AppCompatActivity() {
    var mInterstitialAd: InterstitialAd? = null
    var nextClassNameInString: String? = null
    var interstitialId: String? = null
    var strValue: String? = null
    var intValue = 0
    var actionId = 0
    var mBundle: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_prepair_loading_ads)

        actionId = intent.getIntExtra("actionid", 0)
        mBundle = intent.getBundleExtra("mBundle")
        interstitialId = ConstantAds.interstitialid

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            interstitialId!!,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {

                    mInterstitialAd = interstitialAd
                    Log.i(TAG, "onAdLoaded")
                    showInterstitialAd()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d(TAG, loadAdError.toString())
                    mInterstitialAd = null
                    nextActivity(actionId, intValue, mBundle)
                    finish()
                }
            })
    }

    fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                    nextActivity(actionId, intValue, mBundle)
                    finish()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                    nextActivity(actionId, intValue, mBundle)
                    //                    MainActivity.Companion.replaceFrag(actionId,mBundle);
                    finish()
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                    mInterstitialAd = null
                    nextActivity(actionId, intValue, mBundle)
                    finish()
                }
            }
            mInterstitialAd!!.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    private fun nextActivity(nextClassNameInString: Int, intValue: Int, strValue: Bundle?) {
        val myData = MyData(nextClassNameInString, intValue, strValue!!)
        mutableLiveData!!.postValue(myData)
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        val TAG = LoadAdsActivity::class.java.getName()
        var mutableLiveData: MutableLiveData<MyData>? = null
    }
}