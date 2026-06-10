package thisissadeghi.swap.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.swap.data.model.SwapExecuteRequest
import thisissadeghi.swap.data.model.SwapExecuteResponse

interface SwapRemoteDataSource {
    suspend fun executeSwap(request: SwapExecuteRequest): Either<SwapExecuteResponse>
}
