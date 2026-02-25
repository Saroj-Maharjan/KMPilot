package thisissadeghi.send.presentation

import thisissadeghi.common.UiState

data class SendUiState(
    val state: UiState<SendUiModel> = UiState.Uninitialized,
)
