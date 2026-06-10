package thisissadeghi.assetdetail.data.datasource

import thisissadeghi.assetdetail.data.model.BuyOrderRequest
import thisissadeghi.assetdetail.data.model.BuyOrderResponse
import thisissadeghi.assetdetail.data.remote.AssetDetailResources
import thisissadeghi.common.Either
import thisissadeghi.data.remote.network.ktor.ApiClient
import thisissadeghi.data.remote.network.ktor.RequestConfig

class AssetDetailRemoteDataSourceImpl(
    private val apiClient: ApiClient,
) : AssetDetailRemoteDataSource {
    override suspend fun postBuyOrder(request: BuyOrderRequest): Either<BuyOrderResponse> =
        apiClient.post(
            resource = AssetDetailResources.PostBuy(id = request.assetId),
            body = request,
            requestConfigs = RequestConfig.build(userAuthRequired = false),
        )
}
