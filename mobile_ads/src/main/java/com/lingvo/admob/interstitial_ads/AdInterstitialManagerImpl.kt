package com.lingvo.admob.interstitial_ads

import android.app.Activity
import android.content.Context
import com.lingvo.admob.AdLoadFailureDelay
import com.lingvo.admob.AdRevenueTracker
import com.lingvo.admob.AdditionalLoadCondition
import com.lingvo.admob.AdditionalShowCondition
import com.lingvo.admob.GoogleMobileAdsConsentManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdInterstitialManagerImpl(
    private val context: Context
) : AdInterstitialManager {

    private var _adUnitId: String? = null
    private val loadFailureDelay = AdLoadFailureDelay()
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var lastAdShowTime = 0L
    override var adDisplayGapTime: Long = 60000
    private var additionalLoadCondition: AdditionalLoadCondition? = null
    private var additionalShowCondition: AdditionalShowCondition? = null
    private val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context)

    override fun isAdReady(): Boolean {
        return interstitialAd != null
    }

    override fun isAdEligibleToShow(): Boolean {
        if (!isAdReady()) return false
        val additionalShowCondition = additionalShowCondition
        if (additionalShowCondition != null) {
            if (!additionalShowCondition.shouldShow()) return false
        }
        return System.currentTimeMillis() >= lastAdShowTime + adDisplayGapTime
    }

    private fun isAdEligibleToLoad(): Boolean {
        if (!googleMobileAdsConsentManager.canRequestAds) return false
        if (isAdReady()) return false
        if (isLoading) return false
        if (loadFailureDelay.shouldDelayRetry()) return false
        val additionalLoadCondition = additionalLoadCondition
        return (additionalLoadCondition != null && additionalLoadCondition.shouldLoad())
    }

    override fun loadAd() {
        val adUnitId = _adUnitId ?: throw Exception("Ad unit id must be set")
        if (!isAdEligibleToLoad()) return
        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    AdRevenueTracker.trackAdRevenue(ad)
                    loadFailureDelay.resetFailureCount()
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    isLoading = false
                    loadFailureDelay.recordFailedLoad()
                }
            }
        )
    }

    override fun setAdUnitId(adUnitId: String) {
        _adUnitId = adUnitId
    }

    override fun showAdFullScreen(activity: Activity, onAdComplete: () -> Unit) {
        if (!isAdEligibleToShow()) {
            onAdComplete()
            return
        }

        interstitialAd?.let { ad ->
            interstitialAd = null
            loadAd()
            setFullScreenContentCallback(ad, onAdComplete)
            ad.show(activity)
        } ?: {
            loadAd()
            onAdComplete()
        }
    }

    override fun forceShowAdFullScreen(activity: Activity, onAdComplete: () -> Unit) {
        interstitialAd?.let { ad ->
            interstitialAd = null
            setFullScreenContentCallback(ad, onAdComplete)
            ad.show(activity)
        } ?: onAdComplete()
    }

    override fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?) {
        additionalLoadCondition = condition
    }

    override fun setAdditionalShowCondition(condition: AdditionalShowCondition?) {
        additionalShowCondition = condition
    }

    private fun setFullScreenContentCallback(
        interstitialAd: InterstitialAd,
        onAdComplete: () -> Unit
    ) {
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                lastAdShowTime = System.currentTimeMillis()
                onAdComplete()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                super.onAdFailedToShowFullScreenContent(error)
                onAdComplete()
            }
        }
    }
}
