package thisissadeghi.sample.data.remote

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

/**
 * Ktor Resources for sample feature API endpoints.
 * Base path: /api/sample/
 */
@Resource("/api/sample")
@Serializable
class SampleResources {
    /**
     * GET /api/sample/
     * Fetches all sample items.
     */
    @Resource("")
    @Serializable
    class GetAll(
        val parent: SampleResources = SampleResources(),
    )
}
