package com.zuri.browser.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zuri.browser.browser.BrowserViewModel
import org.mozilla.geckoview.GeckoView

private const val HOME_URL = "https://duckduckgo.com"

@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    var addressText by remember { mutableStateOf("") }
    var addressFocused by remember { mutableStateOf(false) }

    // Load the homepage once, on first composition.
    LaunchedEffect(Unit) {
        if (state.currentUrl.isEmpty()) viewModel.loadUrl(HOME_URL)
    }

    // Keep the address bar in sync with navigation while the user isn't editing it.
    LaunchedEffect(state.currentUrl) {
        if (!addressFocused) addressText = state.currentUrl
    }

    BackHandler(enabled = state.canGoBack) { viewModel.goBack() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
            // Top bar: security indicator + address field + reload/stop.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.secure) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Secure connection",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                }
                OutlinedTextField(
                    value = addressText,
                    onValueChange = { addressText = it },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { addressFocused = it.isFocused },
                    placeholder = { Text("Search or type a URL") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            viewModel.loadUrl(addressText)
                            focusManager.clearFocus()
                        },
                    ),
                )
                IconButton(onClick = {
                    if (state.isLoading) viewModel.stop() else viewModel.reload()
                }) {
                    Icon(
                        imageVector = if (state.isLoading) Icons.Filled.Close else Icons.Filled.Refresh,
                        contentDescription = if (state.isLoading) "Stop" else "Reload",
                    )
                }
            }

            // Determinate progress while a page loads.
            if (state.isLoading) {
                LinearProgressIndicator(
                    progress = { state.progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // The actual web content.
            AndroidView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                factory = { context ->
                    GeckoView(context).apply { setSession(viewModel.session) }
                },
            )

            // Bottom navigation bar.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { viewModel.goBack() }, enabled = state.canGoBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                IconButton(onClick = { viewModel.goForward() }, enabled = state.canGoForward) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
                }
                Text(
                    text = state.pageTitle.ifEmpty { "Zuri" },
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                )
                IconButton(onClick = { viewModel.loadUrl(HOME_URL) }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Home")
                }
            }
        }
    }
}
