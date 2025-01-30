package com.kashifali.admobadslibrary.activity

import android.app.Application
import com.kashifali.admobadslibrary.R

import com.lib.admoblib.appOpen.AppOpenControl

class MyApp:Application() {
    var appOpenManager: AppOpenControl?=null
    override fun onCreate() {
        super.onCreate()
        appOpenManager = AppOpenControl(this,  this.getString(R.string.AppOpen))
    }
}