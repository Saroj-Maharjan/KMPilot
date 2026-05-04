package thisissadeghi.receive.presentation

import thisissadeghi.common.UiState

data class ReceiveUiState(
    val state: UiState<ReceiveUiModel> = UiState.Uninitialized,
)
