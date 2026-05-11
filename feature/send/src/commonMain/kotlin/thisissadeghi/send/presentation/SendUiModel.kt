package thisissadeghi.send.presentation

data class SendUiModel(
    val recipientAddress: String,
    val amount: String,
    val coinName: String,
    val coinSymbol: String,
    val balanceBtc: String,
    val balanceUsd: String,
    val networkName: String,
    val networkDescription: String,
    val networkFee: String,
    val totalDeduct: String,
    val estimatedArrival: String,
)
