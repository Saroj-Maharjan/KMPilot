package thisissadeghi.dashboard.data.repository

import thisissadeghi.dashboard.data.datasource.DashboardLocalDataSource
import thisissadeghi.dashboard.data.model.DashboardData

class DashboardRepositoryImpl(
    private val localDataSource: DashboardLocalDataSource,
) : DashboardRepository {
    override suspend fun getDashboard(): DashboardData = localDataSource.getDashboard()
}
