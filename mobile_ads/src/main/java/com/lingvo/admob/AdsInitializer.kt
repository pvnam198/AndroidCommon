package com.lingvo.admob

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.BuildConfig
import com.adjust.sdk.LogLevel
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AdsInitializer {

    private val ioScope get() = CoroutineScope(Dispatchers.IO)

    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            resumeAdjustSdk()
        }

        override fun onActivityPaused(activity: Activity) {
            pauseAdjustSdk()
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }

    fun initializeAds(application: Application, onInitialized: () -> Unit) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        ioScope.launch {
            MobileAds.initialize(application) {
                onInitialized()
            }
            initializeAdjustSdk(application)
        }
    }

    private fun initializeAdjustSdk(context: Context) {
        val appToken = "4j1fo676j4lc"
        val environment = if (BuildConfig.DEBUG) {
            AdjustConfig.ENVIRONMENT_SANDBOX
        } else {
            AdjustConfig.ENVIRONMENT_PRODUCTION
        }

        val config = AdjustConfig(context, appToken, environment).apply {
            setLogLevel(LogLevel.WARN)
        }

        Adjust.initSdk(config)
    }

    private fun resumeAdjustSdk() {
        Adjust.onResume()
    }

    private fun pauseAdjustSdk() {
        Adjust.onPause()
    }

}
