package thisissadeghi.sample.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.sample.data.model.SampleItem

/**
 * Remote data source for sample items via API.
 */
interface SampleRemoteDataSource {
    /**
     * Fetches sample items from remote API.
     *
     * Contract:
     * - Returns Either<List<SampleItem>>
     * - Left: ErrorModel on failure
     * - Right: List of sample items (may be empty)
     * - Suspending operation (network call)
     */
    suspend fun getSampleItems(): Either<List<SampleItem>>
}
