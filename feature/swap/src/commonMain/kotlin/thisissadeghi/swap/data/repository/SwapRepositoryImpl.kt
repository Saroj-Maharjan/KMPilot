package thisissadeghi.swap.data.repository

import thisissadeghi.common.Either
import thisissadeghi.swap.data.datasource.SwapLocalDataSource
import thisissadeghi.swap.data.datasource.SwapRemoteDataSource
import thisissadeghi.swap.data.model.SwapExecuteRequest
import thisissadeghi.swap.data.model.SwapExecuteResponse
import thisissadeghi.swap.data.model.SwapQuoteResponse

class SwapRepositoryImpl(
    private val localDataSource: SwapLocalDataSource,
    private val remoteDataSource: SwapRemoteDataSource,
) : SwapRepository {
    override suspend fun getSwapQuote(
        fromAssetId: String?,
        toAssetId: String?,
    ): Either<SwapQuoteResponse> = Either.Success(localDataSource.getSwapQuote(fromAssetId, toAssetId))

    override suspend fun executeSwap(request: SwapExecuteRequest): Either<SwapExecuteResponse> = remoteDataSource.executeSwap(request)
}
