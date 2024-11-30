package com.lingvo.admob.native_ads

import com.lingvo.admob.native_ads.adapter.NativeAdPlaceholder
import com.google.android.gms.ads.nativead.NativeAd

class NativeDisplayController(
    private val nativeAdPlaceholder: NativeAdPlaceholder,
    private val nativeAdView: NativeAdView
) {

    fun setupAdDisplay() {
        val nativeAd = nativeAdPlaceholder.nativeAd
        if (nativeAd != null) {
            bind(nativeAd)
        } else {
            if (nativeAdPlaceholder.isLoading) {
                nativeAdView.showPlaceholder()
                nativeAdPlaceholder.onAdLoadingListener = {
                    nativeAdPlaceholder.nativeAd
                    nativeAdPlaceholder.nativeAd?.let { bind(it) }
                }
            } else {
                nativeAdView.hidePlaceholder()
            }
        }
    }

    private fun bind(nativeAd: NativeAd) {
        nativeAdPlaceholder.onAdLoadingListener = null
        nativeAdView.showPlaceholder()
        nativeAdView.bind(nativeAd)
    }

    fun cleanupAdDisplay() {
        nativeAdPlaceholder.onAdLoadingListener = null
        nativeAdPlaceholder.release()
    }

}