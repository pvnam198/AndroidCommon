package com.lingvo.admob.open_ads

import android.app.Activity
import com.lingvo.admob.AdditionalLoadCondition
import com.lingvo.admob.AdditionalShowCondition

interface OpenAdManager {

    fun loadAd()

    fun setAdUnitId(adUnitId: String)

    fun show(activity: Activity, onContinueTask: (() -> Unit)? = null)

    fun setAdditionalLoadCondition(condition: AdditionalLoadCondition?)

    fun setAdditionalShowCondition(condition: AdditionalShowCondition?)

}