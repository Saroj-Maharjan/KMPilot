package thisissadeghi.sample.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.data.remote.network.ktor.ApiClient
import thisissadeghi.data.remote.network.ktor.RequestConfig
import thisissadeghi.sample.data.model.DashboardData
import thisissadeghi.sample.data.remote.SampleResources

class SampleRemoteDataSourceImpl(
    private val apiClient: ApiClient,
) : SampleRemoteDataSource {
    override suspend fun getDashboard(): Either<DashboardData> =
        apiClient.get(
            resource = SampleResources.GetDashboard(),
            requestConfigs = RequestConfig.build(userAuthRequired = false),
        )
}
