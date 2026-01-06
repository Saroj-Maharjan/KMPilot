package thisissadeghi.sample.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thisissadeghi.common.ErrorModel
import thisissadeghi.common.UiState
import thisissadeghi.common.setState
import thisissadeghi.sample.data.model.SampleItem
import thisissadeghi.sample.data.repository.SampleRepository

/**
 * ViewModel for the sample feature.
 * Demonstrates the standard ViewModel pattern with UiState handling.
 */
class SampleViewModel(
    private val repository: SampleRepository,
) : ViewModel() {
    private val _uiModelState = MutableStateFlow(SampleUiModel())
    val uiModelState = _uiModelState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        _uiModelState.setState { copy(itemsState = UiState.Loading) }
        viewModelScope.launch {
            try {
                val items = repository.getSampleItems()
                _uiModelState.setState {
                    copy(itemsState = UiState.Success(items))
                }
            } catch (e: Exception) {
                _uiModelState.setState {
                    copy(itemsState = UiState.Failed(ErrorModel.Exception(e)))
                }
            }
        }
    }

    fun onItemClick(item: SampleItem) {
        _uiModelState.setState { copy(selectedItem = item) }
    }

    fun retry() {
        loadItems()
    }
}
