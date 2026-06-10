package thisissadeghi.swap.presentation

import thisissadeghi.common.UiState
import thisissadeghi.swap.data.model.SwapExecuteResponse
import thisissadeghi.swap.data.model.SwapQuoteResponse

data class SwapUiModel(
    val fromAmount: String = "",
    val toAmount: String = "",
    val quoteState: UiState<SwapQuoteResponse> = UiState.Uninitialized,
    val executeState: UiState<SwapExecuteResponse> = UiState.Uninitialized,
)
