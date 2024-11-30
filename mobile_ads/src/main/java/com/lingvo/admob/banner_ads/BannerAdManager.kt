package com.lingvo.admob.banner_ads

import com.lingvo.admob.AdditionalLoadCondition
import com.lingvo.admob.AdditionalShowCondition
import com.google.android.gms.ads.AdView

interface BannerAdManager {

    fun setAdUnitId(adUnitId: String)

    fun loadBannerAd(
        collapsible: Boolean = false,
        onAdLoaded: ((AdView) -> Unit)? = null,
        onAdFailedToLoad: (() -> Unit)? = null
    )

    fun destroyBannerAd()

    fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?)

    fun setAdditionalShowCondition(condition: AdditionalShowCondition?)
}