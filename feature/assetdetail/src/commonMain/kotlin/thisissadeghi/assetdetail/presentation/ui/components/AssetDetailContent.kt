package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import thisissadeghi.assetdetail.data.model.ActivityResponse
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.data.model.AssetTransaction
import thisissadeghi.assetdetail.data.model.HolderAvatar
import thisissadeghi.assetdetail.data.model.PriceHistoryResponse
import thisissadeghi.assetdetail.data.model.PricePoint
import thisissadeghi.assetdetail.data.model.TopHoldersResponse
import thisissadeghi.assetdetail.presentation.AssetDetailUiModel
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.motion.RevealOnAppear
import thisissadeghi.designsystem.motion.rememberReducedMotion

@Composable
fun AssetDetailContent(
    uiModel: AssetDetailUiModel,
    detail: AssetDetailResponse,
    onPeriodSelected: (String) -> Unit,
    onJoinGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberReducedMotion()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 128.dp),
    ) {
        item {
            if (reducedMotion) {
                HeroSection(detail = detail)
            } else {
                RevealOnAppear(delayMillis = 0) {
                    HeroSection(detail = detail)
                }
            }
        }

        item {
            if (reducedMotion) {
                PriceChart(priceHistoryState = uiModel.priceHistoryState)
            } else {
                RevealOnAppear(delayMillis = 80) {
                    PriceChart(priceHistoryState = uiModel.priceHistoryState)
                }
            }
        }

        item {
            if (reducedMotion) {
                TimePeriodSelector(
                    selectedPeriod = uiModel.selectedPeriod,
                    onPeriodSelected = onPeriodSelected,
                )
            } else {
                RevealOnAppear(delayMillis = 160) {
                    TimePeriodSelector(
                        selectedPeriod = uiModel.selectedPeriod,
                        onPeriodSelected = onPeriodSelected,
                    )
                }
            }
        }

        item {
            if (reducedMotion) {
                StatsGrid(detail = detail)
            } else {
                RevealOnAppear(delayMillis = 240) {
                    StatsGrid(detail = detail)
                }
            }
        }

        item {
            if (reducedMotion) {
                ActivitySection(activityState = uiModel.activityState)
            } else {
                RevealOnAppear(delayMillis = 320) {
                    ActivitySection(activityState = uiModel.activityState)
                }
            }
        }

        item {
            if (reducedMotion) {
                TopHoldersSection(
                    holdersState = uiModel.holdersState,
                    onJoinGroupClick = onJoinGroupClick,
                )
            } else {
                RevealOnAppear(delayMillis = 400) {
                    TopHoldersSection(
                        holdersState = uiModel.holdersState,
                        onJoinGroupClick = onJoinGroupClick,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AssetDetailContentPreview() {
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
        AssetDetailContent(
            uiModel =
                AssetDetailUiModel(
                    detailState = UiState.Success(detail),
                    priceHistoryState =
                        UiState.Success(
                            PriceHistoryResponse(
                                assetId = "bitcoin",
                                period = "1D",
                                dataPoints =
                                    listOf(
                                        PricePoint("09:00", 65000.0),
                                        PricePoint("15:00", 67420.0),
                                    ),
                            ),
                        ),
                    activityState =
                        UiState.Success(
                            ActivityResponse(
                                assetId = "bitcoin",
                                transactions =
                                    listOf(
                                        AssetTransaction("1", "received", "Received", "Today", 0.012, 810.0, "BTC"),
                                    ),
                            ),
                        ),
                    holdersState =
                        UiState.Success(
                            TopHoldersResponse(
                                assetId = "bitcoin",
                                holders = listOf(HolderAvatar("1", "AK", "#4A90D9")),
                                additionalCount = 42,
                            ),
                        ),
                ),
            detail = detail,
            onPeriodSelected = {},
            onJoinGroupClick = {},
        )
    }
}
