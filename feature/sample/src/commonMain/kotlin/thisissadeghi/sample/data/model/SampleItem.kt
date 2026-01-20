package thisissadeghi.sample.data.model

import kotlinx.serialization.Serializable

/**
 * Sample data model demonstrating the pattern.
 */
@Serializable
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
)
