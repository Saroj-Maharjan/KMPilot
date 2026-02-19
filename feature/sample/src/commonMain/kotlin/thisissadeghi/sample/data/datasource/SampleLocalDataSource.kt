package thisissadeghi.sample.data.datasource

import thisissadeghi.sample.data.model.DashboardData

interface SampleLocalDataSource {
    suspend fun getDashboard(): DashboardData
}
