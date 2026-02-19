package thisissadeghi.sample.data.repository

import thisissadeghi.sample.data.datasource.SampleLocalDataSource
import thisissadeghi.sample.data.model.DashboardData

class SampleRepositoryImpl(
    private val localDataSource: SampleLocalDataSource,
) : SampleRepository {
    override suspend fun getDashboard(): DashboardData = localDataSource.getDashboard()
}
