package thisissadeghi.sample.presentation

import thisissadeghi.common.UiState
import thisissadeghi.sample.data.model.SampleItem

/**
 * UI model for the sample feature.
 * Demonstrates the 4-state UI pattern: Uninitialized, Loading, Success, Failed.
 */
data class SampleUiModel(
    val itemsState: UiState<List<SampleItem>> = UiState.Uninitialized,
    val selectedItem: SampleItem? = null,
)
