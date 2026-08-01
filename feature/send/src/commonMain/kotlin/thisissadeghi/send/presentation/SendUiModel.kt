package thisissadeghi.send.presentation

import thisissadeghi.common.UiState
import thisissadeghi.send.data.model.SendData

data class SendUiModel(
    val dataState: UiState<SendData> = UiState.Uninitialized,
)
