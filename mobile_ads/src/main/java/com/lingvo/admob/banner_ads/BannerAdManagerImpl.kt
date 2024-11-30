package com.lingvo.admob.banner_ads

import android.content.Context
import android.os.Bundle
import com.lingvo.admob.AdRevenueTracker
import com.lingvo.admob.AdditionalLoadCondition
import com.lingvo.admob.AdditionalShowCondition
import com.lingvo.admob.GoogleMobileAdsConsentManager
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class BannerAdManagerImpl(
    private val context: Context
) : BannerAdManager {

    private var _adUnitId: String? = null
    private var adView: AdView? = null
    private var isLoading = false
    private var additionalLoadCondition: AdditionalLoadCondition? = null
    private var additionalShowCondition: AdditionalShowCondition? = null
    private val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context)

    private fun isAdEligibleToLoad(): Boolean {
        if (!googleMobileAdsConsentManager.canRequestAds) return false
        if (isLoading) return false
        val additionalLoadCondition = additionalLoadCondition
        if (additionalLoadCondition != null && !additionalLoadCondition.shouldLoad()) {
            return false
        }
        val additionalShowCondition = additionalShowCondition
        if (additionalShowCondition != null){
            return additionalShowCondition.shouldShow()
        }

        return true
    }

    override fun setAdUnitId(adUnitId: String) {
        _adUnitId = adUnitId
    }

    override fun loadBannerAd(
        collapsible: Boolean,
        onAdLoaded: ((AdView) -> Unit)?,
        onAdFailedToLoad: (() -> Unit)?
    ) {
        val adUnitId = _adUnitId ?: throw IllegalStateException("Ad unit ID is not set")
        if (!isAdEligibleToLoad()) {
            onAdFailedToLoad?.invoke()
            return
        }
        isLoading = true
        val adView = AdView(context)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = adUnitId
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                isLoading = false
                AdRevenueTracker.trackAdRevenue(adView)
                onAdLoaded?.invoke(adView)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                isLoading = false
                this@BannerAdManagerImpl.adView = null
                onAdFailedToLoad?.invoke()
            }
        }
        this.adView = adView
        val adRequest = AdRequest.Builder()
        if (collapsible) {
            val extras = Bundle()
            extras.putString("collapsible", "bottom")
            adRequest.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }
        adView.loadAd(adRequest.build())
    }

    override fun destroyBannerAd() {
        adView?.destroy()
        adView = null
    }

    override fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?) {
        additionalLoadCondition = condition
    }

    override fun setAdditionalShowCondition(condition: AdditionalShowCondition?) {
        additionalShowCondition = condition
    }
}
