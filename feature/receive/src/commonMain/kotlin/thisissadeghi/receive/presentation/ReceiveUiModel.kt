package thisissadeghi.receive.presentation

import thisissadeghi.common.UiState
import thisissadeghi.receive.data.model.ReceiveData

data class ReceiveUiModel(
    val dataState: UiState<ReceiveData> = UiState.Uninitialized,
)
