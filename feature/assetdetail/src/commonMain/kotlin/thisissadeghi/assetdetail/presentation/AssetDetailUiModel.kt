package thisissadeghi.assetdetail.presentation

import thisissadeghi.assetdetail.data.model.ActivityResponse
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.data.model.BuyOrderResponse
import thisissadeghi.assetdetail.data.model.PriceHistoryResponse
import thisissadeghi.assetdetail.data.model.TopHoldersResponse
import thisissadeghi.common.UiState

data class AssetDetailUiModel(
    val assetId: String = "",
    val selectedPeriod: String = "1D",
    val buyAmountInput: String = "",
    val buySliderValue: Float = 0f,
    val isBuySheetVisible: Boolean = false,
    val detailState: UiState<AssetDetailResponse> = UiState.Uninitialized,
    val priceHistoryState: UiState<PriceHistoryResponse> = UiState.Uninitialized,
    val activityState: UiState<ActivityResponse> = UiState.Uninitialized,
    val holdersState: UiState<TopHoldersResponse> = UiState.Uninitialized,
    val buyState: UiState<BuyOrderResponse> = UiState.Uninitialized,
)
