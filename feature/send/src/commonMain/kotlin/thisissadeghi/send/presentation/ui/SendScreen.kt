package thisissadeghi.send.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpilot.feature.send.generated.resources.Res
import kmpilot.feature.send.generated.resources.cd_back
import kmpilot.feature.send.generated.resources.error_message
import kmpilot.feature.send.generated.resources.error_title
import kmpilot.feature.send.generated.resources.return_to_dashboard
import kmpilot.feature.send.generated.resources.send_title
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScreen
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextButton
import thisissadeghi.designsystem.app.AppErrorState
import thisissadeghi.designsystem.app.AppLoadingState
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.designsystem.toolbar.XTopAppBarAlignment
import thisissadeghi.send.presentation.SendUiModel
import thisissadeghi.send.presentation.SendViewModel
import thisissadeghi.send.presentation.ui.components.SendBottomBar
import thisissadeghi.send.presentation.ui.components.SuccessContent

@Composable
fun SendScreen(
    viewModel: SendViewModel,
    onBackClick: () -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    SendScreenRoot(
        uiModel = uiModel,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        onSendClick = viewModel::onSendClick,
        onQrScanClick = viewModel::onQrScanClick,
        onAddressChange = viewModel::onAddressChange,
        onPasteClick = viewModel::onPasteClick,
        onQuickAmountClick = { label ->
            when {
                label == "MAX" -> viewModel.onMaxClick()
                else -> viewModel.onPercentClick(label.dropLast(1).toIntOrNull() ?: 0)
            }
        },
        onCoinSelectClick = viewModel::onCoinSelectClick,
        onNetworkSelectClick = viewModel::onNetworkSelectClick,
    )
}

@Composable
fun SendScreenRoot(
    uiModel: SendUiModel,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onSendClick: () -> Unit = {},
    onQrScanClick: () -> Unit = {},
    onAddressChange: (String) -> Unit = {},
    onPasteClick: () -> Unit = {},
    onQuickAmountClick: (String) -> Unit = {},
    onCoinSelectClick: () -> Unit = {},
    onNetworkSelectClick: () -> Unit = {},
) {
    XScreen(
        topBar = {
            XTopAppBar(
                title = {
                    XText(
                        text = stringResource(Res.string.send_title),
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
                is UiState.Success -> SendBottomBar(onSendClick = onSendClick)
                else -> {}
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        when (val state = uiModel.dataState) {
            UiState.Uninitialized -> Box(modifier = Modifier.fillMaxSize())

            UiState.Loading -> AppLoadingState()

            is UiState.Success ->
                SuccessContent(
                    data = state.value,
                    onAddressChange = onAddressChange,
                    onPasteClick = onPasteClick,
                    onQrClick = onQrScanClick,
                    onQuickAmountClick = onQuickAmountClick,
                    onCoinSelectClick = onCoinSelectClick,
                    onNetworkSelectClick = onNetworkSelectClick,
                )

            is UiState.Failed ->
                AppErrorState(
                    title = stringResource(Res.string.error_title),
                    message = stringResource(Res.string.error_message),
                    onRetry = onRetry,
                    secondaryAction = {
                        XTextButton(onClick = onBackClick) {
                            XText(
                                text = stringResource(Res.string.return_to_dashboard),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )
        }
    }
}
