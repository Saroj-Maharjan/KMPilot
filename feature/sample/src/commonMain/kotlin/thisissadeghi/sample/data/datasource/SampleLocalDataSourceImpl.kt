package thisissadeghi.sample.data.datasource

import thisissadeghi.sample.data.model.SampleItem

/**
 * Implementation with mock data - replace with real data source for production.
 */
class SampleLocalDataSourceImpl : SampleLocalDataSource {
    override suspend fun getSampleItems(): List<SampleItem> =
        listOf(
            SampleItem(
                id = "1",
                title = "First Item",
                description = "This is a sample item demonstrating the pattern",
            ),
            SampleItem(
                id = "2",
                title = "Second Item",
                description = "Another sample with optional image",
                imageUrl = "https://via.placeholder.com/150",
            ),
            SampleItem(
                id = "3",
                title = "Third Item",
                description = "Demonstrates UiState handling",
            ),
        )

    override suspend fun getSampleItemById(id: String): SampleItem? = getSampleItems().find { it.id == id }
}
