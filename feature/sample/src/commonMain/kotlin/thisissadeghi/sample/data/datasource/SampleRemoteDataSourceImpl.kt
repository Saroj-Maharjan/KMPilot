package thisissadeghi.sample.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.data.remote.network.ktor.ApiClient
import thisissadeghi.data.remote.network.ktor.RequestConfig
import thisissadeghi.sample.data.model.SampleItem
import thisissadeghi.sample.data.remote.SampleResources

/**
 * Remote data source implementation using ApiClient.
 * Communicates with /api/sample/ endpoint.
 */
class SampleRemoteDataSourceImpl(
    private val apiClient: ApiClient,
) : SampleRemoteDataSource {
    override suspend fun getSampleItems(): Either<List<SampleItem>> =
        apiClient.get(
            resource = SampleResources.GetAll(),
            requestConfigs = RequestConfig.build(userAuthRequired = false),
        )
}
