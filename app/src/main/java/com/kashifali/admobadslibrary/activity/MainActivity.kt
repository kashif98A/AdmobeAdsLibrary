package com.kashifali.admobadslibrary.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdError
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kashifali.admobadslibrary.R
import com.kashifali.admobadslibrary.databinding.ActivityMainBinding
import com.lib.admoblib.AdsCallBack
import com.lib.admoblib.appOpen.AppOpenControl
import com.lib.admoblib.nativeAds.NativeLarge
import com.lib.admoblib.showBottomSheetDialog


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var bottomSheetDialog:BottomSheetDialog
    var yesexit: CardView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        AppOpenControl.setAppOpenNotShow()
        binding.adaptiveBanner.loadAdaptiveBanner(this,getString(R.string.BannerGender),true)
       binding.nativeLarge.loadNativeLarge(this,getString(R.string.NativeMain),true)
        binding.nativeMedium.loadNativeMedium(this,getString(R.string.NativeMain),true)

        binding.btnInter.setOnClickListener(View.OnClickListener {
//            InterAds.startLoadAdActivity(this,
//               SecondActivity::class.java.canonicalName, getString(R.string.InterstitialSplash),
//              "some_value",
//              123,false
//            )
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
//            showInterstitial(this) {
//                startActivity(Intent(this@MainActivity, SecondActivity::class.java))
//
//            }
        })

//        binding.nativeMedium.nativeAdsCallback(object : AdsCallBack{
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//                Log.d("checkAdsLoads", " native onAdLoaded: ")
//            }
//        })
//        binding.adaptiveBanner.bannerAdsCallback(object : AdsCallBack{
//            override fun onAdLoaded() {
//                Log.d("checkAdsLoads", "onAdLoaded: ")
//            }
//
////            override fun onFailedToLoad(error: com.google.android.gms.ads.AdError?) {
////                super.onFailedToLoad(error)
////            }
//        })
        ///for frammnet
//        binding.btnInter1.setOnClickListener(View.OnClickListener {
//            InterAdsFragment.startLoadAdFagment(
//                requireContext(),
//                R.id.bankrecylist,
//                bundle,
//                BuildConfig.interstitialBankSwift,
//                navController,
//                true
//            )
//        })
        /// call on main Activity
//        LoadAdsFragment.mutableLiveData = MutableLiveData()
//        LoadAdsFragment.mutableLiveData!!.observe(this) { myData ->
//            if (myData != null) {
//                val nextClassName = myData.nextClassName
//                val strValue = myData.mBundel
//                navController.navigate(nextClassName, strValue)
//            }
//        }
        /**
         * for bottom sheet dialoge
         */
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(com.lib.admoblib.R.layout.bottom_sheet_dialog)
        val nativeAdmob = bottomSheetDialog.findViewById<NativeLarge>(R.id.nativeLarge)
        nativeAdmob!!.loadNativeLarge(this@MainActivity,getString(R.string.NativeMain),true)

        // Set callback to navigate to SecondActivity once ad is loaded
        binding.nativeMediumpre.nativeAdsCallback(object : AdsCallBack {
            override fun onAdLoaded() {
//                startActivity(Intent(this@MainActivity, SecondActivity::class.java))
            }

            override fun onFailedToLoad(error: AdError?) {
                // Handle ad load failure
            }
        })

        // Preload the ad
        binding.nativeMediumpre.preloadAd(this, getString(R.string.NativeMain),true)

    }


    override fun onBackPressed() {
//        super.onBackPressed()
        showBottomSheetDialog(this,bottomSheetDialog)
    }
}