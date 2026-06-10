package thisissadeghi.swap.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thisissadeghi.common.Either
import thisissadeghi.common.UiState
import thisissadeghi.common.ext.formatDecimals
import thisissadeghi.common.setState
import thisissadeghi.swap.data.model.SwapExecuteRequest
import thisissadeghi.swap.data.repository.SwapRepository

class SwapViewModel(
    private val repository: SwapRepository,
) : ViewModel() {
    private val _uiModel = MutableStateFlow(SwapUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        loadQuote()
    }

    private fun loadQuote(
        fromAssetId: String? = null,
        toAssetId: String? = null,
    ) {
        viewModelScope.launch {
            _uiModel.setState { copy(quoteState = UiState.Loading, fromAmount = "", toAmount = "") }
            when (val result = repository.getSwapQuote(fromAssetId, toAssetId)) {
                is Either.Success ->
                    _uiModel.setState {
                        copy(
                            quoteState = UiState.Success(result.data),
                            toAmount =
                                result.data.toAsset.balance
                                    .formatDecimals(4),
                        )
                    }
                is Either.Failure -> _uiModel.setState { copy(quoteState = UiState.Failed(result.error)) }
            }
        }
    }

    fun onFromAmountChange(value: String) {
        val quote = (_uiModel.value.quoteState as? UiState.Success)?.value ?: return
        val amount = value.toDoubleOrNull()
        _uiModel.setState {
            copy(
                fromAmount = value,
                toAmount =
                    if (amount != null) {
                        (amount * quote.exchangeRate).formatDecimals(4)
                    } else {
                        quote.toAsset.balance.formatDecimals(4)
                    },
            )
        }
    }

    fun onMaxClick() {
        val quote = (_uiModel.value.quoteState as? UiState.Success)?.value ?: return
        onFromAmountChange(quote.fromAsset.balance.formatDecimals(4))
    }

    fun onSwapDirectionClick() {
        val quote = (_uiModel.value.quoteState as? UiState.Success)?.value ?: return
        loadQuote(fromAssetId = quote.toAsset.id, toAssetId = quote.fromAsset.id)
    }

    fun onReviewSwapClick() {
        val quote = (_uiModel.value.quoteState as? UiState.Success)?.value ?: return
        val amount = _uiModel.value.fromAmount.toDoubleOrNull() ?: return
        viewModelScope.launch {
            _uiModel.setState { copy(executeState = UiState.Loading) }
            val request =
                SwapExecuteRequest(
                    fromAssetId = quote.fromAsset.id,
                    toAssetId = quote.toAsset.id,
                    fromAmount = amount,
                )
            when (val result = repository.executeSwap(request)) {
                is Either.Success -> _uiModel.setState { copy(executeState = UiState.Success(result.data)) }
                is Either.Failure -> _uiModel.setState { copy(executeState = UiState.Failed(result.error)) }
            }
        }
    }

    fun retry() {
        _uiModel.setState { copy(quoteState = UiState.Uninitialized) }
        loadQuote()
    }
}
