package com.zuri.browser

import android.app.Application
import com.zuri.browser.browser.GeckoEngine

/**
 * Application entry point. Initializes the single shared GeckoRuntime that every
 * browser session in the app attaches to.
 */
class ZuriApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GeckoEngine.init(this)
    }
}
