package com.lingvo.admob.native_ads.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd

abstract class BaseNativeAdHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var nativeAdPlaceholder: NativeAdPlaceholder? = null
    private var isVisible = true

    fun bind(nativeAdPlaceholder: NativeAdPlaceholder) {
        this.nativeAdPlaceholder = nativeAdPlaceholder
        if (!isVisible) {
            hideNativeAdPlaceholderWhenInvisible(nativeAdPlaceholder)
            return
        }
        showNativeAdPlaceholder(nativeAdPlaceholder)
        if (nativeAdPlaceholder.nativeAd != null) {
            nativeAdPlaceholder.onAdLoadingListener = null
            onBind(nativeAdPlaceholder.nativeAd!!)
        } else {
            if (nativeAdPlaceholder.isLoading) {
                nativeAdPlaceholder.onAdLoadingListener = {
                    itemView.post { onNativeAdPlaceholderChanged(nativeAdPlaceholder) }
                }
            } else {
                itemView.post { hideNativeAdPlaceholderWhenAdNotLoaded(nativeAdPlaceholder) }
            }
        }
    }

    fun onVisibilityChanged(isVisible: Boolean) {
        this.isVisible = isVisible
    }

    fun refreshAd() {
        nativeAdPlaceholder?.refreshAd()
    }

    fun releaseNativeAd() {
        nativeAdPlaceholder?.release()
        nativeAdPlaceholder = null
    }

    abstract fun onNativeAdPlaceholderChanged(nativeAdPlaceholder: NativeAdPlaceholder)
    abstract fun showNativeAdPlaceholder(nativeAdPlaceholder: NativeAdPlaceholder)
    open fun hideNativeAdPlaceholderWhenInvisible(nativeAdPlaceholder: NativeAdPlaceholder) {}
    abstract fun hideNativeAdPlaceholderWhenAdNotLoaded(nativeAdPlaceholder: NativeAdPlaceholder)
    abstract fun onBind(nativeAd: NativeAd)
}