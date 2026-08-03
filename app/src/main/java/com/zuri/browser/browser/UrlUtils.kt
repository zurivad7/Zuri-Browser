package com.zuri.browser.browser

import java.net.URLEncoder

/** Default search engine used when the address-bar input isn't a URL. */
private const val SEARCH_URL = "https://duckduckgo.com/?q="

/**
 * Turn raw address-bar text into something loadable: a full URL is used as-is, a
 * bare domain gets an https scheme, and anything else becomes a search query.
 */
fun normalizeToUri(input: String): String {
    val text = input.trim()
    if (text.isEmpty()) return "about:blank"

    val hasScheme = text.startsWith("http://") ||
        text.startsWith("https://") ||
        text.startsWith("about:") ||
        text.startsWith("file:")
    if (hasScheme) return text

    val looksLikeDomain = !text.contains(" ") && text.contains(".")
    return if (looksLikeDomain) {
        "https://$text"
    } else {
        SEARCH_URL + URLEncoder.encode(text, "UTF-8")
    }
}
