package com.zuri.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zuri.browser.ui.BrowserScreen
import com.zuri.browser.ui.theme.ZuriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZuriTheme {
                BrowserScreen()
            }
        }
    }
}
