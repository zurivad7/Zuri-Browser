package com.zuri.browser.browser

import android.content.Context
import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings

/**
 * Owns the process-wide [GeckoRuntime]. GeckoView requires exactly one runtime per
 * process; all [org.mozilla.geckoview.GeckoSession] instances share it.
 *
 * Enhanced Tracking Protection is turned on at the runtime level so every tab gets
 * ad/tracker blocking without per-session wiring.
 */
object GeckoEngine {

    @Volatile
    private var runtime: GeckoRuntime? = null

    /** Idempotently create the runtime. Safe to call from Application.onCreate. */
    fun init(context: Context): GeckoRuntime {
        return runtime ?: synchronized(this) {
            runtime ?: buildRuntime(context.applicationContext).also { runtime = it }
        }
    }

    fun requireRuntime(): GeckoRuntime =
        runtime ?: error("GeckoEngine.init() must run before requireRuntime()")

    private fun buildRuntime(context: Context): GeckoRuntime {
        val contentBlocking = ContentBlocking.Settings.Builder()
            .antiTracking(
                ContentBlocking.AntiTracking.DEFAULT or
                    ContentBlocking.AntiTracking.STP,
            )
            .cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS)
            .enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.STRICT)
            .build()

        val settings = GeckoRuntimeSettings.Builder()
            .contentBlocking(contentBlocking)
            .javaScriptEnabled(true)
            .build()

        return GeckoRuntime.create(context, settings)
    }
}
