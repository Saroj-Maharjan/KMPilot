package thisissadeghi.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thisissadeghi.common.ErrorModel
import thisissadeghi.common.UiState
import thisissadeghi.common.setState
import thisissadeghi.dashboard.data.repository.DashboardRepository

class DashboardViewModel(
    private val repository: DashboardRepository,
) : ViewModel() {
    private val _uiModelState = MutableStateFlow(DashboardUiModel())
    val uiModelState = _uiModelState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        _uiModelState.setState { copy(dashboardState = UiState.Loading) }
        viewModelScope.launch {
            try {
                val data = repository.getDashboard()
                _uiModelState.setState {
                    copy(dashboardState = UiState.Success(data))
                }
            } catch (e: Exception) {
                _uiModelState.setState {
                    copy(dashboardState = UiState.Failed(ErrorModel.Exception(e)))
                }
            }
        }
    }

    fun retry() {
        loadDashboard()
    }
}
