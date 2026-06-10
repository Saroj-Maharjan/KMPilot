package thisissadeghi.assetdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thisissadeghi.assetdetail.data.model.BuyOrderRequest
import thisissadeghi.assetdetail.data.repository.AssetDetailRepository
import thisissadeghi.common.Either
import thisissadeghi.common.UiState
import thisissadeghi.common.setState

class AssetDetailViewModel(
    private val repository: AssetDetailRepository,
    private val assetId: String,
) : ViewModel() {
    private val _uiModel = MutableStateFlow(AssetDetailUiModel(assetId = assetId))
    val uiModel = _uiModel.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() {
        viewModelScope.launch { loadDetail() }
        viewModelScope.launch { loadPriceHistory(_uiModel.value.selectedPeriod) }
        viewModelScope.launch { loadActivity() }
        viewModelScope.launch { loadHolders() }
    }

    private suspend fun loadDetail() {
        _uiModel.setState { copy(detailState = UiState.Loading) }
        when (val result = repository.getDetail(assetId)) {
            is Either.Success -> _uiModel.setState { copy(detailState = UiState.Success(result.data)) }
            is Either.Failure -> _uiModel.setState { copy(detailState = UiState.Failed(result.error)) }
        }
    }

    private suspend fun loadPriceHistory(period: String) {
        _uiModel.setState { copy(selectedPeriod = period, priceHistoryState = UiState.Loading) }
        when (val result = repository.getPriceHistory(assetId, period)) {
            is Either.Success -> _uiModel.setState { copy(priceHistoryState = UiState.Success(result.data)) }
            is Either.Failure -> _uiModel.setState { copy(priceHistoryState = UiState.Failed(result.error)) }
        }
    }

    private suspend fun loadActivity() {
        _uiModel.setState { copy(activityState = UiState.Loading) }
        when (val result = repository.getActivity(assetId)) {
            is Either.Success -> _uiModel.setState { copy(activityState = UiState.Success(result.data)) }
            is Either.Failure -> _uiModel.setState { copy(activityState = UiState.Failed(result.error)) }
        }
    }

    private suspend fun loadHolders() {
        _uiModel.setState { copy(holdersState = UiState.Loading) }
        when (val result = repository.getHolders(assetId)) {
            is Either.Success -> _uiModel.setState { copy(holdersState = UiState.Success(result.data)) }
            is Either.Failure -> _uiModel.setState { copy(holdersState = UiState.Failed(result.error)) }
        }
    }

    fun selectPeriod(period: String) {
        viewModelScope.launch { loadPriceHistory(period) }
    }

    fun updateBuyAmount(input: String) {
        val amount = input.toDoubleOrNull() ?: 0.0
        val maxBalance = (_uiModel.value.detailState as? UiState.Success)?.value?.holdingFiatValue ?: 1000.0
        val sliderValue = if (maxBalance > 0) (amount / maxBalance).coerceIn(0.0, 1.0).toFloat() else 0f
        _uiModel.setState { copy(buyAmountInput = input, buySliderValue = sliderValue) }
    }

    fun updateBuySlider(value: Float) {
        val maxBalance = (_uiModel.value.detailState as? UiState.Success)?.value?.holdingFiatValue ?: 1000.0
        val amount = value.toDouble() * maxBalance
        _uiModel.setState {
            copy(
                buySliderValue = value,
                buyAmountInput = "%.2f".format(amount),
            )
        }
    }

    fun selectQuickAmount(percent: Float) {
        updateBuySlider(percent)
    }

    fun showBuySheet() {
        _uiModel.setState { copy(isBuySheetVisible = true) }
    }

    fun hideBuySheet() {
        _uiModel.setState { copy(isBuySheetVisible = false) }
    }

    fun confirmBuy() {
        val amount = _uiModel.value.buyAmountInput.toDoubleOrNull() ?: return
        viewModelScope.launch {
            _uiModel.setState { copy(buyState = UiState.Loading) }
            val request = BuyOrderRequest(assetId = assetId, amount = amount, currency = "USD")
            when (val result = repository.buyAsset(request)) {
                is Either.Success ->
                    _uiModel.setState {
                        copy(
                            buyState = UiState.Success(result.data),
                            isBuySheetVisible = false,
                        )
                    }
                is Either.Failure -> _uiModel.setState { copy(buyState = UiState.Failed(result.error)) }
            }
        }
    }

    fun retry() {
        _uiModel.setState {
            copy(
                detailState = UiState.Uninitialized,
                priceHistoryState = UiState.Uninitialized,
                activityState = UiState.Uninitialized,
                holdersState = UiState.Uninitialized,
            )
        }
        loadAll()
    }
}
