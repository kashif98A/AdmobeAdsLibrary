package com.lib.admoblib.utiliz

import android.app.Activity
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

object InAppReviewHelperJava {
    fun launchInAppReview(context: Activity, onCompleted: (Boolean) -> Unit) {
        val reviewManager: ReviewManager = ReviewManagerFactory.create(context)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { requestTask ->
            if (requestTask.isSuccessful) {
                val reviewInfo = requestTask.result
                val flow = reviewManager.launchReviewFlow(context, reviewInfo)
                flow.addOnCompleteListener { reviewTask ->
                    onCompleted(reviewTask.isSuccessful)
                }
            } else {
                onCompleted(false)
            }
        }
    }
}