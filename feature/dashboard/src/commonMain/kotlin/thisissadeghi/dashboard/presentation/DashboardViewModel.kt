package thisissadeghi.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thisissadeghi.common.Either
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
            when (val result = repository.getDashboard()) {
                is Either.Success ->
                    _uiModelState.setState {
                        copy(dashboardState = UiState.Success(result.data))
                    }
                is Either.Failure ->
                    _uiModelState.setState {
                        copy(dashboardState = UiState.Failed(result.error))
                    }
            }
        }
    }

    fun retry() {
        loadDashboard()
    }
}
