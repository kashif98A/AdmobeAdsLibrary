package com.kashifali.admobadslibrary.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kashifali.admobadslibrary.R
import com.kashifali.admobadslibrary.databinding.ActivityMainBinding
import com.kashifali.admobadslibrary.databinding.ActivitySecondBinding

import com.lib.admoblib.IntertialAds.InterAds

class SecondActivity : AppCompatActivity() {
    val TAG="SecondActivity"
    lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Instant: attach the collapsible banner preloaded on the previous screen.
        binding.collapsibleBanner.showBanner(this,getString(R.string.BannerGender),true)
        // Instant: show the native ad preloaded on the previous screen.
        binding.nativeMediumpre.showNativeAd(this,getString(R.string.NativeMain),true)
        // Custom native, centered on this screen.
        binding.nativeCustom.loadNativeCustom(this,getString(R.string.NativeMain),true)

    }


    override fun onResume() {
        super.onResume()
        binding.collapsibleBanner.resumeAdView()
        Log.d("CheAdview", "onResume: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CheAdview", "onDestroy: ")
        binding.collapsibleBanner.destroyAdView()
       binding.nativeMediumpre.destroyNative()
        binding.nativeCustom.onDestroy()

    }
}