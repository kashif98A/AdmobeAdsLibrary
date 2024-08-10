package com.kashifali.admobadslibrary.activity

import android.app.Application
import android.util.Log
import com.kashifali.admobadslibrary.R

import com.lib.admoblib.AppOpenManager

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        AppOpenManager(this, this.getString(R.string.AppOpen))
    }
}