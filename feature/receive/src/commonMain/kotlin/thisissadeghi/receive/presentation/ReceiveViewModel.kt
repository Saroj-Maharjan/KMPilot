package thisissadeghi.receive.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import thisissadeghi.common.UiState
import thisissadeghi.common.setState

class ReceiveViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.setState {
            copy(
                state =
                    UiState.Success(
                        ReceiveUiModel(
                            coinName = "Bitcoin",
                            networkName = "Bitcoin Network",
                            walletAddress = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
                        ),
                    ),
            )
        }
    }

    fun retry() {}
}
