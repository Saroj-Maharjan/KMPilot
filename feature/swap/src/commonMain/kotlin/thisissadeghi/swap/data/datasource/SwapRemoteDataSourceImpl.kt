package thisissadeghi.swap.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.data.remote.network.ktor.ApiClient
import thisissadeghi.data.remote.network.ktor.RequestConfig
import thisissadeghi.swap.data.model.SwapExecuteRequest
import thisissadeghi.swap.data.model.SwapExecuteResponse
import thisissadeghi.swap.data.remote.SwapResources

class SwapRemoteDataSourceImpl(
    private val apiClient: ApiClient,
) : SwapRemoteDataSource {
    override suspend fun executeSwap(request: SwapExecuteRequest): Either<SwapExecuteResponse> =
        apiClient.post(
            resource = SwapResources.Execute(),
            body = request,
            requestConfigs = RequestConfig.build(userAuthRequired = false),
        )
}
