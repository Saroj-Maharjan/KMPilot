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
    private val _uiState = MutableStateFlow(SendUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSendData()
    }

    private fun loadSendData() {
        _uiState.setState { copy(state = UiState.Loading) }
        viewModelScope.launch {
            when (val result = repository.getSendData()) {
                is Either.Success -> {
                    val data = result.data
                    val uiModel =
                        SendUiModel(
                            recipientAddress = data.recipientAddress,
                            amount = data.amount,
                            coinName = data.selectedCoin.name,
                            coinSymbol = data.selectedCoin.symbol,
                            balanceBtc = data.balanceBtc,
                            balanceUsd = data.balanceUsd,
                            networkName = data.selectedNetwork.name,
                            networkDescription = data.selectedNetwork.description,
                            networkFee = data.networkFee,
                            totalDeduct = data.totalDeduct,
                            estimatedArrival = data.estimatedArrival,
                        )
                    _uiState.setState { copy(state = UiState.Success(uiModel)) }
                }
                is Either.Failure -> {
                    _uiState.setState { copy(state = UiState.Failed(result.error)) }
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
