package thisissadeghi.receive.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpilot.feature.receive.generated.resources.Res
import kmpilot.feature.receive.generated.resources.cd_back
import kmpilot.feature.receive.generated.resources.error_message
import kmpilot.feature.receive.generated.resources.error_title
import kmpilot.feature.receive.generated.resources.receive_title
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScreen
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.app.AppErrorState
import thisissadeghi.designsystem.app.AppLoadingState
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.designsystem.toolbar.XTopAppBarAlignment
import thisissadeghi.receive.presentation.ReceiveUiModel
import thisissadeghi.receive.presentation.ReceiveViewModel
import thisissadeghi.receive.presentation.ui.components.ReceiveBottomBar
import thisissadeghi.receive.presentation.ui.components.ReceiveSuccessContent

@Composable
fun ReceiveScreen(
    viewModel: ReceiveViewModel,
    onBackClick: () -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    ReceiveScreenRoot(
        uiModel = uiModel,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
    )
}

@Composable
fun ReceiveScreenRoot(
    uiModel: ReceiveUiModel,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onAssetSelectorClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    XScreen(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            XTopAppBar(
                title = {
                    XText(
                        text = stringResource(Res.string.receive_title),
                    )
                },
                navigationIcon = {
                    XIconButton(
                        onClick = onBackClick,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        XIcon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.cd_back),
                        )
                    }
                },
                backgroundColor = Color.Transparent,
                alignment = XTopAppBarAlignment.Start,
            )
        },
        bottomBar = {
            when (uiModel.dataState) {
                is UiState.Success ->
                    ReceiveBottomBar(
                        onShareClick = onShareClick,
                        onCopyClick = onCopyClick,
                    )
                else -> {}
            }
        },
    ) {
        when (val state = uiModel.dataState) {
            UiState.Uninitialized,
            UiState.Loading,
            -> AppLoadingState()

            is UiState.Success ->
                ReceiveSuccessContent(
                    data = state.value,
                    onAssetSelectorClick = onAssetSelectorClick,
                    onCopyClick = onCopyClick,
                )

            is UiState.Failed ->
                AppErrorState(
                    title = stringResource(Res.string.error_title),
                    message = stringResource(Res.string.error_message),
                    onRetry = onRetry,
                )
        }
    }
}
