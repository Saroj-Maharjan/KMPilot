package thisissadeghi.sample.data.datasource

import thisissadeghi.sample.data.model.SampleItem

/**
 * Local data source interface for sample items.
 */
interface SampleLocalDataSource {
    suspend fun getSampleItems(): List<SampleItem>

    suspend fun getSampleItemById(id: String): SampleItem?
}
