package thisissadeghi.sample.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.sample.data.model.DashboardData

interface SampleRemoteDataSource {
    suspend fun getDashboard(): Either<DashboardData>
}
