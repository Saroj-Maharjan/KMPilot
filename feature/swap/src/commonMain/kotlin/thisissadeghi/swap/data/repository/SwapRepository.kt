package thisissadeghi.swap.data.repository

import thisissadeghi.common.Either
import thisissadeghi.swap.data.model.SwapExecuteRequest
import thisissadeghi.swap.data.model.SwapExecuteResponse
import thisissadeghi.swap.data.model.SwapQuoteResponse

interface SwapRepository {
    suspend fun getSwapQuote(
        fromAssetId: String? = null,
        toAssetId: String? = null,
    ): Either<SwapQuoteResponse>

    suspend fun executeSwap(request: SwapExecuteRequest): Either<SwapExecuteResponse>
}
