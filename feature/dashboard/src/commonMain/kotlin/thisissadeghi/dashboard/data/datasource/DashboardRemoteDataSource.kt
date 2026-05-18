package thisissadeghi.dashboard.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.dashboard.data.model.DashboardData

interface DashboardRemoteDataSource {
    suspend fun getDashboard(): Either<DashboardData>
}
