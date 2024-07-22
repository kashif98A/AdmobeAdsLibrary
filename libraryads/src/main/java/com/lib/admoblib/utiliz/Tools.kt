package com.lib.admoblib.utiliz

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import com.lib.admoblib.R
import com.lib.admoblib.isNetworkConnected
import java.io.IOException
import java.io.InputStream


object Tools {
    private var isNavigationBarHidden = false
    private var isCollapsed = false

    fun getexpendcollap(view: View) {
        if (isCollapsed) {
            isCollapsed = false
            expand(view)
        } else {
            collapse(view)
            isCollapsed = true
        }
    }

    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }


    fun hideOrShowNavigation(context: Activity) {
        if ( context.isNetworkConnected() ) {
            hideNavigationBar(context)
        } else {
            showNavigationBar(context)
        }
    }

    fun hideNavigationBar(context: Activity) {
        val decorView = context.window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
        isNavigationBarHidden = true
    }

    fun showNavigationBar(context: Activity) {
        val decorView = context.window.decorView
        // Clear the SYSTEM_UI_FLAG_HIDE_NAVIGATION flag to show the navigation bar
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        isNavigationBarHidden = false
    }


    fun getImageFileNamesFromAssets(context: Context, foldername: String): List<String> {
        val assetManager = context.assets
        val imageUrls = mutableListOf<String>()

        try {
            val assetNames = assetManager.list(foldername)
            if (assetNames != null && assetNames.isNotEmpty()) {
                for (fileName in assetNames) {
                    val imageUrl = "file:///android_asset/$foldername/$fileName"
                    imageUrls.add(imageUrl)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageUrls
    }

    fun loadJSONFromAsset(context: Context, fileName: String): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }

    fun rateUs(activity: Activity) {
        val uri = Uri.parse("market://details?id=${activity.packageName}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            activity.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=${activity.packageName}")))
        }
    }

    fun shareApp(activity: Activity,appname:String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, appname)
                val shareMessage = """
                Let me recommend you this application

                https://play.google.com/store/apps/details?id=${activity.packageName}
            """.trimIndent()
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }
            activity.startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun moreApps(context: Context,accountname:String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/developer?id=${accountname}")
        }
        context.startActivity(intent)
    }



}
