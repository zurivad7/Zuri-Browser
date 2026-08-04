package com.zuri.browser.browser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mozilla.geckoview.GeckoSession

/** Immutable snapshot of the current tab, rendered by the Compose UI. */
data class BrowserState(
    val currentUrl: String = "",
    val pageTitle: String = "",
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
) {
    val secure: Boolean get() = currentUrl.startsWith("https://")
}

/**
 * Holds the single [GeckoSession] for M1 (one tab) and mirrors its navigation and
 * progress callbacks into a [StateFlow] the UI observes. Multi-tab management lands
 * in a later milestone.
 */
class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(BrowserState())
    val state: StateFlow<BrowserState> = _state.asStateFlow()

    /** Last URL asked to load, used to recover from a content-process crash. */
    private var lastRequestedUrl: String? = null

    val session: GeckoSession = GeckoSession().apply {
        open(GeckoEngine.requireRuntime())
    }

    init {
        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLocationChange(
                session: GeckoSession,
                url: String?,
                perms: MutableList<GeckoSession.PermissionDelegate.ContentPermission>,
                hasUserGesture: Boolean,
            ) {
                _state.update { it.copy(currentUrl = url ?: "") }
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                _state.update { it.copy(canGoBack = canGoBack) }
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                _state.update { it.copy(canGoForward = canGoForward) }
            }
        }

        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                _state.update { it.copy(isLoading = true, progress = 0) }
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                _state.update { it.copy(isLoading = false, progress = 100) }
            }

            override fun onProgressChange(session: GeckoSession, progress: Int) {
                _state.update { it.copy(progress = progress) }
            }
        }

        session.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                _state.update { it.copy(pageTitle = title ?: "") }
            }

            // If the Gecko content process dies, recover by reloading rather
            // than letting the tab stay blank.
            override fun onCrash(session: GeckoSession) {
                recover()
            }

            override fun onKill(session: GeckoSession) {
                recover()
            }
        }
    }

    private fun recover() {
        val url = lastRequestedUrl
        if (url != null) session.loadUri(url)
    }

    fun loadUrl(input: String) {
        val uri = normalizeToUri(input)
        lastRequestedUrl = uri
        session.loadUri(uri)
    }

    fun reload() = session.reload()

    fun stop() = session.stop()

    fun goBack() = session.goBack()

    fun goForward() = session.goForward()

    override fun onCleared() {
        super.onCleared()
        session.close()
    }
}
