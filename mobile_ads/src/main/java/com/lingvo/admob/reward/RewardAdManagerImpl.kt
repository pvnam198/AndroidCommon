package com.lingvo.admob.reward

import android.app.Activity
import android.content.Context
import com.lingvo.admob.AdRevenueTracker
import com.lingvo.admob.GoogleMobileAdsConsentManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class RewardAdManagerImpl(private val context: Context) : RewardAdManager {

    private var rewardedAd: RewardedAd? = null
    private val adLoadState = MutableStateFlow(RewardLoadState.Initial)
    private val googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context)
    private var adUnitId: String? = null
    private var isShowAdProgress = false

    override fun setAdUnitId(id: String) {
        adUnitId = id
    }

    override fun loadRewardAd() {
        val adUnitId = adUnitId ?: throw Exception("Ad unit id must be set")
        if (!googleMobileAdsConsentManager.canRequestAds) return
        if (rewardedAd != null) return
        if (adLoadState.value != RewardLoadState.Initial) return
        adLoadState.tryEmit(RewardLoadState.Loading)
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adLoadState.tryEmit(RewardLoadState.Failed)
            }

            override fun onAdLoaded(ad: RewardedAd) {
                AdRevenueTracker.trackAdRevenue(ad)
                rewardedAd = ad
                adLoadState.tryEmit(RewardLoadState.Loaded)
            }
        })
    }

    override suspend fun show(activity: Activity, onStateChanged: (RewardAdShowState) -> Unit) {
        if (isShowAdProgress) return
        isShowAdProgress = true
        withContext(Dispatchers.Main) {
            adLoadState.collect { state ->
                when (state) {
                    RewardLoadState.Initial -> {
                        onStateChanged(RewardAdShowState.Initial)
                    }

                    RewardLoadState.Loading -> {
                        onStateChanged(RewardAdShowState.Loading)
                    }

                    RewardLoadState.Loaded -> {
                        val rewardedAd =
                            rewardedAd ?: throw IllegalStateException("Reward ad is required")
                        show(rewardedAd, onStateChanged, activity)
                    }

                    RewardLoadState.Failed -> {
                        onStateChanged(RewardAdShowState.Failed("Reward failed to load"))
                        finishShowAdProgress()
                    }
                }
            }
        }
    }

    private fun CoroutineScope.show(
        rewardedAd: RewardedAd,
        onStateChanged: (RewardAdShowState) -> Unit,
        activity: Activity
    ) {
        var isEarned = false

        rewardedAd.fullScreenContentCallback =
            object : FullScreenContentCallback() {

                override fun onAdClicked() {
                    onStateChanged(RewardAdShowState.Clicked)
                }

                override fun onAdDismissedFullScreenContent() {
                    onStateChanged(RewardAdShowState.Completed(isEarned))
                    finishShowAdProgress()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    onStateChanged(RewardAdShowState.Failed(adError.message))
                    finishShowAdProgress()
                }

                override fun onAdImpression() {
                    onStateChanged(RewardAdShowState.Impression)
                }

                override fun onAdShowedFullScreenContent() {
                    onStateChanged(RewardAdShowState.Showing)
                }
            }

        rewardedAd.show(activity) {
            isEarned = true
            onStateChanged(RewardAdShowState.RewardEarned)
        }
    }

    private fun CoroutineScope.finishShowAdProgress() {
        cancel()
        isShowAdProgress = false
        resetRewardAd()
    }

    private fun resetRewardAd() {
        rewardedAd = null
        adLoadState.value = RewardLoadState.Initial
    }

}