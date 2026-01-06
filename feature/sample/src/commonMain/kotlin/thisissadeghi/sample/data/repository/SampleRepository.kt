package thisissadeghi.sample.data.repository

import thisissadeghi.sample.data.model.SampleItem

/**
 * Repository interface for sample items.
 */
interface SampleRepository {
    suspend fun getSampleItems(): List<SampleItem>

    suspend fun getSampleItemById(id: String): SampleItem?
}
