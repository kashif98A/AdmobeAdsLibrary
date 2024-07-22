// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.lib.admoblib.nativeAds

import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable

/** A class containing the optional styling options for the Native Template. *  */
class NativeTemplateStyle {
    // Call to action typeface.
    var callToActionTextTypeface: Typeface? = null
        private set

    // Size of call to action Heading_Text.
    var callToActionTextSize = 0f
        private set

    // Call to action typeface color in the form 0xAARRGGBB.
    var callToActionTypefaceColor = 0
        private set

    // Call to action background color.
    var callToActionBackgroundColor: ColorDrawable? = null
        private set

    // All templates have a primary Heading_Text area which is populated by the native ad's headline.
    // Primary Heading_Text typeface.
    var primaryTextTypeface: Typeface? = null
        private set

    // Size of primary Heading_Text.
    var primaryTextSize = 0f
        private set

    // Primary Heading_Text typeface color in the form 0xAARRGGBB.
    var primaryTextTypefaceColor = 0
        private set

    // Primary Heading_Text background color.
    var primaryTextBackgroundColor: ColorDrawable? = null
        private set

    // The typeface, typeface color, and background color for the second row of Heading_Text in the template.
    // All templates have a secondary Heading_Text area which is populated either by the body of the ad or
    // by the rating of the app.
    // Secondary Heading_Text typeface.
    var secondaryTextTypeface: Typeface? = null
        private set

    // Size of secondary Heading_Text.
    var secondaryTextSize = 0f
        private set

    // Secondary Heading_Text typeface color in the form 0xAARRGGBB.
    var secondaryTextTypefaceColor = 0
        private set

    // Secondary Heading_Text background color.
    var secondaryTextBackgroundColor: ColorDrawable? = null
        private set

    // The typeface, typeface color, and background color for the third row of Heading_Text in the template.
    // The third row is used to display store name or the default tertiary Heading_Text.
    // Tertiary Heading_Text typeface.
    var tertiaryTextTypeface: Typeface? = null
        private set

    // Size of tertiary Heading_Text.
    var tertiaryTextSize = 0f
        private set

    // Tertiary Heading_Text typeface color in the form 0xAARRGGBB.
    var tertiaryTextTypefaceColor = 0
        private set

    // Tertiary Heading_Text background color.
    var tertiaryTextBackgroundColor: ColorDrawable? = null
        private set

    // The background color for the bulk of the ad.
    var mainBackgroundColor: ColorDrawable? = null
        private set

    /** A class that provides helper methods to build a style object. *  */
    class Builder {
        private val styles: NativeTemplateStyle

        init {
            styles = NativeTemplateStyle()
        }

        fun withCallToActionTextTypeface(callToActionTextTypeface: Typeface?): Builder {
            styles.callToActionTextTypeface = callToActionTextTypeface
            return this
        }

        fun withCallToActionTextSize(callToActionTextSize: Float): Builder {
            styles.callToActionTextSize = callToActionTextSize
            return this
        }

        fun withCallToActionTypefaceColor(callToActionTypefaceColor: Int): Builder {
            styles.callToActionTypefaceColor = callToActionTypefaceColor
            return this
        }

        fun withCallToActionBackgroundColor(callToActionBackgroundColor: ColorDrawable?): Builder {
            styles.callToActionBackgroundColor = callToActionBackgroundColor
            return this
        }

        fun withPrimaryTextTypeface(primaryTextTypeface: Typeface?): Builder {
            styles.primaryTextTypeface = primaryTextTypeface
            return this
        }

        fun withPrimaryTextSize(primaryTextSize: Float): Builder {
            styles.primaryTextSize = primaryTextSize
            return this
        }

        fun withPrimaryTextTypefaceColor(primaryTextTypefaceColor: Int): Builder {
            styles.primaryTextTypefaceColor = primaryTextTypefaceColor
            return this
        }

        fun withPrimaryTextBackgroundColor(primaryTextBackgroundColor: ColorDrawable?): Builder {
            styles.primaryTextBackgroundColor = primaryTextBackgroundColor
            return this
        }

        fun withSecondaryTextTypeface(secondaryTextTypeface: Typeface?): Builder {
            styles.secondaryTextTypeface = secondaryTextTypeface
            return this
        }

        fun withSecondaryTextSize(secondaryTextSize: Float): Builder {
            styles.secondaryTextSize = secondaryTextSize
            return this
        }

        fun withSecondaryTextTypefaceColor(secondaryTextTypefaceColor: Int): Builder {
            styles.secondaryTextTypefaceColor = secondaryTextTypefaceColor
            return this
        }

        fun withSecondaryTextBackgroundColor(secondaryTextBackgroundColor: ColorDrawable?): Builder {
            styles.secondaryTextBackgroundColor = secondaryTextBackgroundColor
            return this
        }

        fun withTertiaryTextTypeface(tertiaryTextTypeface: Typeface?): Builder {
            styles.tertiaryTextTypeface = tertiaryTextTypeface
            return this
        }

        fun withTertiaryTextSize(tertiaryTextSize: Float): Builder {
            styles.tertiaryTextSize = tertiaryTextSize
            return this
        }

        fun withTertiaryTextTypefaceColor(tertiaryTextTypefaceColor: Int): Builder {
            styles.tertiaryTextTypefaceColor = tertiaryTextTypefaceColor
            return this
        }

        fun withTertiaryTextBackgroundColor(tertiaryTextBackgroundColor: ColorDrawable?): Builder {
            styles.tertiaryTextBackgroundColor = tertiaryTextBackgroundColor
            return this
        }

        fun withMainBackgroundColor(mainBackgroundColor: ColorDrawable?): Builder {
            styles.mainBackgroundColor = mainBackgroundColor
            return this
        }

        fun build(): NativeTemplateStyle {
            return styles
        }
    }
}
