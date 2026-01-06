package thisissadeghi.sample.data.model

/**
 * Sample data model demonstrating the pattern.
 */
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
)
