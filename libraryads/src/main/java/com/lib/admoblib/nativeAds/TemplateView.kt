package com.lib.admoblib.nativeAds

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.lib.admoblib.R


class TemplateView : FrameLayout {
    private var templateType = 0
    private var styles: NativeTemplateStyle? = null
    internal var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null
    private var primaryView: TextView? = null
    private var secondaryView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var tertiaryView: TextView? = null
    private var iconView: ImageView? = null
    private var mediaView: MediaView? = null
    private var callToActionView: Button? = null
    private var background: ConstraintLayout? = null
    private var iconparent_Lay: ConstraintLayout? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    fun setStyles(styles: NativeTemplateStyle?) {
        this.styles = styles
        applyStyles()
    }

    fun getNativeAdView(): NativeAdView? {
        return nativeAdView
    }

    private fun applyStyles() {
        val mainBackground: Drawable? = styles!!.mainBackgroundColor
        if (mainBackground != null) {
            background?.setBackground(mainBackground)
            if (primaryView != null) {
                primaryView!!.background = mainBackground
            }
            if (secondaryView != null) {
                secondaryView!!.background = mainBackground
            }
            if (tertiaryView != null) {
                tertiaryView!!.background = mainBackground
            }
        }
        val primary: Typeface? = styles!!.primaryTextTypeface
        if (primary != null && primaryView != null) {
            primaryView!!.setTypeface(primary)
        }
        val secondary: Typeface? = styles!!.secondaryTextTypeface
        if (secondary != null && secondaryView != null) {
            secondaryView!!.setTypeface(secondary)
        }
        val tertiary: Typeface? = styles!!.tertiaryTextTypeface
        if (tertiary != null && tertiaryView != null) {
            tertiaryView!!.setTypeface(tertiary)
        }
        val ctaTypeface: Typeface? = styles!!.callToActionTextTypeface
        if (ctaTypeface != null && callToActionView != null) {
            callToActionView!!.setTypeface(ctaTypeface)
        }
        val primaryTypefaceColor = styles!!.primaryTextTypefaceColor
        if (primaryTypefaceColor > 0 && primaryView != null) {
            primaryView!!.setTextColor(primaryTypefaceColor)
        }
        val secondaryTypefaceColor = styles!!.secondaryTextTypefaceColor
        if (secondaryTypefaceColor > 0 && secondaryView != null) {
            secondaryView!!.setTextColor(secondaryTypefaceColor)
        }
        val tertiaryTypefaceColor = styles!!.tertiaryTextTypefaceColor
        if (tertiaryTypefaceColor > 0 && tertiaryView != null) {
            tertiaryView!!.setTextColor(tertiaryTypefaceColor)
        }
        val ctaTypefaceColor = styles!!.callToActionTypefaceColor
        if (ctaTypefaceColor > 0 && callToActionView != null) {
            callToActionView!!.setTextColor(ctaTypefaceColor)
        }
        val ctaTextSize = styles!!.callToActionTextSize
        if (ctaTextSize > 0 && callToActionView != null) {
            callToActionView!!.textSize = ctaTextSize
        }
        val primaryTextSize = styles!!.primaryTextSize
        if (primaryTextSize > 0 && primaryView != null) {
            primaryView!!.textSize = primaryTextSize
        }
        val secondaryTextSize = styles!!.secondaryTextSize
        if (secondaryTextSize > 0 && secondaryView != null) {
            secondaryView!!.textSize = secondaryTextSize
        }
        val tertiaryTextSize = styles!!.tertiaryTextSize
        if (tertiaryTextSize > 0 && tertiaryView != null) {
            tertiaryView!!.textSize = tertiaryTextSize
        }
        val ctaBackground: Drawable? = styles!!.callToActionBackgroundColor
        if (ctaBackground != null && callToActionView != null) {
            callToActionView!!.background = ctaBackground
        }
        val primaryBackground: Drawable? = styles!!.primaryTextBackgroundColor
        if (primaryBackground != null && primaryView != null) {
            primaryView!!.background = primaryBackground
        }
        val secondaryBackground: Drawable? = styles!!.secondaryTextBackgroundColor
        if (secondaryBackground != null && secondaryView != null) {
            secondaryView!!.background = secondaryBackground
        }
        val tertiaryBackground: Drawable? = styles!!.tertiaryTextBackgroundColor
        if (tertiaryBackground != null && tertiaryView != null) {
            tertiaryView!!.background = tertiaryBackground
        }
        invalidate()
        requestLayout()
    }

