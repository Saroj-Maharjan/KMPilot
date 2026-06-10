package thisissadeghi.assetdetail.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AssetDetailResponse(
    val id: String,
    val name: String,
    val symbol: String,
    val price: Double,
    val changePercent24h: Double,
    val marketCap: Double,
    val volume24h: Double,
    val circulatingSupply: Double,
    val holdingAmount: Double,
    val holdingFiatValue: Double,
    val currency: String,
)

@Serializable
data class PricePoint(
    val timestamp: String,
    val price: Double,
)

@Serializable
data class PriceHistoryResponse(
    val assetId: String,
    val period: String,
    val dataPoints: List<PricePoint>,
)

@Serializable
data class AssetTransaction(
    val id: String,
    val type: String,
    val title: String,
    val timestamp: String,
    val amount: Double,
    val fiatValue: Double,
    val currency: String,
)

@Serializable
data class ActivityResponse(
    val assetId: String,
    val transactions: List<AssetTransaction>,
)

@Serializable
data class HolderAvatar(
    val id: String,
    val initials: String,
    val colorHex: String,
)

@Serializable
data class TopHoldersResponse(
    val assetId: String,
    val holders: List<HolderAvatar>,
    val additionalCount: Int,
)

@Serializable
data class BuyOrderRequest(
    val assetId: String,
    val amount: Double,
    val currency: String,
)

@Serializable
data class BuyOrderResponse(
    val orderId: String,
    val status: String,
)
