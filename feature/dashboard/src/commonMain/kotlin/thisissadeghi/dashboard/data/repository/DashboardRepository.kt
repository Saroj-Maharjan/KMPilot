package thisissadeghi.dashboard.data.repository

import thisissadeghi.dashboard.data.model.DashboardData

interface DashboardRepository {
    suspend fun getDashboard(): DashboardData
}
