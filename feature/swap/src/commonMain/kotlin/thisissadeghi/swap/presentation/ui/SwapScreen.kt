package thisissadeghi.swap.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpilot.feature.swap.generated.resources.Res
import kmpilot.feature.swap.generated.resources.swap_error_message
import kmpilot.feature.swap.generated.resources.swap_error_title
import kmpilot.feature.swap.generated.resources.swap_review_cta
import kmpilot.feature.swap.generated.resources.swap_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.common.ErrorModel
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScreen
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.app.AppErrorState
import thisissadeghi.designsystem.app.AppLoadingState
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.swap.data.model.SwapAsset
import thisissadeghi.swap.data.model.SwapQuoteResponse
import thisissadeghi.swap.presentation.SwapUiModel
import thisissadeghi.swap.presentation.SwapViewModel
import thisissadeghi.swap.presentation.ui.components.SwapContent

@Composable
fun SwapScreen(
    viewModel: SwapViewModel,
    onBackClick: () -> Unit,
    onSwapComplete: () -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    SwapScreenRoot(
        uiModel = uiModel,
        onBackClick = onBackClick,
        onSwapComplete = onSwapComplete,
        onRetry = viewModel::retry,
        onFromAmountChange = viewModel::onFromAmountChange,
        onMaxClick = viewModel::onMaxClick,
        onSwapDirectionClick = viewModel::onSwapDirectionClick,
        onReviewSwapClick = viewModel::onReviewSwapClick,
        onSlippageClick = {},
    )
}

@Composable
fun SwapScreenRoot(
    uiModel: SwapUiModel,
    onBackClick: () -> Unit,
    onSwapComplete: () -> Unit,
    onRetry: () -> Unit,
    onFromAmountChange: (String) -> Unit,
    onMaxClick: () -> Unit,
    onSwapDirectionClick: () -> Unit,
    onReviewSwapClick: () -> Unit,
    onSlippageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(uiModel.executeState) {
        if (uiModel.executeState is UiState.Success) onSwapComplete()
    }

    XScreen(
        modifier = modifier,
        topBar = {
            XTopAppBar(
                title = { XText(text = stringResource(Res.string.swap_title)) },
                backgroundColor = Color.Transparent,
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
                            painter = painterResource(DesignSystemResources.drawable.arrow_back),
                            contentDescription = stringResource(DesignSystemResources.string.cd_back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (uiModel.quoteState is UiState.Success) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                            .windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
                            .padding(24.dp),
                ) {
                    XButton(
                        onClick = onReviewSwapClick,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                    ) {
                        XText(
                            text = stringResource(Res.string.swap_review_cta),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
        },
    ) {
        when (val quoteState = uiModel.quoteState) {
            is UiState.Uninitialized, UiState.Loading -> {
                AppLoadingState()
            }

            is UiState.Failed -> {
                AppErrorState(
                    title = stringResource(Res.string.swap_error_title),
                    message = stringResource(Res.string.swap_error_message),
                    onRetry = onRetry,
                    secondaryAction = null,
                )
            }

            is UiState.Success -> {
                SwapContent(
                    quote = quoteState.value,
                    fromAmount = uiModel.fromAmount,
                    toAmount = uiModel.toAmount,
                    onFromAmountChange = onFromAmountChange,
                    onMaxClick = onMaxClick,
                    onSwapDirectionClick = onSwapDirectionClick,
                    onSlippageClick = onSlippageClick,
                )
            }
        }
    }
}

private val sampleQuote =
    SwapQuoteResponse(
        fromAsset =
            SwapAsset(
                id = "btc",
                name = "Bitcoin",
                symbol = "BTC",
                avatarUrl = "",
                balance = 0.4821,
            ),
        toAsset =
            SwapAsset(
                id = "eth",
                name = "Ethereum",
                symbol = "ETH",
                avatarUrl = "",
                balance = 8.5994,
            ),
        exchangeRate = 17.84,
        rateDisplay = "1 BTC ≈ 17.84 ETH",
        networkFee = "$7.20",
        slippageTolerance = "0.5%",
        estimatedTotal = "0.4821 BTC",
    )

@Preview
@Composable
private fun SwapScreenRootSuccessPreview() {
    XTheme {
        SwapScreenRoot(
            uiModel =
                SwapUiModel(
                    fromAmount = "",
                    toAmount = "8.5994",
                    quoteState = UiState.Success(sampleQuote),
                ),
            onBackClick = {},
            onSwapComplete = {},
            onRetry = {},
            onFromAmountChange = {},
            onMaxClick = {},
            onSwapDirectionClick = {},
            onReviewSwapClick = {},
            onSlippageClick = {},
        )
    }
}

@Preview
@Composable
private fun SwapScreenRootLoadingPreview() {
    XTheme {
        SwapScreenRoot(
            uiModel = SwapUiModel(quoteState = UiState.Loading),
            onBackClick = {},
            onSwapComplete = {},
            onRetry = {},
            onFromAmountChange = {},
            onMaxClick = {},
            onSwapDirectionClick = {},
            onReviewSwapClick = {},
            onSlippageClick = {},
        )
    }
}

@Preview
@Composable
private fun SwapScreenRootFailedPreview() {
    XTheme {
        SwapScreenRoot(
            uiModel =
                SwapUiModel(
                    quoteState = UiState.Failed(error = ErrorModel.Message("Network error")),
                ),
            onBackClick = {},
            onSwapComplete = {},
            onRetry = {},
            onFromAmountChange = {},
            onMaxClick = {},
            onSwapDirectionClick = {},
            onReviewSwapClick = {},
            onSlippageClick = {},
        )
    }
}
