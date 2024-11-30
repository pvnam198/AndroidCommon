package com.lingvo.admob.native_ads.adapter

import com.lingvo.admob.native_ads.NativeAdLoadState
import com.lingvo.admob.native_ads.NativeAdRepository
import com.lingvo.admob.native_ads.OnNativeAdLoadStateListener
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdPlaceholder(
    private val nativeAdRepository: NativeAdRepository
) : OnNativeAdLoadStateListener {

    private var _nativeAd: NativeAd? = null
    private var _isLoading: Boolean = false

    val isLoading get() = _isLoading
    var onAdLoadingListener: (() -> Unit)? = null

    init {
        nativeAdRepository.addOnNativeAdLoadStateListener(this)
    }

    val nativeAd: NativeAd?
        get() {
            if (_nativeAd == null) {
                _nativeAd = nativeAdRepository.getLoadedNativeAd()
                nativeAdRepository.loadAd()
            }
            return _nativeAd
        }

    fun load() {
        nativeAdRepository.loadAd()
    }

    fun refreshAd() {
        _nativeAd?.destroy()
        _nativeAd = null
        load()
    }

    fun release() {
        _nativeAd?.destroy()
        _nativeAd = null
        onAdLoadingListener = null
        nativeAdRepository.removeOnNativeAdLoadStateListener(this)
    }

    override fun onNativeAdLoadStateChanged(state: NativeAdLoadState) {
        _isLoading = when (state) {
            is NativeAdLoadState.Loading -> {
                true
            }

            is NativeAdLoadState.Loaded -> {
                false
            }
        }
        onAdLoadingListener?.invoke()
    }

}
