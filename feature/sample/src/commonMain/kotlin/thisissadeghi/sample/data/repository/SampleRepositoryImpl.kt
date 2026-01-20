package thisissadeghi.sample.data.repository

import thisissadeghi.sample.data.datasource.SampleLocalDataSource
import thisissadeghi.sample.data.model.SampleItem

/**
 * Repository implementation using local data source.
 *
 * NOTE: Currently uses mock data from [SampleLocalDataSource] for testing purposes.
 * To switch to remote API:
 * 1. Inject [SampleRemoteDataSource] instead of [SampleLocalDataSource] in DI
 * 2. Update methods to handle Either<T> responses
 * 3. Add error handling for network failures
 *
 * The remote data source infrastructure is already implemented and ready to use.
 */
class SampleRepositoryImpl(
    private val localDataSource: SampleLocalDataSource,
) : SampleRepository {
    override suspend fun getSampleItems(): List<SampleItem> = localDataSource.getSampleItems()

    override suspend fun getSampleItemById(id: String): SampleItem? = localDataSource.getSampleItemById(id)
}
