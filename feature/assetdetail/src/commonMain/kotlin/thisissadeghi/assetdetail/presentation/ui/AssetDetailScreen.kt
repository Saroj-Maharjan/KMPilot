package thisissadeghi.assetdetail.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.action_buy
import kmpilot.feature.assetdetail.generated.resources.action_sell
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
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XOutlinedButton
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

@Composable
private fun StickyTradeBar(
    onSellClick: () -> Unit,
    onBuyClick: () -> Unit,
) {
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .drawBehind {
                    drawLine(
                        color = outlineVariant,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                }.windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
                .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        XOutlinedButton(
            onClick = onSellClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            border =
                androidx.compose.foundation.BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                ),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
        ) {
            XText(
                text = stringResource(Res.string.action_sell),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        XButton(
            onClick = onBuyClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            XText(
                text = stringResource(Res.string.action_buy),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
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
