package com.lingvo.admob.interstitial_ads

import android.app.Activity
import com.lingvo.admob.AdditionalLoadCondition
import com.lingvo.admob.AdditionalShowCondition

interface AdInterstitialManager {

    var adDisplayGapTime: Long

    fun isAdReady(): Boolean

    fun isAdEligibleToShow(): Boolean

    fun loadAd()

    fun setAdUnitId(adUnitId: String)

    fun showAdFullScreen(activity: Activity, onAdComplete: () -> Unit)

    fun forceShowAdFullScreen(activity: Activity, onAdComplete: () -> Unit)

    fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?)

    fun setAdditionalShowCondition(condition: AdditionalShowCondition?)


}