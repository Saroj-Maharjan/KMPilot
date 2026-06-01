package thisissadeghi.kmpilot

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import thisissadeghi.common.locale.ProvideAppLocale
import thisissadeghi.designsystem.SnackbarController
import thisissadeghi.designsystem.Toast
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.rememberToastState
import thisissadeghi.designsystem.toolbar.LocalToolbarConfig
import thisissadeghi.designsystem.toolbar.ToolbarConfig
import thisissadeghi.designsystem.toolbar.ToolbarRenderer

@Composable
fun App() {
    ProvideAppLocale {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    XTheme {
        val toastState = rememberToastState()
        var currentToolbarConfig by remember { mutableStateOf<ToolbarConfig>(ToolbarConfig.None) }
        val setToolbarConfig: (ToolbarConfig) -> Unit = { config ->
            currentToolbarConfig = config
        }

        val snackbarHostState = remember { SnackbarHostState() }

        CompositionLocalProvider(LocalToolbarConfig provides setToolbarConfig) {
            Scaffold(
                modifier = Modifier.windowInsetsPadding(WindowInsets.ime),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                bottomBar = {},
                topBar = {
                    ToolbarRenderer(
                        config = currentToolbarConfig,
                    )
                },
                contentWindowInsets = WindowInsets.statusBars,
            ) { innerPadding ->
                BaseAppNavHost(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                )

                Toast(state = toastState)

                SnackbarController(snackbarHostState = snackbarHostState)
            }
        }
    }
}
