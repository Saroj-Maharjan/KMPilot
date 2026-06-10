package thisissadeghi.swap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SwapAsset(
    val id: String,
    val name: String,
    val symbol: String,
    val avatarUrl: String,
    val balance: Double,
)

@Serializable
data class SwapQuoteResponse(
    val fromAsset: SwapAsset,
    val toAsset: SwapAsset,
    val exchangeRate: Double,
    val rateDisplay: String,
    val networkFee: String,
    val slippageTolerance: String,
    val estimatedTotal: String,
)

@Serializable
data class SwapExecuteRequest(
    val fromAssetId: String,
    val toAssetId: String,
    val fromAmount: Double,
)

@Serializable
data class SwapExecuteResponse(
    val transactionId: String,
    val status: String,
)
