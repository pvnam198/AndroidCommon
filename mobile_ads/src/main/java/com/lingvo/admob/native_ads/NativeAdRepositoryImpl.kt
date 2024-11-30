package com.lingvo.admob.native_ads

import android.content.Context
import android.util.Log
import com.lingvo.admob.AdLoadFailureDelay
import com.lingvo.admob.AdRevenueTracker
import com.lingvo.admob.AdditionalLoadCondition
import com.lingvo.admob.AdditionalShowCondition
import com.lingvo.admob.GoogleMobileAdsConsentManager
import com.lingvo.admob.native_ads.adapter.NativeAdPlaceholder
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class NativeAdRepositoryImpl(
    private val context: Context
) : NativeAdRepository {

    private val loadStateListeners = mutableListOf<OnNativeAdLoadStateListener>()
    private var _nativeAdId: String? = null
    private var nativeAds = mutableListOf<NativeAd>()
    private val failedLoadGap = AdLoadFailureDelay()
    override var preloadCount: Int = 2
    private var additionalLoadCondition: AdditionalLoadCondition? = null
    private var additionalShowCondition: AdditionalShowCondition? = null
    private val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context)
    private var isLoading = false
    private var isGettingNativeAd = false

    private fun shouldLoad(): Boolean {
        if (isGettingNativeAd) return false
        if (isLoading) return false
        if (!googleMobileAdsConsentManager.canRequestAds) return false
        if (nativeAds.size >= preloadCount) return false
        if (failedLoadGap.shouldDelayRetry()) return false
        val additionalLoadCondition = additionalLoadCondition
        if (additionalLoadCondition != null) {
            return additionalLoadCondition.shouldLoad()
        }
        return true
    }

    override fun loadAd() {
        val nativeAdId = _nativeAdId ?: throw IllegalStateException("Native ad id is not set")
        if (!shouldLoad()) {
            onLoaded()
            return
        }

        onLoading()
        val adLoader = AdLoader.Builder(context, nativeAdId).forNativeAd { ad: NativeAd ->
            AdRevenueTracker.trackAdRevenue(ad)
            failedLoadGap.resetFailureCount()
            nativeAds.add(ad)
            onLoaded()
            loadAd()
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("log_test", "onAdFailedToLoad: ${adError.message}")
                failedLoadGap.recordFailedLoad()
                onLoaded()
                loadAd()
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun onLoading() {
        isLoading = true
        loadStateListeners.forEach { it.onNativeAdLoadStateChanged(NativeAdLoadState.Loading) }
    }

    private fun onLoaded() {
        isLoading = false
        loadStateListeners.forEach { it.onNativeAdLoadStateChanged(NativeAdLoadState.Loaded) }
    }

    override fun setNativeAdId(id: String) {
        _nativeAdId = id
    }

    override fun getLoadedNativeAd(): NativeAd? {
        var nativeAd: NativeAd?
        synchronized(this) {
            isGettingNativeAd = true
            val mutableAdsList = ArrayList(nativeAds)
            if (mutableAdsList.isEmpty()) return null
            nativeAd = mutableAdsList[0]
            nativeAds.remove(nativeAd)
            isGettingNativeAd = false
        }
        return nativeAd
    }

    override fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?) {
        additionalLoadCondition = condition
    }

    override fun setAdditionalShowCondition(condition: AdditionalShowCondition?) {
        additionalShowCondition = condition
    }

    override fun insertAdsAtInterval(
        originalData: List<Any>, interval: Int, adLimit: Int?
    ): List<Any> {
        val dataWithAds = mutableListOf<Any>()
        var totalAdsAdded = 0
        for ((index, item) in originalData.withIndex()) {
            dataWithAds.add(item)
            val isAdLimitReached = adLimit != null && totalAdsAdded >= adLimit
            if (!isAdLimitReached && (index + 1) % interval == 0) {
                totalAdsAdded += 1
                val adPlaceholder = NativeAdPlaceholder(this)
                dataWithAds.add(adPlaceholder)
            }
        }
        return dataWithAds
    }

    override fun addOnNativeAdLoadStateListener(state: OnNativeAdLoadStateListener) {
        loadStateListeners.add(state)
    }

    override fun removeOnNativeAdLoadStateListener(state: OnNativeAdLoadStateListener) {
        loadStateListeners.remove(state)
    }

}