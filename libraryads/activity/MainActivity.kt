package com.lib.admoblib.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lib.admobeadslib.R
import com.lib.admobeadslib.databinding.ActivityMainBinding
import com.lib.admoblib.IntertialAds.InterAds
import com.lib.admoblib.nativeAds.NativeLargeAdmob
import com.lib.admoblib.showBottomSheetDialog


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var bottomSheetDialog:BottomSheetDialog
    var yesexit: CardView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.admobBanner.loadAdaptiveBanner(this,getString(R.string.BannerGender),true)
        binding.admobcollaspBanner.loadBannerCollasibile(this,getString(R.string.BannerGender),false)
        binding.nativeAdmob.loadNativeAD(this,getString(R.string.NativeMain),false)
        binding.nativeSmallAdmob.loadNativeAD(this,getString(R.string.NativeMain),true)

        binding.btnInter.setOnClickListener(View.OnClickListener {
            InterAds.startLoadAdActivity(this,
               SecondActivity::class.java.canonicalName, getString(R.string.InterstitialSplash),
              "some_value",
              123,false
            )
        })


        /**
         * for bottom sheet dialoge
         */
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val nativeAdmob = bottomSheetDialog.findViewById<NativeLargeAdmob>(R.id.nativeAdmob)
        nativeAdmob!!.loadNativeAD(this@MainActivity,getString(R.string.NativeMain),false)

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        showBottomSheetDialog(this,bottomSheetDialog)
    }
}