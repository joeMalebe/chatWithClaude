package co.za.server

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
