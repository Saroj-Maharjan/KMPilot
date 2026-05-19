package thisissadeghi.dashboard.data.repository

import thisissadeghi.common.Either
import thisissadeghi.dashboard.data.datasource.DashboardRemoteDataSource
import thisissadeghi.dashboard.data.model.DashboardData

class DashboardRepositoryImpl(
    private val remoteDataSource: DashboardRemoteDataSource,
) : DashboardRepository {
    override suspend fun getDashboard(): Either<DashboardData> = remoteDataSource.getDashboard()
}
