package thisissadeghi.send.data.model

data class SendData(
    val recipientAddress: String,
    val amount: String,
    val selectedCoin: CoinInfo,
    val balanceBtc: String,
    val balanceUsd: String,
    val selectedNetwork: NetworkInfo,
    val networkFee: String,
    val totalDeduct: String,
    val estimatedArrival: String,
)
