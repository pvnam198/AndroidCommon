package com.lingvo.admob.native_ads

sealed class NativeAdLoadState {
    object Loading : NativeAdLoadState()
    object Loaded : NativeAdLoadState()
}