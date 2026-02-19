package thisissadeghi.sample.data.repository

import thisissadeghi.sample.data.model.DashboardData

interface SampleRepository {
    suspend fun getDashboard(): DashboardData
}
