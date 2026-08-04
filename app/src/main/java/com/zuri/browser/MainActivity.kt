package com.zuri.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zuri.browser.ui.BrowserScreen
import com.zuri.browser.ui.theme.ZuriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ZuriTheme {
                BrowserScreen()
            }
        }
    }
}
