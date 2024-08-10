package com.kashifali.admobadslibrary.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kashifali.admobadslibrary.R
import com.kashifali.admobadslibrary.databinding.ActivityMainBinding
import com.kashifali.admobadslibrary.databinding.ActivitySecondBinding

import com.lib.admoblib.IntertialAds.InterAds

class SecondActivity : AppCompatActivity() {
    lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.collapsibleBanner.loadCollapsibleBanner(this,getString(R.string.BannerGender),true)

    }
}