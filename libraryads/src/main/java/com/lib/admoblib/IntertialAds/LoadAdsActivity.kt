package com.lib.admoblib.IntertialAds

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lib.admoblib.R

class LoadAdsActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private var nextClassNameInString: String? = null
    private var interstitialId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_prepair_loading_ads)

        nextClassNameInString = intent.getStringExtra(ConstantAds.nextClassName)
        interstitialId = intent.getStringExtra(ConstantAds.interstitialid)

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, interstitialId!!, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                Log.i(TAG, "onAdLoaded")
                showInterstitialAd()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d(TAG, loadAdError.toString())
                mInterstitialAd = null
                try {
                    nextActivity(nextClassNameInString!!)
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
                finish()
            }
        })
    }

    private fun showInterstitialAd() {
        mInterstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                    try {
                        nextActivity(nextClassNameInString!!)
                        finish()
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                    overridePendingTransition(0, 0)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                    try {
                        nextActivity(nextClassNameInString!!)
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                    finish()
                }

                override fun onAdImpression() {
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }
            ad.show(this@LoadAdsActivity)
        } ?: run {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    @Throws(ClassNotFoundException::class)
    private fun nextActivity(nextActivity: String) {
        val mIntent = Intent(this@LoadAdsActivity, Class.forName(nextActivity))
        mIntent.putExtra(ConstantAds.position, intent.getIntExtra(ConstantAds.position, 0))
        mIntent.putExtra(ConstantAds.KeyOne, intent.getStringExtra(ConstantAds.KeyOne))
        mIntent.putExtra(ConstantAds.KeyTwo, intent.getStringExtra(ConstantAds.KeyTwo))
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mIntent)
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
        const val TAG = "LoadAdsActivity"
    }
}
