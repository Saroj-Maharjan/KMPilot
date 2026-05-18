package thisissadeghi.dashboard.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.data.remote.network.ktor.ApiClient
import thisissadeghi.data.remote.network.ktor.RequestConfig
import thisissadeghi.dashboard.data.model.DashboardData
import thisissadeghi.dashboard.data.remote.DashboardResources

class DashboardRemoteDataSourceImpl(
    private val apiClient: ApiClient,
) : DashboardRemoteDataSource {
    override suspend fun getDashboard(): Either<DashboardData> =
        apiClient.get(
            resource = DashboardResources.GetDashboard(),
            requestConfigs = RequestConfig.build(userAuthRequired = false),
        )
}
