package thisissadeghi.sample.data.remote

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Resource("/api/finance")
@Serializable
class SampleResources {
    @Resource("/dashboard")
    @Serializable
    class GetDashboard(
        val parent: SampleResources = SampleResources(),
    )
}
