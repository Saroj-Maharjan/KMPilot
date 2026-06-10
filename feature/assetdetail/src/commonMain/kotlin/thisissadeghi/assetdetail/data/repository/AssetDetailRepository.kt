package thisissadeghi.assetdetail.data.repository

import thisissadeghi.assetdetail.data.model.ActivityResponse
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.data.model.BuyOrderRequest
import thisissadeghi.assetdetail.data.model.BuyOrderResponse
import thisissadeghi.assetdetail.data.model.PriceHistoryResponse
import thisissadeghi.assetdetail.data.model.TopHoldersResponse
import thisissadeghi.common.Either

interface AssetDetailRepository {
    suspend fun getDetail(assetId: String): Either<AssetDetailResponse>

    suspend fun getPriceHistory(
        assetId: String,
        period: String,
    ): Either<PriceHistoryResponse>

    suspend fun getActivity(assetId: String): Either<ActivityResponse>

    suspend fun getHolders(assetId: String): Either<TopHoldersResponse>

    suspend fun buyAsset(request: BuyOrderRequest): Either<BuyOrderResponse>
}
