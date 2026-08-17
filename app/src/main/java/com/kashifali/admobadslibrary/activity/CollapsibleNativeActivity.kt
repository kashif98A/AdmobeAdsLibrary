package com.kashifali.admobadslibrary.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kashifali.admobadslibrary.R
import com.kashifali.admobadslibrary.databinding.ActivityCollapsibleNativeBinding

/**
 * A screen that shows the custom collapsible native ad anchored at the bottom.
 * Tap the chevron on the card to collapse / expand the media area.
 */
class CollapsibleNativeActivity : AppCompatActivity() {

    lateinit var binding: ActivityCollapsibleNativeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollapsibleNativeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nativeCollapsible.loadNativeCollapsible(
            this, getString(R.string.NativeMain), true
        )
    }

    override fun onResume() {
        super.onResume()
        binding.nativeCollapsible.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.nativeCollapsible.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.nativeCollapsible.onDestroy()
    }
}
