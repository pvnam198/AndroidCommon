package com.lingvo.admob.native_ads

import com.google.android.gms.ads.nativead.NativeAd

interface NativeAdView {

    fun bind(nativeAd: NativeAd)

    fun showPlaceholder()

    fun hidePlaceholder()

}