package com.zuri.browser

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import com.zuri.browser.browser.GeckoEngine
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * Application entry point. Installs a crash handler first so any failure during
 * engine startup is surfaced, then initializes the shared GeckoRuntime — but
 * only in the main process.
 */
class ZuriApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        installCrashHandler()

        // GeckoView renders page content in isolated child processes, and Android
        // runs this Application.onCreate in EVERY process. Creating a GeckoRuntime
        // in a Gecko child process crashes it (blank page, no navigation), so only
        // initialize the engine in the main process.
        if (isMainProcess()) {
            GeckoEngine.init(this)
        }
    }

    private fun isMainProcess(): Boolean = currentProcessName() == packageName

    private fun currentProcessName(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return getProcessName()
        }
        val am = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return null
        val pid = Process.myPid()
        return am.runningAppProcesses?.firstOrNull { it.pid == pid }?.processName
    }

    /**
     * Route uncaught exceptions from the main process to [CrashActivity] (which
     * runs in its own :crash process, so it survives the dying main process) and
     * also persist the trace to external files for retrieval.
     */
    private fun installCrashHandler() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val trace = StringWriter().apply {
                PrintWriter(this).use { throwable.printStackTrace(it) }
            }.toString()

            runCatching {
                File(getExternalFilesDir(null), "last_crash.txt").writeText(trace)
            }
            runCatching {
                startActivity(
                    Intent(this, CrashActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        putExtra(CrashActivity.EXTRA_TRACE, trace)
                    },
                )
            }

            // Give the crash screen a moment to launch, then tear down.
            runCatching { Thread.sleep(400) }
            previous?.uncaughtException(thread, throwable)
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }
    }
}
