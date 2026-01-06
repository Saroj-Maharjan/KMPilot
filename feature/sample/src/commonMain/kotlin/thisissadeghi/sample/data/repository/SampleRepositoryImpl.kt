package thisissadeghi.sample.data.repository

import thisissadeghi.sample.data.datasource.SampleLocalDataSource
import thisissadeghi.sample.data.model.SampleItem

/**
 * Repository implementation using local data source.
 */
class SampleRepositoryImpl(
    private val localDataSource: SampleLocalDataSource,
) : SampleRepository {
    override suspend fun getSampleItems(): List<SampleItem> = localDataSource.getSampleItems()

    override suspend fun getSampleItemById(id: String): SampleItem? = localDataSource.getSampleItemById(id)
}
