package thisissadeghi.assetdetail.presentation.ui

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.cd_back
import kmpilot.feature.assetdetail.generated.resources.error_message
import kmpilot.feature.assetdetail.generated.resources.error_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.presentation.AssetDetailUiModel
import thisissadeghi.assetdetail.presentation.AssetDetailViewModel
import thisissadeghi.assetdetail.presentation.ui.components.AssetDetailContent
import thisissadeghi.assetdetail.presentation.ui.components.BuyBottomSheet
import thisissadeghi.assetdetail.presentation.ui.components.StickyTradeBar
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScreen
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.app.AppErrorState
import thisissadeghi.designsystem.app.AppLoadingState
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.designsystem.toolbar.XTopAppBarAlignment

@Composable
fun AssetDetailScreen(
    viewModel: AssetDetailViewModel,
    onBackClick: () -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    AssetDetailScreenRoot(
        uiModel = uiModel,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        onPeriodSelected = viewModel::selectPeriod,
        onBuyClick = viewModel::showBuySheet,
        onSellClick = {},
        onBuySheetDismiss = viewModel::hideBuySheet,
        onBuyAmountChange = viewModel::updateBuyAmount,
        onBuySliderChange = viewModel::updateBuySlider,
        onQuickAmountSelect = viewModel::selectQuickAmount,
        onConfirmBuy = viewModel::confirmBuy,
        onJoinGroupClick = {},
    )
}

@Composable
fun AssetDetailScreenRoot(
    uiModel: AssetDetailUiModel,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onPeriodSelected: (String) -> Unit,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit,
    onBuySheetDismiss: () -> Unit,
    onBuyAmountChange: (String) -> Unit,
    onBuySliderChange: (Float) -> Unit,
    onQuickAmountSelect: (Float) -> Unit,
    onConfirmBuy: () -> Unit,
    onJoinGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val detailState = uiModel.detailState) {
        is UiState.Uninitialized, UiState.Loading -> {
            AppLoadingState()
        }

        is UiState.Failed -> {
            AppErrorState(
                title = stringResource(Res.string.error_title),
                message = stringResource(Res.string.error_message),
                onRetry = onRetry,
                secondaryAction = null,
            )
        }

        is UiState.Success -> {
            val detail = detailState.value

            XScreen(
                modifier = modifier,
                topBar = {
                    XTopAppBar(
                        title = {
                            XText(
                                text = detail.name,
                                style =
                                    MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
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
                                    painter = painterResource(DesignSystemResources.drawable.arrow_back),
                                    contentDescription = stringResource(Res.string.cd_back),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                        backgroundColor = Color.Transparent,
                        alignment = XTopAppBarAlignment.Start,
                    )
                },
                bottomBar = {
                    StickyTradeBar(
                        onSellClick = onSellClick,
                        onBuyClick = onBuyClick,
                    )
                },
            ) {
                AssetDetailContent(
                    uiModel = uiModel,
                    detail = detail,
                    onPeriodSelected = onPeriodSelected,
                    onJoinGroupClick = onJoinGroupClick,
                    modifier = Modifier.matchParentSize(),
                )
            }

            // Buy bottom sheet
            if (uiModel.isBuySheetVisible) {
                BuyBottomSheet(
                    uiModel = uiModel,
                    assetDetail = detail,
                    onDismiss = onBuySheetDismiss,
                    onAmountChange = onBuyAmountChange,
                    onSliderChange = onBuySliderChange,
                    onQuickAmountSelect = onQuickAmountSelect,
                    onConfirm = onConfirmBuy,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AssetDetailScreenRootSuccessPreview() {
    val detail =
        AssetDetailResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "BTC",
            price = 67420.50,
            changePercent24h = 2.34,
            marketCap = 1_300_000_000_000.0,
            volume24h = 28_000_000_000.0,
            circulatingSupply = 19_700_000.0,
            holdingAmount = 0.085,
            holdingFiatValue = 5_730.0,
            currency = "USD",
        )
    XTheme {
        AssetDetailScreenRoot(
            uiModel =
                AssetDetailUiModel(
                    detailState = UiState.Success(detail),
                ),
            onBackClick = {},
            onRetry = {},
            onPeriodSelected = {},
            onBuyClick = {},
            onSellClick = {},
            onBuySheetDismiss = {},
            onBuyAmountChange = {},
            onBuySliderChange = {},
            onQuickAmountSelect = {},
            onConfirmBuy = {},
            onJoinGroupClick = {},
        )
    }
}

@Preview
@Composable
private fun AssetDetailScreenRootLoadingPreview() {
    XTheme {
        AssetDetailScreenRoot(
            uiModel = AssetDetailUiModel(detailState = UiState.Loading),
            onBackClick = {},
            onRetry = {},
            onPeriodSelected = {},
            onBuyClick = {},
            onSellClick = {},
            onBuySheetDismiss = {},
            onBuyAmountChange = {},
            onBuySliderChange = {},
            onQuickAmountSelect = {},
            onConfirmBuy = {},
            onJoinGroupClick = {},
        )
    }
}

@Preview
@Composable
private fun AssetDetailScreenRootFailedPreview() {
    XTheme {
        AssetDetailScreenRoot(
            uiModel =
                AssetDetailUiModel(
                    detailState =
                        UiState.Failed(
                            error = thisissadeghi.common.ErrorModel.Message("Network error"),
                        ),
                ),
            onBackClick = {},
            onRetry = {},
            onPeriodSelected = {},
            onBuyClick = {},
            onSellClick = {},
            onBuySheetDismiss = {},
            onBuyAmountChange = {},
            onBuySliderChange = {},
            onQuickAmountSelect = {},
            onConfirmBuy = {},
            onJoinGroupClick = {},
        )
    }
}
