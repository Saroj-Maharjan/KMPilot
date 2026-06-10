package thisissadeghi.assetdetail.data.repository

import thisissadeghi.assetdetail.data.datasource.AssetDetailLocalDataSource
import thisissadeghi.assetdetail.data.datasource.AssetDetailRemoteDataSource
import thisissadeghi.assetdetail.data.model.ActivityResponse
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.data.model.BuyOrderRequest
import thisissadeghi.assetdetail.data.model.BuyOrderResponse
import thisissadeghi.assetdetail.data.model.PriceHistoryResponse
import thisissadeghi.assetdetail.data.model.TopHoldersResponse
import thisissadeghi.common.Either

class AssetDetailRepositoryImpl(
    private val localDataSource: AssetDetailLocalDataSource,
    private val remoteDataSource: AssetDetailRemoteDataSource,
) : AssetDetailRepository {
    override suspend fun getDetail(assetId: String): Either<AssetDetailResponse> = Either.Success(localDataSource.getDetail(assetId))

    override suspend fun getPriceHistory(
        assetId: String,
        period: String,
    ): Either<PriceHistoryResponse> = Either.Success(localDataSource.getPriceHistory(assetId, period))

    override suspend fun getActivity(assetId: String): Either<ActivityResponse> = Either.Success(localDataSource.getActivity(assetId))

    override suspend fun getHolders(assetId: String): Either<TopHoldersResponse> = Either.Success(localDataSource.getHolders(assetId))

    override suspend fun buyAsset(request: BuyOrderRequest): Either<BuyOrderResponse> = remoteDataSource.postBuyOrder(request)
}
