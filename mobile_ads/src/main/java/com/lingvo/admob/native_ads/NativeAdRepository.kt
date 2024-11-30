package com.lingvo.admob.native_ads

import com.lingvo.admob.AdditionalLoadCondition
import com.lingvo.admob.AdditionalShowCondition
import com.google.android.gms.ads.nativead.NativeAd

interface NativeAdRepository {

    var preloadCount: Int

    fun loadAd()

    fun setNativeAdId(id: String)

    fun getLoadedNativeAd(): NativeAd?

    fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?)

    fun setAdditionalShowCondition(condition: AdditionalShowCondition?)

    fun insertAdsAtInterval(originalData: List<Any>, interval: Int, adLimit: Int? = null): List<Any>

    fun addOnNativeAdLoadStateListener(state: OnNativeAdLoadStateListener)

    fun removeOnNativeAdLoadStateListener(state: OnNativeAdLoadStateListener)

}