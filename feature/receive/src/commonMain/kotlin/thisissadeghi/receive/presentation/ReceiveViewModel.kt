package thisissadeghi.receive.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import thisissadeghi.common.UiState
import thisissadeghi.common.setState
import thisissadeghi.receive.data.model.ReceiveData

class ReceiveViewModel : ViewModel() {
    private val _uiModel = MutableStateFlow(ReceiveUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        _uiModel.setState {
            copy(
                dataState =
                    UiState.Success(
                        ReceiveData(
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
