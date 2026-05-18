package thisissadeghi.dashboard.data.datasource

import thisissadeghi.dashboard.data.model.DashboardData

interface DashboardLocalDataSource {
    suspend fun getDashboard(): DashboardData
}
