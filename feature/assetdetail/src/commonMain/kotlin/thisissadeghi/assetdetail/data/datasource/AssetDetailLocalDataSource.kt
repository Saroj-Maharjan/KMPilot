package thisissadeghi.assetdetail.data.datasource

import thisissadeghi.assetdetail.data.model.ActivityResponse
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.data.model.PriceHistoryResponse
import thisissadeghi.assetdetail.data.model.TopHoldersResponse

interface AssetDetailLocalDataSource {
    suspend fun getDetail(assetId: String): AssetDetailResponse

    suspend fun getPriceHistory(
        assetId: String,
        period: String,
    ): PriceHistoryResponse

    suspend fun getActivity(assetId: String): ActivityResponse

    suspend fun getHolders(assetId: String): TopHoldersResponse
}
