package thisissadeghi.sample.presentation

import thisissadeghi.common.UiState
import thisissadeghi.sample.data.model.DashboardData

data class SampleUiModel(
    val dashboardState: UiState<DashboardData> = UiState.Uninitialized,
)
