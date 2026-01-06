package thisissadeghi.designsystem.toolbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import thisissadeghi.designsystem.XTextIconButton

sealed class ToolbarConfig {
    data object None : ToolbarConfig()

    data class Simple(
        val title: String,
        val showBack: Boolean = false,
        val onBackClick: () -> Unit = {},
    ) : ToolbarConfig()

    data class Custom(
        val content: @Composable () -> Unit,
    ) : ToolbarConfig()
}

// Toolbar provider
val LocalToolbarConfig =
    compositionLocalOf<(ToolbarConfig) -> Unit> {
        error("ToolbarConfig setter not provided")
    }

@Composable
fun ToolbarRenderer(config: ToolbarConfig) {
    when (config) {
        is ToolbarConfig.None -> { /* No toolbar */ }

        is ToolbarConfig.Simple -> {
            XTopAppBar(
                title = {
                    Text(text = config.title)
                },
                navigationIcon = {
                    if (config.showBack) {
                        XTextIconButton(onClick = config.onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                },
            )
        }

        is ToolbarConfig.Custom -> {
            config.content()
        }
    }
}
