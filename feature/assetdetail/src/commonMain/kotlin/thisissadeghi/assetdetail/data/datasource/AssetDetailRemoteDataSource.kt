package thisissadeghi.assetdetail.data.datasource

import thisissadeghi.assetdetail.data.model.BuyOrderRequest
import thisissadeghi.assetdetail.data.model.BuyOrderResponse
import thisissadeghi.common.Either

interface AssetDetailRemoteDataSource {
    suspend fun postBuyOrder(request: BuyOrderRequest): Either<BuyOrderResponse>
}