    private fun adHasOnlyStore(nativeAd: NativeAd): Boolean {
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
    }

    fun setNativeAd(nativeAd: NativeAd) {
        this.nativeAd = nativeAd
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        val headline = nativeAd.headline
        val body = nativeAd.body
        val cta = nativeAd.callToAction
        val starRating = nativeAd.starRating
        val icon = nativeAd.icon
        val secondaryText: String?
        callToActionView!!.visibility = View.VISIBLE
        //        iconparent_Lay.setVisibility(VISIBLE);
        nativeAdView?.setCallToActionView(callToActionView)
        nativeAdView?.setHeadlineView(primaryView)
        nativeAdView?.setMediaView(mediaView)
        secondaryView!!.visibility = View.VISIBLE
        secondaryText = if (adHasOnlyStore(nativeAd)) {
            nativeAdView?.setStoreView(secondaryView)
            store
        } else if (!TextUtils.isEmpty(advertiser)) {
            nativeAdView?.setAdvertiserView(secondaryView)
            advertiser
        } else {
            ""
        }
        primaryView!!.text = headline
        callToActionView!!.text = cta

        //  Set the secondary view to be the star rating if available.
        if (starRating != null && starRating > 0) {
            secondaryView!!.visibility = View.GONE
            ratingBar?.setVisibility(View.VISIBLE)
            ratingBar?.setRating(starRating.toFloat())
            nativeAdView?.setStarRatingView(ratingBar)
        } else {
            secondaryView!!.text = secondaryText
            secondaryView!!.visibility = View.VISIBLE
            ratingBar?.setVisibility(View.GONE)
        }
        if (icon != null) {
            iconView!!.setVisibility(View.VISIBLE)
            iconView!!.setImageDrawable(icon.drawable)
        } else {
            iconView!!.setVisibility(View.GONE)
        }
        if (tertiaryView != null) {
            tertiaryView!!.text = body
            nativeAdView?.setBodyView(tertiaryView)
        }
        nativeAdView?.setNativeAd(nativeAd)
    }

    /**
     * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
     * method does not destroy the template view.
     * https://developers.google.com/admob/android/native-unified#destroy_ad
     */
    fun destroyNativeAd() {
        nativeAd!!.destroy()
    }


    val templateTypeName: String
        get() {
            if (templateType == R.layout.gnt_medium_template_view) {
                return MEDIUM_TEMPLATE
            } else if (templateType == R.layout.gnt_small_template_view) {
                return SMALL_TEMPLATE
            }
            return ""
        }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        val attributes: TypedArray =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)
        templateType = try {
            attributes.getResourceId(
                R.styleable.TemplateView_gnt_template_type, R.layout.gnt_medium_template_view
            )
        } finally {
            attributes.recycle()
        }
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(templateType, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        nativeAdView = findViewById<View>(R.id.native_ad_view) as NativeAdView?
        primaryView = findViewById<View>(R.id.primary) as TextView?
        secondaryView = findViewById<View>(R.id.secondary) as TextView?
        tertiaryView = findViewById<View>(R.id.body) as TextView?
        ratingBar = findViewById<View>(R.id.rating_bar) as RatingBar?
        ratingBar?.setEnabled(false)
        callToActionView = findViewById<View>(R.id.cta) as Button?
        iconView = findViewById<View>(R.id.icon) as ImageView?
        mediaView = findViewById<View>(R.id.media_view) as MediaView?
        background = findViewById<View>(R.id.background) as ConstraintLayout?
        iconparent_Lay = findViewById<ConstraintLayout>(R.id.iconparent_Lay)
    }

    companion object {
        private const val MEDIUM_TEMPLATE = "medium_template"
        private const val SMALL_TEMPLATE = "small_template"
    }
}
