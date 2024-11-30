package com.lingvo.admob.reward

import android.app.Activity

interface RewardAdManager {

    fun setAdUnitId(id: String)

    fun loadRewardAd()

    suspend fun show(activity: Activity, onStateChanged: (RewardAdShowState) -> Unit)

}