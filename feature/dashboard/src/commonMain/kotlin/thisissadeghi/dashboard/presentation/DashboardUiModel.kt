package thisissadeghi.dashboard.presentation

import thisissadeghi.common.UiState
import thisissadeghi.dashboard.data.model.DashboardData

data class DashboardUiModel(
    val dashboardState: UiState<DashboardData> = UiState.Uninitialized,
)
