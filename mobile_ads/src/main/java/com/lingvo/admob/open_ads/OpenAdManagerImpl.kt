package com.lingvo.admob.open_ads

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
import com.google.android.gms.ads.appopen.AppOpenAd

class OpenAdManagerImpl(private val context: Context) : OpenAdManager {

    private var isLoading = false
    private var appOpenAd: AppOpenAd? = null
    private val loadFailureDelay = AdLoadFailureDelay()
    private var _adUnitId: String? = null
    private var additionalLoadCondition: AdditionalLoadCondition? = null
    private var additionalShowCondition: AdditionalShowCondition? = null
    private val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context)

    private fun isAdEligibleToLoad(): Boolean {
        val additionalLoadCondition = additionalLoadCondition
        if (additionalLoadCondition != null && !additionalLoadCondition.shouldLoad()) {
            return false
        }

        if (isLoading || isAdReady() || loadFailureDelay.shouldDelayRetry() || !googleMobileAdsConsentManager.canRequestAds) {
            return false
        }

        return true
    }

    override fun loadAd() {
        val adUnitId = _adUnitId ?: throw Exception("Ad unit ID must be set")
        if (!isAdEligibleToLoad()) return
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(context, adUnitId, adRequest, object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                AdRevenueTracker.trackAdRevenue(ad)
                setLoadedAd(ad)
                loadFailureDelay.resetFailureCount()
                finishLoading()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                loadFailureDelay.recordFailedLoad()
                finishLoading()
            }
        })
    }

    override fun setAdUnitId(adUnitId: String) {
        _adUnitId = adUnitId
    }

    private fun finishLoading() {
        isLoading = false
    }

    private fun setLoadedAd(ad: AppOpenAd) {
        appOpenAd = ad
    }

    private fun isAdReady(): Boolean {
        return appOpenAd != null
    }

    override fun show(activity: Activity, onContinueTask: (() -> Unit)?) {
        if (!isAdReady()) {
            onContinueTask?.invoke()
            return
        }
        if (additionalShowCondition?.shouldShow() == false) {
            onContinueTask?.invoke()
            return
        }
        val ad = appOpenAd
        loadNewAd()
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onContinueTask?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    onContinueTask?.invoke()
                }
            }
            ad.show(activity)
        } else {
            onContinueTask?.invoke()
        }
    }

    private fun loadNewAd() {
        appOpenAd = null
        loadAd()
    }

    override fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?) {
        additionalLoadCondition = condition
    }

    override fun setAdditionalShowCondition(condition: AdditionalShowCondition?) {
        additionalShowCondition = condition
    }

}


