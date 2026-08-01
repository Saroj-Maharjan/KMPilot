package thisissadeghi.send.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thisissadeghi.common.Either
import thisissadeghi.common.UiState
import thisissadeghi.common.setState
import thisissadeghi.send.data.repository.SendRepository

class SendViewModel(
    private val repository: SendRepository,
) : ViewModel() {
    private val _uiModel = MutableStateFlow(SendUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        loadSendData()
    }

    private fun loadSendData() {
        _uiModel.setState { copy(dataState = UiState.Loading) }
        viewModelScope.launch {
            when (val result = repository.getSendData()) {
                is Either.Success -> {
                    _uiModel.setState { copy(dataState = UiState.Success(result.data)) }
                }
                is Either.Failure -> {
                    _uiModel.setState { copy(dataState = UiState.Failed(result.error)) }
                }
            }
        }
    }

    fun retry() {
        loadSendData()
    }

    fun onSendClick() = Unit

    fun onRetryClick() {
        retry()
    }

    fun onAddressChange(address: String) = Unit

    fun onPasteClick() = Unit

    fun onPercentClick(percent: Int) = Unit

    fun onMaxClick() = Unit

    fun onCoinSelectClick() = Unit

    fun onNetworkSelectClick() = Unit

    fun onQrScanClick() = Unit
}
