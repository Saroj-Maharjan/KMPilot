package thisissadeghi.dashboard.data.repository

import thisissadeghi.common.Either
import thisissadeghi.dashboard.data.model.DashboardData

interface DashboardRepository {
    suspend fun getDashboard(): Either<DashboardData>
}
